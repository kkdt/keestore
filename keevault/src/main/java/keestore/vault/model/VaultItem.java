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
    private boolean encryptKey;
    private boolean encryptValue;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(boolean encryptKey) {
        this.encryptKey = encryptKey;
    }

    public boolean isEncryptValue() {
        return encryptValue;
    }

    public void setEncryptValue(boolean encryptValue) {
        this.encryptValue = encryptValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
}
