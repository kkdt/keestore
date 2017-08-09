/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'keestore' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package keestore.access;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * {@code KeeItem} unit tests.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class KeeItemTest {
    private KeeItem item;

    @Before
    public void init() {
        item = new KeeItem("test");
        item.put("test1", "value1");
        item.put("test2", "value2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInvalidKeeItemNullItemName() {
        String name = null;
        new KeeItem(name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInvalidKeeItemEmptyItemName() {
        new KeeItem("");
    }

    @Test
    public void testNewKeeItem() {
        String name = "test";
        KeeItem item = new KeeItem(name);
        assertTrue("Expected a new KeeItem with one key-value pair (the name field)",
                item != null && item.size() == 2 && name.equals(item.getName()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyMapConstructor() {
        Map<String, String> empty = new HashMap<>();
        new KeeItem(empty);
    }

    @Test
    public void testMapConstructor() {
        KeeItem item = null;
        Map<Object, Object> map = new HashMap<>();
        map.put("key1", "value1");

        try {
            item = new KeeItem(map);
            assertTrue("Cannot create a KeeItem with a map without a valid 'name' key", false);
        } catch (IllegalArgumentException e) {
        }

        map.put(KeeItem.ITEMNAME_KEY, 1);
        try {
            item = new KeeItem(map);
            assertTrue("Cannot create a KeeItem with non-string value: " + map, false);
        } catch (IllegalArgumentException e) {
            map.put(KeeItem.ITEMNAME_KEY, "keeitem");
        }
        
        map.put(KeeItem.ITEMNAME_ID, 1);
        try {
            item = new KeeItem(map);
            assertTrue("Cannot create a KeeItem with non-string value: " + map, false);
        } catch (IllegalArgumentException e) {
            map.put(KeeItem.ITEMNAME_ID, "keeitemID");
        }

        map.put(1, "hello");
        try {
            item = new KeeItem(map);
            assertTrue("Cannot create a KeeItem with non-string key: " + map, false);
        } catch (IllegalArgumentException e) {
            map.remove(1);
        }

        map.put("key1", 1);
        try {
            item = new KeeItem(map);
            assertTrue("Cannot create a KeeItem with non-string value: " + map, false);
        } catch (IllegalArgumentException e) {
            map.remove("key1");
        }

        map.put("key1", "value1");
        item = new KeeItem(map);
        assertTrue(item != null && item.size() == map.size());
    }

    @Test
    public void testUpdateReferences() {
        Map<Object, Object> map = new HashMap<>();
        map.put(KeeItem.ITEMNAME_KEY, "keeitem");
        map.put(KeeItem.ITEMNAME_ID, "keeitemID");
        map.put("key1", "value1");

        KeeItem item = new KeeItem(map);
        assertTrue(map.size() == item.size());

        map.put("key2", "value2");
        assertTrue("Updating the referenced map should not update the KeeItem",
                item.get("key2") == null && item.size() != map.size());
        map.remove("key2");

        item.put("key2", "value2");
        assertTrue("Updating the KeeItem should not update the original map",
                map.get("key2") == null && item.size() != map.size());
    }

    @Test
    public void testToMap() {
        String newkey = "newkey";
        int size = item.size();
        Map<String, Object> map = item.toMap();
        map.put(newkey, "helloworld");
        assertTrue("Updating the toMap() reference should not modify the original KeeItem",
                item.size() == size && item.get(newkey) == null);
        map.remove(newkey);

        KeeItem itemCopy = new KeeItem(map);
        assertTrue(itemCopy.size() == item.size() && itemCopy.toMap().equals(item.toMap()));
    }

    @Test
    public void testUpdates() {
        int size = item.size();
        String key = "username";

        assertTrue(item.get("nothing") == null);
        assertTrue(item.size() == size && "test".equals(item.getName()));
        item.put(key, "value1");
        size++;
        assertTrue(item.size() == size && "value1".equals(item.get(key)));
        item.remove(key);
        size--;
        assertTrue(item.size() == size && item.get(key) == null);
    }

    @Test
    public void testToJson() {
        KeeItem item = new KeeItem("test");
        item.put("key1", "value1");
        item.put("key2", "value2");
        String json = item.toJSONString();
        assertTrue(json != null && json.length() > 0);

        try {
            KeeItem.toKeeItem(null);
            assertTrue("Expecting IllegalArgumentException: Should not be able to KeeItem.toKeeItem(null) ", false);
        } catch (IllegalArgumentException e) {
        }

        try {
            KeeItem.toKeeItem("");
            assertTrue("Expecting IllegalArgumentException: Should not be able to KeeItem.toKeeItem(null) ", false);
        } catch (IllegalArgumentException e) {
        }

        final KeeItem convertedItem = KeeItem.toKeeItem(json);
        assertTrue(convertedItem.size() == item.size());
        assertTrue("Converted and original items do not have the same keys",
                convertedItem.keys().containsAll(item.keys()));
        convertedItem.keys().forEach(k -> {
            assertTrue("Mismatched key-value for key: " + k + ", converted: " + convertedItem + ", original: " + item,
                    item.get(k) != null && item.get(k).equals(convertedItem.get(k)));
        });
    }
}
