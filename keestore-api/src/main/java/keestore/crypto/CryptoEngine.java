/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <p>
 * The main crypto engine for encryption and decryption routines.
 * </p>
 * 
 * @author thinh ho
 *
 */
public interface CryptoEngine {
   /**
    * Default character encoding.
    */
   public static final String charSet = "UTF-8";
   /**
    * <p>
    * Generate a random byte array of <code>size</code>.
    * </p>
    * 
    * @param size
    * @return
    */
   public byte[] randomBytes(int size);
   /**
    * <p>
    * Encrypt the specified payload using the specified symmetric key.
    * </p>
    * 
    * @param key
    * @param payload
    * @return
    * @throws CryptoException
    */
   public byte[] encrypt(byte[] key, byte[] payload) throws CryptoException;
   /**
    * <p>
    * Decrypt the specified payload using the specified symmetric key.
    * </p>
    * 
    * @param key
    * @param payload
    * @return
    * @throws CryptoException
    */
   public byte[] decrypt(byte[] key, byte[] payload) throws CryptoException;
   /**
    * <p>
    * Generate a secret/symmetric key given a secret or key value.
    * </p>
    * 
    * @param secret
    * @return
    * @throws CryptoException
    */
   public byte[] generateKey(String secret) throws CryptoException;
   /**
    * <p>
    * Create the new crypto context.
    * </p>
    * 
    * @see <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html">Java Cryptography Architecture</a>
    * @param secret the symmetric key (or secret value).
    * @param keyPairAlgorithm the keypair algorithm (i.e. RSA).
    * @param keyPairSize the keypair key size (i.e. 1024).
    * @return
    * @throws CryptoException
    * 
    */
   public Crypto createCrypto(String secret, String keyPairAlgorithm, int keyPairSize) throws CryptoException;
   /**
    * <p>
    * Encrypt the specified payload using the public key.
    * </p>
    * 
    * @param publicKey
    * @param payload
    * @return
    */
   public byte[] encrypt(PublicKey publicKey, byte[] payload);
   /**
    * <p>
    * Encrypt the specified payload using the private key.
    * </p>
    * 
    * @param privateKey
    * @param payload
    * @return
    */
   public byte[] encrypt(PrivateKey privateKey, byte[] payload);
   /**
    * <p>
    * The algorithm for the symmetric key generation (i.e. DESede).
    * </p>
    * 
    * @return
    */
   public String getSecretKeyAlgorithm();
}
