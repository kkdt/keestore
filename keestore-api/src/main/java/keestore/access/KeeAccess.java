/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.access;

/**
 * <p>
 * Data access specification for {@code Kee} models.
 * </p>
 * 
 * @author thinh ho
 *
 */
public interface KeeAccess {
    /**
     * <p>
     * Obtain a {@code Kee} model with the specified identifier.
     * </p>
     * 
     * @param id
     * @return
     */
    Kee getKee(String id);

    /**
     * Add the specified {@code Kee}.
     * 
     * @param item
     */
    void addKee(Kee item);

    /**
     * Delete the specified {@code Kee}.
     * 
     * @param item
     */
    void deleteKee(Kee item);

    /**
     * Delete the {@code Kee} with the specified identifier.
     * 
     * @param id
     */
    void deleteKee(String id);

    /**
     * Clear the underlying data model.
     */
    void clear();
}
