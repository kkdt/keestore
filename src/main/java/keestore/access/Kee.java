/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.access;

import java.util.Map;

/**
 * <p>
 * The base model for all key-value items.
 * </p>
 * 
 * @author thinh ho
 *
 */
public interface Kee {
   /**
    * <p>
    * The underlying representation is a map with keys expected to be Strings,
    * mapping to Object values.
    * </p>
    * 
    * @return
    */
   Map<String, Object> toMap();
   /**
    * <p>
    * Each {@code Kee} model has a unique identifier.
    * </p>
    * 
    * @return
    */
   String getId();
}
