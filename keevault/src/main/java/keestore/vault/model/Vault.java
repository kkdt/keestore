/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.model;

import org.springframework.util.Assert;

import keestore.access.KeeItem;

/**
 * <p>
 * The main UI model that enforces a vault name upon instantiation.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class Vault extends KeeItem {
    private static final long serialVersionUID = -6017392994844748913L;
    
    /**
     * Must instantiate with a vault name.
     * 
     * @param name
     */
    public Vault(String name) {
        super(name);
        Assert.notNull(name, "Vault name must exist and be non-null");
    }

}
