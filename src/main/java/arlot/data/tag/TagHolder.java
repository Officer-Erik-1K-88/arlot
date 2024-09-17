package arlot.data.tag;

import arlot.data.Data;
import arlot.data.Info;
import arlot.error.GetValueException;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class TagHolder extends Info implements Serializable, Iterable<Tag> {
    // static
    @Serial
    private static final long serialVersionUID = 778272580L;

    private static final HashMap<String, TagHolder> allHolders = new HashMap<>();

    public static TagHolder getHolder(String name) {
        if (holderExists(name)) {
            return allHolders.get(name);
        } else {
            throw new GetValueException("The TagHolder being fetched does not exist: "+name);
        }
    }

    /**
     *
     * @param name The holder to check if exists.
     * @return true if the holder with the provided name exists.
     */
    public static boolean holderExists(String name) {
        return allHolders.containsKey(name);
    }

    // properties
    private final HashMap<String, Tag> holder;

    // constructor

    /**
     *
     * @param name The name of this holder.
     */
    public TagHolder(String name) {
        super(name, "Holds tags.");
        holder = new HashMap<>();
        if (holderExists(name)) {
            throw new IllegalArgumentException("Cannot create a TagHolder with the name of `"+name+"` when another TagHolder has said name.");
        } else {
            allHolders.put(name, this);
        }
    }

    // methods
    /**
     * Gets a {@link Tag} instance that is identified by the provided name.
     * @param name The tag name to search for.
     * @return The {@link Tag} with the given name.
     */
    public Tag get(String name) {
        if (hasTag(name)) {
            return holder.get(name);
        } else {
            throw new GetValueException("The given tag doesn't exists: "+name);
        }
    }

    /**
     * Creates and defines a new tag in this given TagHolder.
     * @param name How the tag will be identified via a hash, is different from it's {@code identifier}.
     * @param title What the tag actually spells without the limitations of the {@code name}.
     * @param description The definition of the tag.
     * @throws IllegalArgumentException If the tag already exists in this {@code TagHolder}.
     */
    public void add(String name, String title, String description) {
        tagCheck(name);
        holder.put(name, new Tag(name, title, description, getName()));
    }

    /**
     * Creates a new tag that'll be added to this {@code TagHolder}
     * from an already defined tag.
     * @param tag The tag to add to this TagHolder.
     * @throws IllegalArgumentException If the name already exists as a tag
     * in this {@code TagHolder}.
     */
    public void add(Tag tag) {
        if (!hasTag(tag.getName())) {
            add(tag.getName(), tag.getTitle(), tag.getDescription());
        } else {
            throw new IllegalArgumentException("This tag already exists: "+tag.getName());
        }
    }

    /**
     * Removes a tag from this {@code TagHolder}.
     * @param name The tag to remove from this {@code TagHolder}.
     * @return The {@link Tag} that was removed,
     * if the tag doesn't exist then {@code null} is returned.
     */
    public Tag remove(String name) {
        if (hasTag(name)) {
            return holder.remove(name);
        }
        return null;
    }

    /**
     * Gets an array of strings containing all available tags.
     * @return a String Array of all tag names.
     */
    public String[] getAllTags() {
        return holder.keySet().toArray(new String[0]);
    }

    /**
     * Checks to see if the given tag exists in this TagHolder.
     * @param name The tag's name to check for.
     * @return true if the given tag exists.
     */
    public boolean hasTag(String name) {
        return holder.containsKey(name);
    }

    /**
     * The identifier of tag usage.
     * <br>
     * This is a way of calling the {@code .use()} method on a {@link Tag}
     * without needing to get the {@link Tag}.
     * @param name The tag to use.
     * @return This holder and the tag's name. Formatted like: "[holder].[tag name]"
     */
    public String useTag(String name) {
        Tag tag = get(name);
        return tag.use();
    }

    /**
     * States that a tag gets used on the stated object.
     * <br>
     * The stated object must implement {@link Taggable}.
     * <br>
     * This is a way of calling the {@code .useOn(Object)} method on a {@link Tag}
     * without needing to get the {@link Tag}.
     * @param name The tag to use.
     * @param obj The object to add the stated tag to.
     */
    public void useTag(String name, Object obj) {
        Tag tag = get(name);
        tag.useOn(obj);
    }

    /**
     * Checks to see if a given string is valid for use in a new {@link Tag} construct.
     * @param tag The tag to check for validity of use in a new {@link Tag} construct.
     * @return The given String, otherwise will throw an exception if invalid.
     */
    public String tagCheck(String tag) {
        if (tag == null) {
            throw new NullPointerException("Null values are not allowed");
        }
        if (tag.contains(" ")) {
            throw new IllegalArgumentException("Tag names must not contain spaces, use '_' instead.");
        }
        if (tag.contains("#")) {
            throw new IllegalArgumentException("Tag names must not contain '#', as it's special.");
        }
        if (tag.contains(".")) {
            throw new IllegalArgumentException("Tag names must not contain '.', as it's special.");
        }
        if (hasTag(tag)) {
            throw new IllegalArgumentException("Duplicate values are not allowed");
        }
        return tag;
    }

    public Iterator<Tag> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<Tag> {
        Iterator<Tag> elementIterator = holder.values().iterator();

        // prevent creating a synthetic constructor
        Itr() {}

        public boolean hasNext() {
            return elementIterator.hasNext();
        }

        public Tag next() {
            return elementIterator.next();
        }

        public void remove() {
            elementIterator.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super Tag> action) {
            Objects.requireNonNull(action);
            elementIterator.forEachRemaining(action);
        }
    }
}
