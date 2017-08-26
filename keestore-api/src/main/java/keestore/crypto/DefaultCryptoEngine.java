/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * Base crypto engine that has a templated pattern for decryption and encryption
 * for symmetric-key crypto.
 * </p>
 * 
 * @author thinh ho
 *
 */
public abstract class DefaultCryptoEngine implements CryptoEngine {
    protected static final SecureRandom random = new SecureRandom();

    /**
     * <p>
     * The cipher algorithm to use (i.e. DESede/CTR/PKCS5Padding).
     * </p>
     * 
     * @return
     */
    protected abstract Cipher getCipher();

    /**
     * <p>
     * The IV is used during encryption and will be appended to the beginning of
     * the encrypted payload.
     * </p>
     * 
     * @return
     */
    protected abstract IvParameterSpec createInitializingVector();

    /**
     * <p>
     * During decryption, the IV is extracted from the encrypted payload.
     * </p>
     * 
     * @param data
     * @return
     */
    protected abstract IvParameterSpec extractInitializingVector(byte[] data);

    @Override
    public byte[] randomBytes(int size) {
        byte[] r = new byte[size];
        random.nextBytes(r);
        return r;
    }

    @Override
    public byte[] generateKey(String secret) throws CryptoException {
        byte[] key = null;
        try {
            SecretKey spec = new SecretKeySpec(Crypto.decode(secret).get(), getSecretKeyAlgorithm());
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
            if (iv != null) {
                cipher.init(Cipher.ENCRYPT_MODE, k, iv);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, k);
            }

            byte[] _encrypted = cipher.doFinal(payload);
            ByteBuffer buffer = ByteBuffer.allocate(_encrypted.length + iv.getIV().length);
            buffer.put(iv.getIV());
            buffer.put(_encrypted);
            encrypted = buffer.array();
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
            IvParameterSpec iv = extractInitializingVector(payload);
            if (iv != null) {
                cipher.init(Cipher.DECRYPT_MODE, k, iv);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, k);
            }
            byte[] _payload = Arrays.copyOfRange(payload, iv.getIV().length, payload.length);
            decrypted = cipher.doFinal(_payload);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return decrypted;
    }

    @Override
    public Crypto createCrypto(String secret, String keyPairAlgorithm, int keyPairSize) throws CryptoException {
        Crypto crypto = null;
        try {
            byte[] secretKey = generateKey(secret);
            KeyPair keypair = generateKeyPair(keyPairAlgorithm, keyPairSize);
            crypto = new Crypto(secretKey, keypair);
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
    
    @Override
    public byte[] decrypt(PrivateKey privateKey, byte[] payload) {
        byte[] decrypted = null;
        try {
            final Cipher decryptCipher = Cipher.getInstance(privateKey.getAlgorithm());
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            decrypted = decryptCipher.doFinal(payload);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return decrypted;
    }
    
    @Override
    public byte[] decrypt(PublicKey publicKey, byte[] payload) {
        byte[] decrypted = null;
        try {
            final Cipher decryptCipher = Cipher.getInstance(publicKey.getAlgorithm());
            decryptCipher.init(Cipher.DECRYPT_MODE, publicKey);
            decrypted = decryptCipher.doFinal(payload);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return decrypted;
    }

    /**
     * <p>
     * Helper method to generate the {@code KeyPair} using the specified
     * algorithm and key size (number of bits).
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
