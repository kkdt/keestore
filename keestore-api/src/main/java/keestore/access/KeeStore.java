/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.access;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;

/**
 * <p>
 * Represent the storage unit for various sets of {@code KeeItem}.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class KeeStore implements Kee, JSONAware, JSONStreamAware {
   public static final String STOREID_KEY = "storeId";
   public static final String STORENAME_KEY = "storeName";
   
   /**
    * <p>
    * Create a {@code KeeItem} from the specified json string, throwing an 
    * {@code IllegalArgumentException} for invalid data or missing requird fields.
    * </p>
    * 
    * @param json
    * @return
    */
   @SuppressWarnings({ "rawtypes" })
   public static KeeStore toKeeVault(String json) {
      if(json == null) {
         throw new IllegalArgumentException("Invalid object for conversion: " + json);
      }
      Object converted = JSONValue.parse(json);
      Map map = (Map)converted;
      return new KeeStore(map);
   }
   
   private static Set<String> expectedKeys() {
      Set<String> keys = new HashSet<>();
      keys.add(STOREID_KEY);
      keys.add(STORENAME_KEY);
      return keys;
   }
   
   private final Map<String, Object> storage;
   
   /**
    * <p>
    * Internally parse a map and build a {@code KeeVault}, throwing an {@code IllegalArgumentException}
    * if the specified map does not contain required fields or invalid data.
    * </p>
    * 
    * @param map
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   protected KeeStore(Map map) {
      this("");
      if(!map.containsKey(STOREID_KEY)) {
         throw new IllegalArgumentException("Vault identifier not found, key: " + STOREID_KEY);
      }
      
      if(!map.containsKey(STORENAME_KEY)) {
         throw new IllegalArgumentException("Vault name not found, key: " + STORENAME_KEY);
      }
      
      expectedKeys().forEach(k -> {
         if(!(map.get(k) instanceof String)) {
            throw new IllegalArgumentException("Expecting String value for key: " + k);
         }
      });
      
      Set keys = map.keySet();
      keys.forEach(k -> {
         if(!(k instanceof String)) {
            throw new IllegalArgumentException("All keys are expected to be type String: " + k);
         }
         
         Object value = map.get(k);
         if(value instanceof String) {
            storage.put((String)k, (String)map.get(k));
         } else if(value instanceof Map) {
            addItem(new KeeItem((Map)value));
         } else {
            throw new IllegalArgumentException("All values are expected to be type String or Map, key:" + k + ", value:" + value);
         }
      });
   }
   
   /**
    * <p>
    * Empty name vault.
    * </p>
    */
   public KeeStore() {
      this("");
   }
   
   /**
    * <p>
    * Vault with a specified initial name.
    * </p>
    * 
    * @param name
    */
   public KeeStore(String name) {
      storage = Collections.synchronizedMap(new HashMap<>());
      storage.put(STOREID_KEY, UUID.randomUUID().toString());
      setName(name);
   }
   
   /**
    * <p>
    * A unique identifier.
    * </p>
    * 
    * @return
    */
   @Override
   public String getId() {
      return (String)storage.get(STOREID_KEY);
   }

   /**
    * <p>
    * The vault name (human-readable value).
    * </p>
    * 
    * @return
    */
   public String getName() {
      return (String)storage.get(STORENAME_KEY);
   }
   
   /**
    * <p>
    * The vault name (human-readable value).
    * </p>
    * 
    * @param name
    */
   public void setName(String name) {
      storage.put(STORENAME_KEY, name != null ? name : "");
   }
   
   /**
    * <p>
    * Add and overwrite the existin {@code Kee} with the same identifier. This 
    * is equivalent to committing any updates.
    * </p>
    * 
    * @param item
    * @return
    */
   public Object addItem(Kee item) {
      Object previous = null;
      if(item != null && item.getId() != null && item.getId().length() > 0) 
      {
         previous = storage.put(item.getId(), item.toMap());
      }
      return previous;
   }
   
   /**
    * <p>
    * Get a <b><u>copy</u></b> of a {@code KeeItem} entry in this vault. Changes
    * to the returned value (if exist) will not affect the underlying vault. The
    * entry needs to be re-added to commit any updates to the vault.
    * </p>
    * 
    * @param id
    * @return
    */
   @SuppressWarnings("rawtypes")
   public KeeItem getItem(String id) {
      KeeItem item = null;
      if(storage.get(id) instanceof Map) {
         item = new KeeItem((Map)storage.get(id));
      }
      return item;
   }
   
   /**
    * <p>
    * Return a copy of the underlying map. Changes to the returned map should not
    * affect this object, and vice versa.
    * </p>
    * 
    * @return
    */
   @Override
   public Map<String, Object> toMap() {
      Map<String, Object> copy = new HashMap<>();
      synchronized(storage) {
         copy.putAll(storage);
      }
      return copy;
   }
   
   @Override
   public String toJSONString() {
      return JSONObject.toJSONString(storage);
   }

   @Override
   public void writeJSONString(Writer out) throws IOException {
      JSONObject.writeJSONString(storage, out);
   }
   
   @Override
   public String toString() {
      return storage.toString();
   }
}
