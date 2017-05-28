/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.crypto;

/**
 * <p>
 * Capture all exceptions thrown during crypto operations.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class CryptoException extends RuntimeException {
   private static final long serialVersionUID = -3141700076596904272L;
   
   public CryptoException() {}
   
   public CryptoException(Throwable t) {
      super(t);
   }
   
   public CryptoException(String err, Throwable t) {
      super(err, t);
   }
   
   public CryptoException(String err) {
      super(err);
   }
}
