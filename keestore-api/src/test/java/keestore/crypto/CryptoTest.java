/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CryptoTest {
    
    @Test
    public void testAESRSA1024JsonCryptoSignature() throws CryptoException, UnsupportedEncodingException {
        int keySize = 16;
        String message = "hello world, my name is peter parker!";
        CryptoEngine engine = new AesCryptoEngine();
        Crypto crypto = engine.createCrypto(new String(engine.randomBytes(keySize), CryptoEngine.charSet), "RSA", 1024);
        JsonCrypto jsonCrypto = new JsonCrypto(crypto);
        String signature = jsonCrypto.sign(message);
        assertTrue(jsonCrypto.verify(signature));
    }
    
    @Test
    public void testDESEDRSA1024JsonCryptoSignature() throws CryptoException, UnsupportedEncodingException {
        int keySize = 16;
        String message = "hello world, my name is peter parker!";
        CryptoEngine engine = new DESedeCryptoEngine();
        Crypto crypto = engine.createCrypto(new String(engine.randomBytes(keySize), CryptoEngine.charSet), "RSA", 1024);
        JsonCrypto jsonCrypto = new JsonCrypto(crypto);
        String signature = jsonCrypto.sign(message);
        assertTrue(jsonCrypto.verify(signature));
    }
    
    @Test
    public void testAESRSA1024CryptoSignature() throws CryptoException, UnsupportedEncodingException {
        int keySize = 16;
        String message = "hello world, my name is peter parker!";
        CryptoEngine engine = new AesCryptoEngine();
        Crypto crypto = engine.createCrypto(new String(engine.randomBytes(keySize), CryptoEngine.charSet), "RSA", 1024);
        String signature = crypto.sign(message);
        assertTrue("Signature does not match (AESRSA1024)", crypto.verify(message, signature));
    }
    
    @Test
    public void testDESEDRSA1024CryptoSignature() throws CryptoException, UnsupportedEncodingException {
        int keySize = 16;
        String message = "hello world, my name is peter parker!";
        CryptoEngine engine = new DESedeCryptoEngine();
        Crypto crypto = engine.createCrypto(new String(engine.randomBytes(keySize), CryptoEngine.charSet), "RSA", 1024);
        String signature = crypto.sign(message);
        assertTrue("Signature does not match (DESEDRSA1024)", crypto.verify(message, signature));
    }
}
