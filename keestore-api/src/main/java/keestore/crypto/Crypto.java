/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.Supplier;

/**
 * <p>
 * Crypto context that holds an instance of the private/symmetric key and the
 * public/private key pair.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class Crypto implements Serializable {
    private static final long serialVersionUID = -8078081637447939945L;

    private final KeyPair keyPair;
    private final byte[] secretKey;
    
    public Crypto(byte[] secretKey, KeyPair keyPair) {
        this.secretKey = secretKey;
        this.keyPair = keyPair;
    }
    
    public static Supplier<String> encode(final byte[] k) {
        return () -> Base64.getEncoder().encodeToString(k);
    }
    
    public static Supplier<byte[]> decode(final String k) {
        return () -> Base64.getDecoder().decode(k);
    }
    
    public static Supplier<PublicKey> buildPublicKey(final byte[] encoded) {
        return () -> {
            PublicKey key = null;
            try {
                X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
                key = KeyFactory.getInstance("RSA").generatePublic(spec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new CryptoException(e);
            }
            return key;
        };
    }
    
    public static Supplier<PrivateKey> buildPrivateKey(final byte[] encoded) {
        return () -> {
            PrivateKey key = null;
            try {
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encoded);
                key = KeyFactory.getInstance("RSA").generatePrivate(spec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new CryptoException(e);
            }
            return key;
        };
    }
    
    /**
     * <p>
     * The key pair (pub/priv).
     * </p>
     * 
     * @return
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }
    
    /**
     * <p>
     * The public/private key.
     * </p>
     * 
     * @return
     */
    public byte[] getPublicKey() {
        return keyPair.getPublic().getEncoded();
    }
    
    /**
     * <p>
     * The public/private key.
     * </p>
     * 
     * @return
     */
    public byte[] getPrivateKey() {
        return keyPair.getPrivate().getEncoded();
    }

    /**
     * <p>
     * The symmetric key.
     * </p>
     * 
     * @return
     */
    public byte[] getSecretKey() {
        return secretKey;
    }
    
    /**
     * <p>
     * Sign using SHA256withRSA.
     * </p>
     * 
     * @param payload
     * @return
     */
    public String sign(String payload) {
        return sign(payload, "SHA256withRSA");
    }
    
    /**
     * <p>
     * Signing will use the RSA private key using the specified hashing algorithm.
     * </p>
     * 
     * @param payload
     * @param signatureAlgorithm
     * @return
     */
    public String sign(String payload, String signatureAlgorithm) {
        String value = null;
        try {
            Signature s = Signature.getInstance(signatureAlgorithm);
            s.initSign(keyPair.getPrivate(), new SecureRandom());
            byte[] message = payload.getBytes();
            s.update(message);
            byte[] _signature = s.sign();
            value = encode(_signature).get();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return value;
    }
    
    /**
     * <p>
     * Verify default SHA256withRSA signature and original payload.
     * </p>
     *  
     * @param payload
     * @param signature
     * @return
     */
    public boolean verify(String payload, String signature) {
        return verify(payload, signature, "SHA256withRSA");
    }
    
    /**
     * <p>
     * Verify the signature against the payload and specified hashing algorithm.
     * </p>
     * 
     * @param payload
     * @param signature
     * @param signatureAlgorithm
     * @return
     */
    public boolean verify(String payload, String signature, String signatureAlgorithm) {
        boolean valid = false;
        try {
            Signature s = Signature.getInstance(signatureAlgorithm);
            s.initVerify(keyPair.getPublic());
            s.update(payload.getBytes());
            valid = s.verify(decode(signature).get());
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return valid;
    }
}
