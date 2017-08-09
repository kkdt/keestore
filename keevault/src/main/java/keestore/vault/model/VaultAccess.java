/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.vault.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import keestore.access.Kee;
import keestore.access.KeeAccess;

/**
 * <p>
 * Data access to a repository of {@code Vault}.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class VaultAccess implements KeeAccess {
    private Map<String, Vault> storage = Collections.synchronizedMap(new HashMap<>());
    
    @Override
    public Kee getKee(String id) {
        return storage.get(id);
    }

    @Override
    public void addKee(Kee item) {
        if(item == null || !(item instanceof Vault)) {
            throw new IllegalArgumentException("Invalid item to add");
        }
        String id = item.getId();
        if(storage.containsKey(id)) {
            throw new IllegalArgumentException("Vault with id " + id + " already exists");
        }
        storage.put(id, (Vault)item);
    }

    @Override
    public void deleteKee(Kee item) {
        synchronized(storage) {
            Iterator<Vault> itr = storage.values().iterator();
            while(itr.hasNext()) {
                Vault current = itr.next();
                if(current.getId().equals(item.getId())) {
                    itr.remove();
                }
            }
        }
    }

    @Override
    public void deleteKee(String id) {
        storage.remove(id);
    }

    @Override
    public void clear() {
        storage.clear();
    }

}
