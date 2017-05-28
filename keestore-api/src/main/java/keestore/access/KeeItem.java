/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.access;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;

/**
 * <p>
 * Represent an group of related key-value data (strings only). The underlying 
 * storage is a syncrhonized map that can be converted into a json object.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class KeeItem implements Kee, JSONAware, JSONStreamAware 
{
   public static final String ITEMNAME_KEY = "itemName";
   
   private final Map<String, String> storage;
   
   @SuppressWarnings("rawtypes")
   public static KeeItem toKeeItem(String json) {
      if(json == null) {
         throw new IllegalArgumentException("Invalid object for conversion: " + json);
      }
      Object converted = JSONValue.parse(json);
      return new KeeItem((Map)converted);
   }
   
   private KeeItem() {
      storage = Collections.synchronizedMap(new HashMap<>());
   }
   
   /**
    * <p>
    * Must set with an initial non-empty name value.
    * </p>
    * 
    * @param name
    */
   public KeeItem(String name) {
      this();
      if(name == null || name.length() == 0) {
         throw new IllegalArgumentException("Item name must be non-null and non-empty");
      }
      storage.put(ITEMNAME_KEY, name);
   }
   
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public KeeItem(final Map item) {
      this();
      if(item != null) {
         if(!item.containsKey(ITEMNAME_KEY)) {
            throw new IllegalArgumentException("Item missing required field: " + ITEMNAME_KEY);
         }
         item.keySet().forEach(k -> {
            if(!(k instanceof String)) {
               throw new IllegalArgumentException("All key values must be of type String");
            }
         });
         item.values().forEach(v -> {
            if(!(v instanceof String)) {
               throw new IllegalArgumentException("All values in map must be of type String");
            }
         });
         
         storage.putAll(item);
      } else {
         throw new IllegalArgumentException("Cannot create a KeeItem from a null map");
      }
   }
   
   /**
    * <p>
    * A name for this item.
    * </p>
    * 
    * @return
    */
   public String getName() {
      return storage.get(ITEMNAME_KEY);
   }
   
   @Override
   public String getId() {
      return getName();
   }
   
   /**
    * <p>
    * The value mapped to the specified key.
    * </p>
    * 
    * @param key
    * @return
    */
   public String get(String key) {
      return storage.get(key);
   }
   
   /**
    * <p>
    * Add the key-value pair.
    * </p>
    * 
    * @param key
    * @param value
    * @return the previous value, if exist, or null.
    */
   public String put(String key, String value) {
      return storage.put(key, value);
   }
   
   /**
    * <p>
    * All key(s) grouped with this item.
    * </p>
    * 
    * @return
    */
   public Set<String> keys() {
      Set<String> copy = new HashSet<>();
      synchronized(storage) {
         copy.addAll(storage.keySet());
      }
      return copy;
   }
   
   /**
    * <p>
    * All value(s) grouped with this item.
    * </p>
    * 
    * @return
    */
   public Collection<String> values() {
      Collection<String> copy = new HashSet<>();
      synchronized(storage) {
         copy.addAll(storage.values());
      }
      return copy;
   }
   
   /**
    * <p>
    * Total key-value items in this group.
    * </p>
    * 
    * @return
    */
   public int size() {
      return storage.size();
   }
   
   /**
    * <p>
    * Remove the item with the specified key and return its value.
    * </p>
    * 
    * @param key
    * @return
    */
   public String remove(String key) {
      return storage.remove(key);
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
