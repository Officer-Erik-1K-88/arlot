package arlot.roleplay.info;

import arlot.data.Info;
import arlot.data.tag.Tag;
import arlot.data.tag.TagHolder;
import arlot.data.tag.Taggable;
import arlot.error.UpdateDeniedException;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Just a generic data definer.
 */
public class Data extends Info implements Taggable {
    @Serial
    private static final long serialVersionUID = 573618736L;
    // properties
    private final List<String> tags = new ArrayList<>();
    private arlot.data.Data<?> content;
    // constructors

    /**
     * Create a new data holder.
     * @param name The reference of data.
     * @param description The description/definition of the data.
     * @param data The data to be stored.
     * @param tags The tags to be given to the data. (Must be formatted like: "[tag holder].[tag name]")
     */
    public Data(String name, String description, Object data, String[] tags) {
        super(name, description);
        for (String tag : tags) {
            String[] s = tag.split("\\.");
            if (s.length!=2) {
                throw new IllegalArgumentException("One or more tags have (or not have) the holder with tag name separator character of '.', it is being used "+s.length+" times, when it should only be 2.");
            }
            addTag(s[0], s[1]);
        }
        this.content = new arlot.data.Data<>(data);
    }
    // getters

    /**
     * Gets the tags of the data.
     * @return A String array of tag names.
     */
    @Override
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Add a tag to the data.
     * @param tag The name of the tag to be added.
     */
    @Override
    public void addTag(String tag, String holder) {
        TagHolder held = TagHolder.getHolder(holder);
        if (held.hasTag(tag)) {
            if (hasTag(held.get(tag))) {
                throw new UpdateDeniedException("The provided tag is already applied to the data.");
            }
            tags.add(held.useTag(tag));
        } else {
            throw new IllegalArgumentException("The provided tag doesn't exist.");
        }
    }

    /**
     * Add a tag to the data.
     * @param tag The tag to be added.
     */
    @Override
    public void addTag(Tag tag) {
        addTag(tag.getName(), tag.getHolderName());
    }

    /**
     * Gets the stored data.
     * @return The object in which is the data stored.
     */
    public arlot.data.Data<?> getData() {
        return content;
    }

    /**
     * Gets the class that the data belongs to.
     * @return The data's class.
     */
    public Class<?> dataClass() {
        return content.dataClass();
    }
    // methods

    /**
     * Remove a tag from the data.
     * @param tag The name of the tage to be removed.
     */
    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    /**
     * Replace the content of the data with data of the same class.
     * @param data The data to replace the old data.
     * @throws UpdateDeniedException Only when the class of the new data doesn't equal {@code getDataClass()}.
     */
    public void updateData(Object data) throws Exception {
        if (content.instance(data.getClass())) {
            this.content = new arlot.data.Data<>(data);
        } else {
            throw new UpdateDeniedException("The new data's data class ("+data.getClass().getName()+") isn't the same as the current data class ("+dataClass().getName()+")");
        }
    }
}
