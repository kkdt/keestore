/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault;

import keestore.vault.model.VaultItem;

/**
 * Common UI interface for an item editor/form.
 * 
 * @author thinh ho
 *
 */
public interface VaultItemEditor {
    /**
     * Close the editor.
     */
    void closeEditor();
    /**
     * The captured vault item from input elements.
     * 
     * @return
     */
    VaultItem getVaultItem();
    /**
     * The item that is in editing mode.
     * 
     * @return
     */
    VaultItem getOriginalItem();
}
