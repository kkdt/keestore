/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

/**
 * <p>
 * Triple DES implementation for crypto (192-bit key or 24 characters).
 * </p>
 * 
 * @author thinh ho
 *
 */
public class DESedeCryptoEngine extends DefaultCryptoEngine {
    private static final String algorithm = "DESede";
    private static final String cipherTransform = "DESede/CTR/PKCS5Padding";
    private static final int IV_LENGTH = 8;

    @Override
    public String getSecretKeyAlgorithm() {
        return algorithm;
    }

    @Override
    public IvParameterSpec createInitializingVector() {
        return new IvParameterSpec(randomBytes(IV_LENGTH));
    }

    @Override
    protected IvParameterSpec extractInitializingVector(byte[] data) {
        if(data == null || data.length < IV_LENGTH) {
            throw new CryptoException("Invalid data for IV extraction");
        }
        return new IvParameterSpec(Arrays.copyOf(data, IV_LENGTH));
    }

    @Override
    protected Cipher getCipher() {
        Cipher c = null;
        try {
            c = Cipher.getInstance(cipherTransform);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return c;
    }

}
