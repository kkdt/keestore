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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

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
@Configuration
@PropertySources(value = {
    // user can provide user-specific properties if exist
    @PropertySource(value="file:${user.home}/.keestore/keevault.properties", ignoreResourceNotFound=true) 
})
public class VaultCryptoInitializer implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(VaultCryptoInitializer.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired(required=true)
    private CryptoEngine cryptoEngine;
    
    @Value(value="${user.crypto.registration.file:${crypto.registration.file}}")
    private String registration;
    
    @Value(value="${user.crypto.rsa.keysize:${crypto.rsa.keysize}}")
    private int publicKeySizeBits = 1024;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void doInitialization() throws Exception {
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
            logger.debug("Vault Crypto initialized, publishing to application context");
            applicationContext.publishEvent(new VaultCryptoInitialized(applicationContext,crypto));
        }
    }
    
    @Bean
    public InitializingBean initialzedBean() {
        return () -> {
            doInitialization();
        };
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
        logger.debug("Registration written to " + regfile.getAbsolutePath());
     }
}
