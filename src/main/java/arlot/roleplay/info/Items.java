package arlot.roleplay.info;

import arlot.error.GetValueException;
import arlot.math.Basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * The class that is used to contain and document both a list and a map of items.
 */
public class Items {
    // properties
    private final List<Item> items;
    private final Map<String, Item> mappedItems;
    // constructors
    public Items() {
        items = new ArrayList<>();
        mappedItems = new HashMap<>();
    }
    public Items(Collection<Item> listed) {
        items = new ArrayList<>(listed);
        mappedItems = new HashMap<>();
    }
    public Items(Map<String, Item> mapped) {
        items = new ArrayList<>();
        mappedItems = new HashMap<>(mapped);
    }
    public Items(Collection<Item> listed, Map<String, Item> mapped) {
        items = new ArrayList<>(listed);
        mappedItems = new HashMap<>(mapped);
    }
    // methods

    /**
     * Stores an already created item in a list.
     * @param item The item to store.
     * @return The stored item.
     */
    public Item add(Item item) {
        this.items.add(item);
        return item;
    }
    /**
     * Creates a new item to be stored.
     * Stores the item in a list.
     * @param name The name of the item.
     * @param description The details of the item, like what does it do.
     * @param data A plate of stat data where the key is the stat's name.
     * @param tags An array of strings that define the tags the item has.
     */
    public Item add(String name, String description, Map<String, Object> data, String[] tags) {
        Item item = new Item(name, description, data, tags);
        return add(item);
    }
    /**
     * Stores an already created item in a key, value pear.
     * @param key The key to store the item behind.
     * @param item The item to store.
     * @return The stored item.
     */
    public Item add(String key, Item item) throws Exception {
        catchKey(key);
        this.mappedItems.put(key, item);
        return item;
    }
    /**
     * Creates a new item to be stored.
     * Stores the item in a key, value pear.
     * @param key The key to store the item behind.
     * @param name The name of the item.
     * @param description The details of the item, like what does it do.
     * @param data A plate of stat data where the key is the stat's name.
     * @param tags An array of strings that define the tags the item has.
     */
    public Item add(String key, String name, String description, Map<String, Object> data, String[] tags) throws Exception {
        catchKey(key);
        Item item = new Item(name, description, data, tags);
        return add(key, item);
    }

    /**
     * Get an item stored in a list.
     * @param index The item's index position. If greater than list item count, then the index will be lapped until it is within valid limits.
     * @return The item at that index.
     * @throws Exception Throws if the index, after processing, is greater than item count in the list.
     */
    public Item get(int index) throws Exception {
        index = Math.toIntExact(Basic.nortic((long) index, (long) items.size()));
        if (index>items.size()-1) {
            throw new GetValueException("An unexpected error caused the index to be greater than amount of items stored: "+index);
        }
        return items.get(index);
    }

    /**
     * Get an item stored in a key, value pear.
     * @param key The key given to an item.
     * @return The item with the given key.
     * @throws Exception Throws if the given key has no item attached to it.
     */
    public Item get(String key) throws Exception {
        if (!containsKey(key)) {
            throw new GetValueException("No item is defined by the provided key: "+key);
        }
        return mappedItems.get(key);
    }

    /**
     * Remove an item stored in a list.
     * @param index The item's index position. If greater than list item count, then the index will be lapped until it is within valid limits.
     * @return The item that was removed at that index.
     * @throws Exception Throws if the index, after processing, is greater than item count in the list.
     */
    public Item remove(int index) throws Exception {
        index = Math.toIntExact(Basic.nortic((long) index, (long) items.size()));
        if (index>items.size()-1) {
            throw new Exception("An unexpected error caused the index to be greater than amount of items stored: "+index);
        }
        Item ret = items.get(index);
        items.remove(index);
        return ret;
    }
    /**
     * Removes an item stored with a key, value pear.
     * @param key The key given to an item.
     * @return The item that was remove with the given key.
     * @throws Exception Throws if the given key has no item attached to it.
     */
    public Item remove(String key) throws Exception {
        Item ret = get(key);
        mappedItems.remove(key);
        return ret;
    }

    /**
     * Check to see if an item has the corresponding key.
     * @param key The key to search for.
     * @return True if an item with teh given key exists.
     */
    public boolean containsKey(String key) {
        return mappedItems.containsKey(key);
    }

    /**
     * Throws an exception if an item with the given key exists.
     * @param key The key to search for.
     * @throws Exception
     */
    protected void catchKey(String key) throws Exception {
        if (containsKey(key)) {
            throw new Exception("Cannot override items stored. The provided key already exists: "+key);
        }
    }
}
