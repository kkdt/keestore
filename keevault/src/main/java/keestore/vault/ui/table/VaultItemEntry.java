/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.ui.table;

import kkdt.generictable.OrderedColumn;

public class VaultItemEntry {
    @OrderedColumn(index = 0, displayName = "Key", name = "key", toolTip = "Unique key", editable = false, width = 100, type = String.class)
    private String key;
    
    @OrderedColumn(index = 1, displayName = "Value", name = "value", toolTip = "Value", editable = false, width = 100, type = String.class)
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
