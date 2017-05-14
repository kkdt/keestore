/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto.unit;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import keestore.crypto.CryptoEngine;
import keestore.crypto.CryptoException;
import keestore.crypto.DESedeCryptoEngine;

/**
 * 3DES unit tests.
 * 
 * @author thinh ho
 *
 */
public class DESedeCryptoEngineTest extends KeyCryptoUnitTest {
   private static final String passwordValue = "h3ll0WoR!d12345678901234";
   private static final String algorithm = "DESede";
   
   @Override
   CryptoEngine getCryptoEngine() {
      return new DESedeCryptoEngine();
   }

   @Override
   String getPasswordValue() {
      return passwordValue;
   }

   @Override
   byte[] loadSecretKey() throws Exception {
      SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(algorithm);
      SecretKey loadedKey = keyfactory.generateSecret(new SecretKeySpec(crypto.generateKey(password), 0, cryptoContext.getSecretKey().length, algorithm));
      return loadedKey.getEncoded();
   }
   
   @Test
   public void testReloadSecretKey() throws Exception {
      byte[] anotherSecret = loadSecretKey();
      assertTrue("The initially secret key must not equal the re-loaded key", !Arrays.equals(anotherSecret, cryptoContext.getSecretKey()));
   }
   
   @Test
   public void testLoadSecretKeyToDecrypt () throws Exception {
      // encrypted using the original key
      byte[] encrypted = crypto.encrypt(cryptoContext.getSecretKey(), message.getBytes(charset));
      assertTrue(encrypted != null && encrypted.length > 0);
      
      // decrypt using the other key reference
      byte[] decrypted = crypto.decrypt(loadSecretKey(), encrypted);
      assertTrue(decrypted != null && decrypted.length > 0);
      assertTrue("Decrypted message does not match original message", new String(decrypted, charset).equals(message));
   }
   
   @Test
   public void testStandardDesedeCtrNoPaddingEncryptDecrypt() {
      final String algorithm = "DESede";
      final String cipherTransformation = "DESede/CTR/NoPadding";
      final int ivSize = 8;
      try {
         doSymmetricEncryptDecryptWithInitializingVector(algorithm, passwordValue, cipherTransformation, ivSize);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
   }
   
   @Test
   public void testStandardDesedeCtrPKCS5PaddingEncryptDecrypt() {
      final String algorithm = "DESede";
      final String cipherTransformation = "DESede/CTR/PKCS5Padding";
      final int ivSize = 8;
      try {
         doSymmetricEncryptDecryptWithInitializingVector(algorithm, passwordValue, cipherTransformation, ivSize);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
   }
}
