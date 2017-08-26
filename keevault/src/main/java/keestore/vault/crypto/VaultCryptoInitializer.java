/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.crypto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import keestore.access.KeeItem;
import keestore.crypto.Crypto;
import keestore.crypto.CryptoEngine;

/**
 * <p>
 * Initializes the crypto service used in Keevault.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class VaultCryptoInitializer implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = Logger.getLogger(VaultCryptoInitializer.class);
    
    private ApplicationContext applicationContext;
    private String registration;
    private CryptoEngine cryptoEngine;
    private int publicKeySizeBits = 1024;
    
    public void setPublicKeySizeBits(int publicKeySizeBits) {
        this.publicKeySizeBits = publicKeySizeBits;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public void setCryptoEngine(CryptoEngine cryptoEngine) {
        this.cryptoEngine = cryptoEngine;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        File regfile = new File(registration);
        VaultCrypto crypto = null;
        
        try {
            if(regfile.exists()) {
                crypto = new VaultCrypto(cryptoEngine, regfile);
            } else {
                crypto = createVaultCrypto();
                KeeItem registration = crypto.getRegistration();
                crypto.setId(registration.getId());
                outputRegistration(registration, regfile);
                crypto.setSource(regfile);
            }
        } catch (Exception e) {
            logger.error("Cannot initialized Vault Crypto", e);
            throw new IllegalStateException(e);
        }
        
        if(applicationContext != null) {
            logger.info("Vault Crypto initialized, publishing to application context");
            applicationContext.publishEvent(new VaultCryptoInitialized(applicationContext,crypto));
        }
    }
    
    private VaultCrypto createVaultCrypto() {
        VaultCrypto crypto = null;
        try {
           String secret = Crypto.encode(cryptoEngine.generateKey()).get();
           Crypto c = cryptoEngine.createCrypto(secret, "RSA", publicKeySizeBits);
           crypto = new VaultCrypto(cryptoEngine, c.getSecretKey(), c.getKeyPair().getPublic(), c.getKeyPair().getPrivate());
        } catch (Exception e) {
           logger.error("Cannot generate registration: " + e.getMessage(), e);
           throw new IllegalStateException(e);
        }
        return crypto;
     }
    
    private void outputRegistration(KeeItem registration, File regfile) throws IOException {
        Files.createDirectories(Paths.get(regfile.getParentFile().toURI()));
        final List<String> data = new ArrayList<>();
        data.add(registration.toJSONString());
        Files.write(regfile.toPath(), new Iterable<String>() {
           @Override
           public Iterator<String> iterator() {
              return data.iterator();
           }
        });
        logger.info("Registration written to " + regfile.getAbsolutePath());
     }
}
