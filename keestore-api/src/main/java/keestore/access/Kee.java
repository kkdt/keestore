/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.access;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * The base model for all key-value items.
 * </p>
 * 
 * @author thinh ho
 *
 */
public interface Kee extends Serializable {
    Object addItem(Kee item);
    void clear();
    /**
     * <p>
     * Add the key-value pair.
     * </p>
     * 
     * @param key
     * @param value
     * @return the previous value, if exist, or null.
     */
    String put(String key, String value);
    /**
     * <p>
     * Remove the item with the specified key and return its value.
     * </p>
     * 
     * @param key
     * @return
     * @throws IllegalArgumentException if the key to remove is the identifier key.
     */
    String remove(String key);
   /**
    * <p>
    * The underlying representation is a map with keys expected to be Strings,
    * mapping to Object values.
    * </p>
    * 
    * @return
    */
   Map<String, Object> toMap();
   Kee toKee();
   Kee toKee(Map<String, Object> data);
   /**
    * <p>
    * Each {@code Kee} model has a unique identifier.
    * </p>
    * 
    * @return
    */
   String getId();
   void setId(String id);
   String idKey();
   /**
    * <p>
    * A human-readable name.
    * </p>
    * 
    * @return
    */
   String getName();
   void setName(String name);
   String nameKey();
}
