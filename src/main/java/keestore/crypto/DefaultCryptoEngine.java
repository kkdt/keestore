/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * Base crypto engine that has a templated pattern for decryption and encryption.
 * The abstract methods determines the implementation-specific characteristics
 * during crypto routines.
 * </p>
 * 
 * @author thinh ho
 *
 */
public abstract class DefaultCryptoEngine implements CryptoEngine {
   /**
    * <p>
    * The cipher algorithm to use (i.e. DESede/CTR/PKCS5Padding).
    * </p>
    * 
    * @return
    */
   protected abstract Cipher getCipher();
   /**
    * TODO: Need to package this along with the encrypted payload because it is
    * required during decryption if used during encryption.
    * 
    * Currently, the same IV is used for both encryption and decryption and is 
    * NOT unique. DO NOT FOLLOW THIS IMPLEMENTATION!
    * 
    * @return
    */
   protected abstract IvParameterSpec createInitializingVector();
   
   @Override
   public byte[] generateKey(String secret) throws CryptoException {
      byte[] key = null;
      try {
         SecretKey spec = new SecretKeySpec(secret.getBytes(CryptoEngine.charSet), getSecretKeyAlgorithm());
         key = spec.getEncoded();
      } catch (Exception e) {
         throw new CryptoException(e);
      }
      return key;
   }
   
   @Override
   public byte[] encrypt(byte[] key, byte[] payload) {
      byte[] encrypted = null;
      SecretKey k = new SecretKeySpec(key, 0, key.length, getSecretKeyAlgorithm());
      try {
         Cipher cipher = getCipher();
         IvParameterSpec iv = createInitializingVector();
         if(iv != null) {
            cipher.init(Cipher.ENCRYPT_MODE, k, iv);
         } else {
            cipher.init(Cipher.ENCRYPT_MODE, k);
         }
         encrypted = cipher.doFinal(payload);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
      return encrypted;
   }

   @Override
   public byte[] decrypt(byte[] key, byte[] payload) throws CryptoException {
      byte[] decrypted = null;
      SecretKey k = new SecretKeySpec(key, 0, key.length, getSecretKeyAlgorithm());
      try {
         Cipher cipher = getCipher();
         IvParameterSpec iv = createInitializingVector();
         if(iv != null) {
            cipher.init(Cipher.DECRYPT_MODE, k, iv);
         } else {
            cipher.init(Cipher.DECRYPT_MODE, k);
         }
         decrypted = cipher.doFinal(payload);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
      return decrypted;
   }
   
   @Override
   public Crypto createCrypto(String secret, String keyPairAlgorithm, int keyPairSize) throws CryptoException {
      Crypto crypto = new Crypto();
      try {
         byte[] secretKey = generateKey(secret);
         crypto.setSecretKey(secretKey);
         KeyPair keypair = generateKeyPair(keyPairAlgorithm, keyPairSize);
         PrivateKey priv = keypair.getPrivate();
         PublicKey pub = keypair.getPublic();
         crypto.setPrivateKey(priv.getEncoded());
         crypto.setPublicKey(pub.getEncoded());
      } catch (Exception e) {
         throw new CryptoException(e);
      }
      return crypto;
   }
   
   @Override
   public byte[] encrypt(PublicKey publicKey, byte[] payload) {
      byte[] encrypted = null;
      try {
         final Cipher encryptCipher = Cipher.getInstance(publicKey.getAlgorithm());
         encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
         encrypted = encryptCipher.doFinal(payload);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
      return encrypted;
   }
   
   @Override
   public byte[] encrypt(PrivateKey privateKey, byte[] payload) {
      byte[] encrypted = null;
      try {
         final Cipher encryptCipher = Cipher.getInstance(privateKey.getAlgorithm());
         encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);
         encrypted = encryptCipher.doFinal(payload);
      } catch (Exception e) {
         throw new CryptoException(e);
      }
      return encrypted;
   }
   
   /**
    * <p>
    * Helper method to generate the {@code KeyPair} using the specified algorithm
    * and key size (number of bits).
    * </p>
    * 
    * @param keyPairAlgorithm
    * @param keyPairSize (number of bits)
    * @return
    * @throws NoSuchAlgorithmException
    */
   protected KeyPair generateKeyPair(String keyPairAlgorithm, int keyPairSize) throws NoSuchAlgorithmException {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyPairAlgorithm);
      keyPairGenerator.initialize(keyPairSize);
      KeyPair keyPair = keyPairGenerator.genKeyPair();
      return keyPair;
   }
}
