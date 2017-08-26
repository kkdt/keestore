/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.crypto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import keestore.access.Kee;
import keestore.access.KeeItem;
import keestore.crypto.Crypto;
import keestore.crypto.CryptoEngine;
import keestore.crypto.CryptoException;
import keestore.vault.model.Vault;

/**
 * <p>
 * This component controls how data is displayed in the GUI, how to load and save
 * data to file. 
 * </p>
 * 
 * @author thinh ho
 *
 */
public class VaultCrypto {
    private final CryptoEngine cryptoEngine;
    private final Crypto crypto;
    private String id;
    private final String salt;
    private File registration;
    
    /**
     * <p>
     * Instantiate using the underlying crypto implementation, the secret key,
     * and keypair. The secret key will be encrypted using the public key.
     * </p>
     * 
     * @param cryptoEngine
     * @param secretKey
     * @param publicKey
     * @param privateKey
     */
    public VaultCrypto(CryptoEngine cryptoEngine, byte[] secretKey, PublicKey publicKey, PrivateKey privateKey) {
        byte[] encSecretKey = cryptoEngine.encrypt(publicKey, secretKey);
        this.salt = Crypto.encode(cryptoEngine.randomBytes(8)).get();
        this.cryptoEngine = cryptoEngine;
        this.crypto = new Crypto(encSecretKey, new KeyPair(publicKey, privateKey));
    }
    
    /**
     * <p>
     * Load the registration information.
     * </p>
     * 
     * @param cryptoEngine
     * @param registration
     * @throws IOException
     */
    public VaultCrypto(CryptoEngine cryptoEngine, File registration) throws IOException {
        Charset charset = Charset.forName(CryptoEngine.charSet);
        String json = Files.readAllLines(registration.toPath(), charset).get(0);
        KeeItem _registration = KeeItem.toKeeItem(json);
        PrivateKey privateKey = Crypto.buildPrivateKey(Crypto.decode(_registration.get("privateKey")).get()).get();
        PublicKey publicKey = Crypto.buildPublicKey(Crypto.decode(_registration.get("publicKey")).get()).get();
        byte[] encSecretKey = Crypto.decode(_registration.get("secretKey")).get();
        this.id = _registration.getId();
        this.salt = _registration.get("salt");
        this.cryptoEngine = cryptoEngine;
        this.crypto = new Crypto(encSecretKey, new KeyPair(publicKey, privateKey));
        this.registration = registration;
    }
    
    /**
     * The file to persist all user data.
     * 
     * @return
     */
    public File getVault() {
        return new File(registration.getParentFile(), id);
    }
    
    /**
     * <p>
     * Load the user's datastore:
     * <ol>
     * <li>Decrypt the symmetric key</li>
     * <li>Decrypt the payload using the dectyped symmetric key</li>
     * <li>Verify signature of decrypted payload</li>
     * </ol>
     * Once all is good, turn the json payload into GUI models with each key/value
     * in encoded format.
     * </p>
     * 
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public List<Vault> loadVault() throws IOException {
        List<Vault> data = new ArrayList<>();
        if(getVault().exists()) {
            try {
                Charset charset = Charset.forName(CryptoEngine.charSet);
                String json = Files.readAllLines(getVault().toPath(), charset).get(0);
                JSONParser p = new JSONParser();
                JSONObject obj = (JSONObject)p.parse(json);
                
                String signature = (String)obj.get("signature");
                String encrypted = (String)obj.get("payload");
                byte[] decrypted = cryptoEngine.decrypt(getInternalSecretKey(), Crypto.decode(encrypted).get());
                String payload = new String(decrypted);
                boolean verified = crypto.verify(payload, signature);
                if(!verified) {
                    throw new CryptoException("Invalid signature! Datastore might have been tampered.");
                }
                JSONArray array = (JSONArray)p.parse(payload);
                array.forEach(a -> {
                    JSONObject o = (JSONObject)a;
                    KeeItem item = KeeItem.toKeeItem(o.toJSONString());
                    Vault v = new Vault(item.getName());
                    item.toMap().keySet().forEach(k -> {
                        if(k.equals(item.nameKey()) || k.equals(item.idKey())) {
                            v.put(k, item.get(k));
                        } else {
                            v.put(Crypto.encode(k.getBytes()).get(), Crypto.encode(item.get(k).getBytes()).get());
                        }
                    });
                    data.add(v);
                });
                
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        return data;
    }
    
    /**
     * <p>
     * The secret key is stored encrypted by the user's public key; this is a 
     * helper method to obtain the decrypted secret key.
     * </p>
     * 
     * @return
     */
    private byte[] getInternalSecretKey() {
        return cryptoEngine.decrypt(crypto.getKeyPair().getPrivate(), crypto.getSecretKey());
    }
    
