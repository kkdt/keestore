/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

/**
 * <p>
 * Triple DES implementation for crypto.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class DESedeCryptoEngine extends DefaultCryptoEngine {
   private static final String algorithm = "DESede";
   private static final String cipherTransform = "DESede/CTR/PKCS5Padding";
   
   @Override
   public String getSecretKeyAlgorithm() {
      return algorithm;
   }
   
   @Override
   public IvParameterSpec createInitializingVector() {
      return new IvParameterSpec(new byte[8]);
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
