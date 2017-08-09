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
 * Main crypto engine object to use and abstracts out the underlying crypto
 * implementation.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class KeyCrypto implements CryptoEngine {
    private final CryptoEngine cryptoEngine;

    /**
     * <p>
     * Must initialize with a non-null {@linkplain CryptoEngine}.
     * </p>
     * 
     * @param cryptoEngine
     */
    public KeyCrypto(CryptoEngine cryptoEngine) {
        if (cryptoEngine == null) {
            throw new CryptoException("Crypto Engine must be non-null");
        }
        this.cryptoEngine = cryptoEngine;
    }

    @Override
    public byte[] encrypt(byte[] key, byte[] payload) throws CryptoException {
        return cryptoEngine.encrypt(key, payload);
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] payload) throws CryptoException {
        return cryptoEngine.decrypt(key, payload);
    }

    @Override
    public Crypto createCrypto(String password, String keyPairAlgorithm, int keyPairSize) throws CryptoException {
        return cryptoEngine.createCrypto(password, keyPairAlgorithm, keyPairSize);
    }

    @Override
    public byte[] generateKey(String password) throws CryptoException {
        return cryptoEngine.generateKey(password);
    }

    @Override
    public String getSecretKeyAlgorithm() {
        return cryptoEngine.getSecretKeyAlgorithm();
    }

    @Override
    public byte[] encrypt(PublicKey publicKey, byte[] payload) {
        return cryptoEngine.encrypt(publicKey, payload);
    }

    @Override
    public byte[] encrypt(PrivateKey privateKey, byte[] payload) {
        return cryptoEngine.encrypt(privateKey, payload);
    }

    @Override
    public byte[] randomBytes(int size) {
        return cryptoEngine.randomBytes(size);
    }
}
