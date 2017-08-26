/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.model;

/**
 * <p>
 * An item that can be added to a {@code Vault}; essentially a key-value storage
 * unit to be used in a UI form.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class VaultItem {
    private String key;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
}