    /**
     * <p>
     * Encryption will take all model(s) and write out the encrypted json to 
     * file in the following format:
     * <pre>
     * {
     *    "payload":(encrypted),
     *    "signature":(signature),
     *    "secretKey":(encrypted symmetric key that was used during encryption)
     * }
     * </pre>
     * The payload and secret key values are in encoded format.
     * </p>
     * 
     * @param models
     */
    @SuppressWarnings("unchecked")
    public void encrypt(List<Kee> models) {
        JSONArray json = new JSONArray();
        models.forEach(d -> {
            json.add(d);
        });
        String jsonpayload = json.toJSONString();
        String signature = crypto.sign(jsonpayload);
        
        byte[] secretKey = getInternalSecretKey();
        byte[] payload = cryptoEngine.encrypt(secretKey, jsonpayload.getBytes());
        
        KeeItem output = new KeeItem("encrypted");
        output.put("signature", signature);
        output.put("payload", Crypto.encode(payload).get());
        output.put("secretKey", Crypto.encode(crypto.getSecretKey()).get());
        FileWriter writer = null;
        try {
            writer = new FileWriter(getVault());
            output.writeJSONString(writer);
        } catch (IOException e) {
            throw new CryptoException("Cannot encrypt file: " + e.getMessage(), e);
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {}
            }
        }
    }
    
    /**
     * <p>
     * Sign the payload.
     * </p>
     * 
     * @param payload
     * @return
     */
    public String sign(String payload) {
        return crypto.sign(payload);
    }
    
    /**
     * <p>
     * The unique identifier of the registration file and is also used as the 
     * filename of the datastore.
     * </p>
     * 
     * @param id
     */
    void setId(String id) {
        this.id = id;
    }
    
    /**
     * <p>
     * The registration file.
     * </p>
     * 
     * @param registration
     */
    void setSource(File registration) {
        this.registration = registration;
    }
    
    /**
     * <p>
     * The registration file.
     * </p>
     * 
     * @return
     */
    public File getSource() {
        return registration;
    }
    
    public String getSalt() {
        return salt;
    }
    
    /**
     * <p>
     * The encoded symmetric key.
     * </p>
     * 
     * @return
     */
    public String getSecretKey() {
        return Crypto.encode(getInternalSecretKey()).get();
    }
    
    /**
     * <p>
     * Determine if the public/private keypair exists.
     * </p>
     * 
     * @return
     */
    public boolean hasPublicPrivateKeys() {
        return crypto.getKeyPair() != null && crypto.getKeyPair().getPrivate() != null 
            && crypto.getKeyPair().getPublic() != null;
    }
    
    /**
     * <p>
     * Build a registration object. This should only be called once and then the
     * registration is saved to file.
     * </p>
     * 
     * @return
     */
    KeeItem getRegistration() {
        KeeItem registration = new KeeItem("registration");
        registration.put("privateKey", Crypto.encode(crypto.getKeyPair().getPrivate().getEncoded()).get());
        registration.put("publicKey", Crypto.encode(crypto.getKeyPair().getPublic().getEncoded()).get());
        registration.put("secretKey", Crypto.encode(crypto.getSecretKey()).get());
        registration.put("salt", salt);
        return registration;
    }
    
}
