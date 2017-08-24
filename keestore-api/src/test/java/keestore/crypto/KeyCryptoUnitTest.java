/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Before;
import org.junit.Test;

/**
 * Base unit tests.
 * 
 * @author thinh
 *
 */
public abstract class KeyCryptoUnitTest {
    /**
     * Exception to be thrown around in unit tests.
     *
     */
    protected static final class ExpectedCryptoException extends CryptoException {
        private static final long serialVersionUID = 2176114514661570549L;
    }

    protected static final String charset = "UTF-8";
    protected static final String message = "this is a secret message!";
    protected static final String defaultKeyPairAlgorithm = "RSA";
    protected static final int defaultKeyPairKeySize = 1024;

    protected KeyCrypto crypto;
    protected Crypto cryptoContext;
    protected String email;
    protected String password;

    /**
     * <p>
     * Each unit test is expected to be testing a specific crypto
     * implementation.
     * </p>
     * 
     * @return
     */
    abstract CryptoEngine getCryptoEngine();

    /**
     * <p>
     * The password value since this can be a crypto-dependent value (i.e. the
     * length may be a specific length).
     * </p>
     * 
     * @return
     */
    abstract String getPasswordValue();

    /**
     * <p>
     * Routine for re-loading the secret key outside of the underlying
     * {@code CryptoEngine} implementation.
     * </p>
     * 
     * @return
     * @throws Exception
     */
    abstract byte[] loadSecretKey() throws Exception;

    /**
     * <p>
     * Specific unit tests can override accordingly.
     * </p>
     * 
     * @throws Exception
     */
    void initCryptoContext() throws Exception {
        cryptoContext = crypto.createCrypto(password, defaultKeyPairAlgorithm, defaultKeyPairKeySize);
    }

