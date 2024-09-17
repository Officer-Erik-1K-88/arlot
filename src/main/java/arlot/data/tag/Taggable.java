package arlot.data.tag;

import java.util.List;

/**
 * States that a class can be tagged.
 * <br>
 * All taggable classes
 */
public interface Taggable {
    /**
     * Gets all tags in the object that implements this interface.
     * <br>
     * All stored tags should be formatted in the following way: "[tag's holder].[tag's name]"
     * @return A List of strings that represent all tags in an object.
     */
    List<String> getTags();

    /**
     * Adds a tag to the object that implements this interface.
     * @param tag The tag name to add.
     * @param holder The {@link TagHolder} that the provided tag is a part of.
     */
    void addTag(String tag, String holder);

    /**
     * Adds a tag to the object that implements this interface.
     * <br>
     * This method should behave like {@link #addTag(String, String)}.
     * @param tag The tag to add.
     */
    void addTag(Tag tag);

    /**
     * Checks to see if the provided tag exists with in the object.
     * @param tag The tag's name to be searched for.
     * @param holder The tag's holder that the provided tag is in.
     * @return true if the object has the tag.
     */
    default boolean hasTag(String tag, String holder) {
        if (tag == null || holder == null) {
            return false;
        }
        return getTags().contains(holder+'.'+tag);
    }
    /**
     * Checks to see if the provided tag exists with in the object.
     * @param tag The tag to be searched for.
     * @return true if the object has the tag.
     */
    default boolean hasTag(Tag tag) {
        if (tag == null) {
            return false;
        }
        return hasTag(tag.getName(), tag.getHolderName());
    }
}
