/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto.unit;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import keestore.crypto.AesCryptoEngine;
import keestore.crypto.CryptoEngine;
import keestore.crypto.CryptoException;

/**
 * AES unit tests.
 * 
 * @author thinh ho
 *
 */
public class AesCryptoEngineTest extends KeyCryptoUnitTest {
   private static final String algorithm = "AES";
   private static final String passwordValue = "h3ll0WoR!d123456";
   
   @Override
   CryptoEngine getCryptoEngine() {
      return new AesCryptoEngine();
   }

   @Override
   String getPasswordValue() {
      return passwordValue;
   }
   
   @Override
   byte[] loadSecretKey() throws Exception {
      SecretKeySpec spec = new SecretKeySpec(passwordValue.getBytes(CryptoEngine.charSet), algorithm);
      return spec.getEncoded();
   }
   
   @Test
   public void testReloadSecretKey() throws Exception {
      byte[] anotherSecret = loadSecretKey();
      assertTrue("AES secret keys should match if created from the same password value", Arrays.equals(anotherSecret, cryptoContext.getSecretKey()));
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
   public void testStandardAesCtrNoPaddingEncryptDecrypt() {
      final String algorithm = "AES";
      final String cipherTransformation = "AES/CTR/NoPadding";
      final int ivSize = 16;
      try {
         doSymmetricEncryptDecryptWithInitializingVector(algorithm, passwordValue, cipherTransformation, ivSize);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
   }
   
   @Test
   public void testStardardAesCtrPKCS5PaddingEncryptDecrypt() {
      final String algorithm = "AES";
      final String cipherTransformation = "AES/CTR/PKCS5Padding";
      final int ivSize = 16;
      try {
         doSymmetricEncryptDecryptWithInitializingVector(algorithm, passwordValue, cipherTransformation, ivSize);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
   }
   
   @Test
   public void testStardardAesCfbNoPaddingEncryptDecrypt() {
      final String algorithm = "AES";
      final String cipherTransformation = "AES/CFB/NoPadding";
      final int ivSize = 16;
      try {
         doSymmetricEncryptDecryptWithInitializingVector(algorithm, passwordValue, cipherTransformation, ivSize);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
   }
   
   @Test
   public void testStardardAesOfbNoPaddingEncryptDecrypt() {
      final String algorithm = "AES";
      final String cipherTransformation = "AES/OFB/NoPadding";
      final int ivSize = 16;
      try {
         doSymmetricEncryptDecryptWithInitializingVector(algorithm, passwordValue, cipherTransformation, ivSize);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
   }

}
