/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * A Spring event that notifies the application that the Vault has been initialized
 * for the current user.
 * 
 * @author thinh ho
 *
 */
public class KeeVaultContextInitialized extends ApplicationContextEvent {
   private static final long serialVersionUID = 8170723725034228957L;
   
   private final VaultContext vaultContext;
   
   public KeeVaultContextInitialized(ApplicationContext source, VaultContext vaultContext) {
      super(source);
      this.vaultContext = vaultContext;
   }

   public VaultContext getVaultContext() {
      return vaultContext;
   }
}
