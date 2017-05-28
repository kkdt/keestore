/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.access;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import keestore.access.KeeItem;
import keestore.access.KeeStore;

public class KeeStoreTest {
   private static final List<String> keys = Arrays.asList("lastName", "firstName", "ssn");
   private KeeStore vault;
   private KeeItem item1;
   private KeeItem item2;
   
   @Before
   public void init() {
      item1 = new KeeItem("superman");
      item1.put("lastName", "Kent");
      item1.put("firstName", "Clark");
      item1.put("ssn", "111-22-3333");
      
      item2 = new KeeItem("spiderman");
      item2.put("lastName", "Parker");
      item2.put("firstName", "Peter");
      item2.put("ssn", "444-55-6666");
      
      vault = new KeeStore();
      vault.addItem(item1);
      vault.addItem(item2);
   }
   
   @Test
   public void testInitialVault() {
      assertTrue("superman".equals(item1.getName()));
      assertTrue("spiderman".equals(item2.getName()));
      
      assertTrue(vault != null);
      assertTrue(vault.getId() != null);
      assertTrue(vault.getName() != null && "".equals(vault.getName()));
   }
   
   @Test
   public void testToJson() {
      try {
         KeeStore.toKeeVault(null);
         assertTrue("Expecting IllegalArgumentException: Should not be able to KeeVault.toKeeItem(null) ", false);
      } catch (IllegalArgumentException e) {}
      
      Map<String, Object> map = new HashMap<>();
      try {
         KeeStore.toKeeVault(JSONValue.toJSONString(map));
         assertTrue("Expecting IllegalArgumentException: Should not be able to KeeVault.toKeeItem(empty-map) ", false);
      } catch (IllegalArgumentException e) {}
      
      map.putAll(vault.toMap());
      String oldId = (String)map.remove(KeeStore.STOREID_KEY);
      try {
         KeeStore.toKeeVault(JSONValue.toJSONString(map));
         assertTrue("Expecting IllegalArgumentException: Missing required KeeVault.VAULTID_KEY", false);
      } catch (IllegalArgumentException e) {
         map.put(KeeStore.STOREID_KEY, oldId);
      }
      
      String oldName = (String)map.remove(KeeStore.STORENAME_KEY);
      try {
         KeeStore.toKeeVault(JSONValue.toJSONString(map));
         assertTrue("Expecting IllegalArgumentException: Missing required KeeVault.VAULTNAME_KEY", false);
      } catch (IllegalArgumentException e) {
         map.put(KeeStore.STORENAME_KEY, oldName);
      }
      
      String invalidEntry = "hello";
      map.put(invalidEntry, new Integer(1));
      try {
         KeeStore.toKeeVault(JSONValue.toJSONString(map));
         assertTrue("Expecting IllegalArgumentException: Values expecting to be String or Map", false);
      } catch (IllegalArgumentException e) {
         map.remove(invalidEntry);
      }
      
      String json = vault.toJSONString();
      assertTrue(json != null && json.length() > 0);
      KeeStore convertedVault = KeeStore.toKeeVault(json);
      assertTrue(convertedVault != null && convertedVault.toMap().equals(vault.toMap()));
   }
   
   @Test
   public void testOverwriteExistingKeeItem() {
      KeeItem duplicate = new KeeItem("spiderman");
      duplicate.put("lastName", "Brock");
      duplicate.put("firstName", "Eddie");
      duplicate.put("ssn", "123-45-6789");
      Object previous = vault.addItem(duplicate);
      assertTrue(previous != null && previous.equals(item2.toMap()));
   }
   
   @Test
   public void testReferenceUpdate() {
      keys.forEach(k -> {
         item1.put(k, "updated");
      });
      
      keys.forEach(k -> {
         assertTrue("Item reference: Expected key:" + k + "->updated but is " 
            + item1.get(k) , "updated".equals(item1.get(k)));
      });
      
      KeeItem value = vault.getItem(item1.getName());
      assertTrue(value != null && !value.toMap().equals(item1.toMap()));
      keys.forEach(k -> {
         assertTrue("Vault reference: Expected key:" + k + "->" + value.get(k), !"updated".equals(value.get(k)));
      });
   }
}
