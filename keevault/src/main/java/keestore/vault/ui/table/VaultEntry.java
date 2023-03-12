/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui.table;

import kkdt.generictable.OrderedColumn;

public class VaultEntry {
    @OrderedColumn(index = 0, displayName = "Id", name = "vaultId", toolTip = "Vault unique identifier", editable = false, width = 50, type = String.class)
    private String id;
    
    @OrderedColumn(index = 1, displayName = "Vault", name = "vault", toolTip = "Vault name", editable = false, width = 100, type = String.class)
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
