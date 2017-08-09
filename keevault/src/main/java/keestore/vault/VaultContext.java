/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import keestore.access.KeeItem;
import keestore.crypto.Crypto;
import keestore.crypto.CryptoEngine;

/**
 * Performs the initialization routine when Vault is first run by the current
 * user within Spring.
 * 
 * @author thinh ho
 *
 */
public class VaultContext implements ApplicationContextAware {
   private static final Logger logger = Logger.getLogger(VaultContext.class);
   private static final String contextId = "keevault";
   
   private ApplicationContext applicationContext;
   private String fileLocation;
   private CryptoEngine cryptoEngine;
   private Crypto crypto;
   private byte[] secret;
   private int secretKeySizeBytes = 16;
   
   /**
    * The application secret, symmetric key.
    * 
    * @return
    */
   public byte[] getSecret() {
      return secret;
   }

   public int getSecretKeySizeBytes() {
      return secretKeySizeBytes;
   }

   public void setSecretKeySizeBytes(int secretKeySizeBytes) {
      this.secretKeySizeBytes = secretKeySizeBytes;
   }

   public void setCryptoEngine(CryptoEngine cryptoEngine) {
      this.cryptoEngine = cryptoEngine;
   }

   public void setFileLocation(String fileLocation) {
      this.fileLocation = fileLocation;
   }

   public String getFileLocation() {
      return fileLocation;
   }
   
   public void init() {
      Assert.notNull(fileLocation, "Invalid KeeVault application directory");
      Assert.notNull(cryptoEngine, "Invalid KeeVault application crypto");
      
      try {
         if(getRegistrationLocation().exists()) {
            logger.info("Loading user registration information");
            internalInit();
         } else {
            createRegistration();
         }
      } catch (Exception e) {
         throw new IllegalStateException("Cannot initialize context: " + e.getMessage(), e);
      }
      logger.info("KeeVault context initialized, publishing to application context");
      applicationContext.publishEvent(new KeeVaultContextInitialized(applicationContext, this));
   }
   
   private File getRegistrationLocation() {
      return new File(fileLocation + File.separator + contextId);
   }
   
   public File getRegistrationFile() {
      return new File(getRegistrationLocation().getAbsolutePath() + File.separator + ".registration");
   }
   
   public File getApplicationSecretKeyFile() {
      return new File(getRegistrationLocation().getAbsolutePath() + File.separator + ".secret");
   }
   
   private void internalInit() throws IOException {
      Charset charset = Charset.forName(CryptoEngine.charSet);
      this.secret = Files.readAllBytes(getApplicationSecretKeyFile().toPath());
      String json = Files.readAllLines(getRegistrationFile().toPath(), charset).get(0);
      KeeItem registration = KeeItem.toKeeItem(json);
      crypto = new Crypto();
      crypto.setSecretKey(registration.get("secretKey").getBytes(charset));
      crypto.setPublicKey(registration.get("publicKey").getBytes(charset));
      crypto.setPrivateKey(registration.get("privateKey").getBytes(charset));
   }
   
   private void createRegistration() {
      try {
         KeeItem registration = new KeeItem("registration");
         Files.createDirectories(Paths.get(getRegistrationLocation().toURI()));
         
         String secret = new String(cryptoEngine.randomBytes(secretKeySizeBytes), CryptoEngine.charSet);
         Crypto c = cryptoEngine.createCrypto(new String(cryptoEngine.randomBytes(secretKeySizeBytes), CryptoEngine.charSet), "RSA", 1024);
         registration.put("secretKey", new String(c.getSecretKey(), CryptoEngine.charSet));
         registration.put("publicKey", new String(c.getPublicKey(), CryptoEngine.charSet));
         registration.put("privateKey", new String(c.getPrivateKey(), CryptoEngine.charSet));
         
         outputApplicationSecretKey(secret);
         outputRegistration(registration);
      } catch (Exception e) {
         logger.error("Cannot generate registration: " + e.getMessage(), e);
         throw new IllegalStateException(e);
      }
   }

   @Override
   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }
   
   private void outputApplicationSecretKey(String secret) throws IOException {
      final List<String> data = new ArrayList<>();
      data.add(secret);
      Files.write(getApplicationSecretKeyFile().toPath(), new Iterable<String>() {
         @Override
         public Iterator<String> iterator() {
            return data.iterator();
         }
      });
      logger.info("Application secret written to " + getApplicationSecretKeyFile().getAbsolutePath());
   }
   
   private void outputRegistration(KeeItem registration) throws IOException {
      final List<String> data = new ArrayList<>();
      data.add(registration.toJSONString());
      Files.write(getRegistrationFile().toPath(), new Iterable<String>() {
         @Override
         public Iterator<String> iterator() {
            return data.iterator();
         }
      });
      logger.info("Registration written to " + getRegistrationFile().getAbsolutePath());
   }
}
