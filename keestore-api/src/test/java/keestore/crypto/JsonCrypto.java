/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;

public class JsonCrypto {
    private final Crypto crypto;
    
    public JsonCrypto(Crypto crypto) {
        this.crypto = crypto;
    }
    
    public String sign(String payload) {
        PrivateKey pk = crypto.getKeyPair().getPrivate();
        String signature = null;
        try {
            JWSSigner signer = new RSASSASigner(pk);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).build();
            JWSObject jwsObject = new JWSObject(header, new Payload(payload));
            jwsObject.sign(signer);
            signature = jwsObject.serialize();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return signature;
    }
    
    public boolean verify(String payload) {
        boolean valid = false;
        try {
            RSAPublicKey rsaPublic = (RSAPublicKey)crypto.getKeyPair().getPublic();
            JWSObject jwsObject = JWSObject.parse(payload);
            JWSVerifier verifier = new RSASSAVerifier(rsaPublic);
            valid = jwsObject.verify(verifier);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return valid;
    }
}
