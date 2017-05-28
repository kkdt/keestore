/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import java.io.Serializable;

/**
 * <p>
 * Crypto context that holds an instance of the private/symmetric key and the
 * public/private key pair. All values are unencrypted.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class Crypto implements Serializable {
   private static final long serialVersionUID = -8078081637447939945L;
   
   private byte[] privateKey;
   private byte[] publicKey;
   private byte[] secretKey;

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
    * The symmetric key.
    * </p>
    * 
    * @param secretKey
    */
   public void setSecretKey(byte[] secretKey) {
      this.secretKey = secretKey;
   }

   /**
    * <p>
    * The public/private key.
    * </p>
    * 
    * @return
    */
   public byte[] getPublicKey() {
      return publicKey;
   }

   /**
    * <p>
    * The public/private key.
    * </p>
    * 
    * @param publicKey
    */
   public void setPublicKey(byte[] publicKey) {
      this.publicKey = publicKey;
   }

   /**
    * <p>
    * The public/private key.
    * </p>
    * 
    * @return
    */
   public byte[] getPrivateKey() {
      return privateKey;
   }

   /**
    * <p>
    * The public/private key.
    * </p>
    * 
    * @param privateKey
    */
   public void setPrivateKey(byte[] privateKey) {
      this.privateKey = privateKey;
   }
}