    /**
     * <p>
     * Unit tests can override if the crypto context has a different
     * implementation.
     * </p>
     * 
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeySpec spec = new X509EncodedKeySpec(cryptoContext.getPublicKey());
        PublicKey publicKey = KeyFactory.getInstance(defaultKeyPairAlgorithm).generatePublic(spec);
        return publicKey;
    }

    /**
     * <p>
     * Unit tests can override if the crypto context has a different
     * implementation.
     * </p>
     * 
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeySpec spec = new PKCS8EncodedKeySpec(cryptoContext.getPrivateKey());
        PrivateKey privateKey = KeyFactory.getInstance(defaultKeyPairAlgorithm).generatePrivate(spec);
        return privateKey;
    }

    @Before
    public void init() throws Exception {
        // known email
        email = "userkeygeneration@test.com";

        // user password is only known to user
        password = new String(getPasswordValue().getBytes(charset), charset);

        // implementation of the crypto
        crypto = new KeyCrypto(getCryptoEngine());

        initCryptoContext();
    }

    @Test
    public void initTest() {
        assertTrue("Unit test must have a CryptoEngine implementation", getCryptoEngine() != null);
        assertTrue(crypto != null);
        assertTrue("Crypto Context not initialized", cryptoContext != null);
        assertTrue(email != null && email.length() > 0);
        assertTrue(password != null && password.length() > 0);
    }

    @Test
    public void testCryptoEngineEncryptDecrypt() throws CryptoException, UnsupportedEncodingException {
        byte[] encrypted = crypto.encrypt(cryptoContext.getSecretKey(), message.getBytes(charset));
        assertTrue(encrypted != null && encrypted.length > 0);

        byte[] decrypted = crypto.decrypt(cryptoContext.getSecretKey(), encrypted);
        assertTrue(decrypted != null && decrypted.length > 0);
        assertTrue("Decrypted message does not match original message", new String(decrypted, charset).equals(message));
    }
    
    @Test
    public void testCryptoEngineGetRandomSecretKey1() {
        byte[] key = crypto.generateKey();
        assertTrue(key != null && key.length > 0);
    }
    
    @Test
    public void testCryptoEngineGetRandomSecretKey2() {
        SecretKey key = crypto.randomKey();
        assertTrue(key != null);
    }
    
    @Test
    public void testCryptoEngineEncryptDecryptRandomSecretKey1() throws CryptoException, UnsupportedEncodingException {
        byte[] key = crypto.generateKey();
        byte[] encrypted = crypto.encrypt(key, message.getBytes(charset));
        assertTrue(encrypted != null && encrypted.length > 0);
        
        byte[] decrypted = crypto.decrypt(key, encrypted);
        assertTrue(decrypted != null && decrypted.length > 0);
        assertTrue("Decrypted message does not match original message", new String(decrypted, charset).equals(message));
    }
    
    @Test
    public void testCryptoEngineEncryptDecryptRandomSecretKey2() throws CryptoException, UnsupportedEncodingException {
        SecretKey key = crypto.randomKey();
        byte[] encrypted = crypto.encrypt(key.getEncoded(), message.getBytes(charset));
        assertTrue(encrypted != null && encrypted.length > 0);
        
        byte[] decrypted = crypto.decrypt(key.getEncoded(), encrypted);
        assertTrue(decrypted != null && decrypted.length > 0);
        assertTrue("Decrypted message does not match original message", new String(decrypted, charset).equals(message));
    }

    @Test(expected = ExpectedCryptoException.class)
    public void testCryptoEngineInvalidKeyDecrypt() throws CryptoException, UnsupportedEncodingException {
        byte[] encrypted = crypto.encrypt(cryptoContext.getSecretKey(), message.getBytes(charset));
        assertTrue(encrypted != null && encrypted.length > 0);

        try {
            byte[] decrypted = crypto.decrypt("invalidcryptosymmetrickey".getBytes(charset), encrypted);
            assertTrue("Decryption should not have gotten this far: " + new String(decrypted), false);
        } catch (Exception e) {
            throw new ExpectedCryptoException();
        }
    }

    @Test
    public void testStandardPublicKeyEncryption() throws Exception {
        // encrypt the message using the public key
        final Cipher encryptCipher = Cipher.getInstance(defaultKeyPairAlgorithm);
        encryptCipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        byte[] encrypted = encryptCipher.doFinal(message.getBytes(charset));
        assertTrue(encrypted != null && encrypted.length > 0);

        // decrypt using private key
        final Cipher decryptCipher = Cipher.getInstance(defaultKeyPairAlgorithm);
        decryptCipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
        byte[] decrypted = decryptCipher.doFinal(encrypted);
        assertTrue(decrypted != null && decrypted.length > 0);
        assertTrue("Decrypted message does not match original message", new String(decrypted, charset).equals(message));
    }
    
    protected SecretKey getSecretKey(String algorithm, String password) throws UnsupportedEncodingException {
        byte[] key = Crypto.decode(password).get();
        SecretKey k = new SecretKeySpec(key, 0, key.length, algorithm);
        return k;
    }

    protected void doSymmetricEncryptDecryptWithInitializingVector(String algorithm, String password,
            String cipherTransformation, int ivLength) throws Exception {
        byte[] encrypted = null;
        byte[] decrypted = null;

        SecretKey k = getSecretKey(algorithm, password);

        // encrypt
        Cipher encryptCipher = Cipher.getInstance(cipherTransformation);
        encryptCipher.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(createInitializingVector(ivLength)));
        encrypted = encryptCipher.doFinal(message.getBytes(charset));

        // decrypt (different IV)
        Cipher cipher1 = Cipher.getInstance(cipherTransformation);
        cipher1.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(createInitializingVector(ivLength)));
        decrypted = cipher1.doFinal(encrypted);
        assertTrue("Decrypted message should not match because a different IV was used during decryption",
                !(new String(decrypted, charset).equals(message)));

        // decrypt (same IV)
        Cipher cipher2 = Cipher.getInstance(cipherTransformation);
        cipher2.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(encryptCipher.getIV()));
        decrypted = cipher2.doFinal(encrypted);
        assertTrue("Decrypted message does not match original message", new String(decrypted, charset).equals(message));

        // decrypt (differet key)
        Cipher cipher3 = Cipher.getInstance(cipherTransformation);
        cipher3.init(Cipher.DECRYPT_MODE, getSecretKey(k.getAlgorithm(), getPasswordValue()),
                new IvParameterSpec(encryptCipher.getIV()));
        decrypted = cipher3.doFinal(encrypted);
        assertTrue("Decrypted message should not match original message",
                !(new String(decrypted, charset).equals(message)));
    }

    protected byte[] createInitializingVector(int size) {
        byte[] unique = new byte[size];
        try {
            SecureRandom.getInstanceStrong().nextBytes(unique);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return unique;
    }
}
