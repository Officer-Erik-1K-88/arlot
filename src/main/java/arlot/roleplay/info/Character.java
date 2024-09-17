package arlot.roleplay.info;

import arlot.error.UpdateDeniedException;

import java.util.Map;
import java.util.Objects;

/**
 * Creates character profiles.
 */
public class Character extends Data {
    // properties
    // constructor

    /**
     * Create a new character with this.
     * @param name The characters name.
     * @param description The details you want others to know about the character, this includes background, what the character looks like, and other similar things that should be known about the character.
     * @param stats The status plate of the character where the key is the stat's name.
     */
    public Character(String name, String description, Map<String, Object> stats) {
        super(name, description, stats, new String[] {"all.character"});
    }
    // methods

    /**
     * Getting character stats this way is prohibited.
     *
     * @return null.
     */
    @Override
    public arlot.data.Data<?> getData() {
        return null;
    }

    /**
     * Get the character stats.
     * @return A {@code Map<String, Object>} that represents the character's stats.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getStats() {
        // Safely cast the data to Map<String, Object>
        if (super.getData().getValue() instanceof Map) {
            return (Map<String, Object>) super.getData().getValue();
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
     * This can also be used to add new stats to the character.
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
     * Character stat data isn't allowed to be replaced, refer to {@code updateStat(String, Object)} for data editing.
     * @throws UpdateDeniedException Because you're not allowed to replace the stat data this way.
     */
    @Override
    public void updateData(Object data) {
        throw new UpdateDeniedException("Character stat data isn't allowed to be replaced, refer to updateStat(String, Object) for data editing.");
    }
}
