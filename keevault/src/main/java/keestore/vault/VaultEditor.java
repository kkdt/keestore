/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault;

import keestore.access.Kee;

/**
 * Common UI interface for editing a vault.
 * 
 * @author thinh ho
 *
 */
public interface VaultEditor {
    /**
     * The vault that is in editing mode.
     * 
     * @return
     */
    Kee getOriginalVault();
    /**
     * The updated vault name.
     * 
     * @return
     */
    String getVaultName();
};
