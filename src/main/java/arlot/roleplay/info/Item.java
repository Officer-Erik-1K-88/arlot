package arlot.roleplay.info;

import arlot.error.UpdateDeniedException;

import java.util.Map;
import java.util.Objects;

/**
 * The class that is used to define items.
 */
public class Item extends Data {
    // properties

    // constructor

    /**
     * Creates an item that characters can use.
     * @param name The name of the item.
     * @param description The details of the item, like what does it do.
     * @param data A plate of stat data where the key is the stat's name.
     * @param tags An array of strings that define the tags the item has.
     */
    public Item(String name, String description, Map<String, Object> data, String[] tags) {
        super(name, description, data, tags);
        addTag("item", "all");
    }
    // methods
    /**
     * Getting item stats this way is prohibited.
     *
     * @return null.
     */
    @Override
    public arlot.data.Data<?> getData() {
        return null;
    }

    /**
     * Get the item stats.
     * @return A {@code Map<String, Object>} that represents the item's stats.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getStats() {
        // Safely cast the data to Map<String, Object>
        if (super.getData() instanceof Map) {
            return (Map<String, Object>) super.getData();
        } else {
            throw new ClassCastException("Unexpectedly, stat data is not a Map<String, Object>");
        }
    }

    /**
     * Gets a single defined stat.
     * @param key The stat to fetch.
     * @return An object representing the stat provided as {@code key}.
     */
    public Object getStat(String key) {
        return getStats().get(key);
    }

    /**
     * Edits a single defined stat, given that the class of the new value is the same class as the old value.
     * <br>
     * This can also be used to add new stats to the item.
     * @param key The stat to edit.
     * @param value The value to replace the old value with.
     * @return The old stat value.
     * @throws UpdateDeniedException Only if the class of the new value doesn't match the old value's class.
     */
    public Object updateStat(String key, Object value) throws Exception {
        if (Objects.equals(value.getClass(), getStat(key).getClass())) {
            return getStats().replace(key, value);
        }
        throw new UpdateDeniedException("Object classes don't match.");
    }

    /**
     * Item stat data isn't allowed to be replaced, refer to {@code updateStat(String, Object)} for data editing.
     * @throws UpdateDeniedException Because you're not allowed to replace the stat data this way.
     */
    @Override
    public void updateData(Object data) throws Exception {
        throw new UpdateDeniedException("Character stat data isn't allowed to be replaced, refer to updateStat(String, Object) for data editing.");
    }
}
