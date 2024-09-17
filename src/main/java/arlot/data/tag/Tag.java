package arlot.data.tag;


import arlot.data.Info;
import arlot.error.UpdateDeniedException;
import arlot.math.Number;

import java.io.Serial;
import java.io.Serializable;

/**
 * Holds information on a defined tag.
 */
public final class Tag extends Info implements Serializable {
    @Serial
    private static final long serialVersionUID = 110599180L;

    // static
    private static final TagHolder all = new TagHolder("all");

    /**
     * Gets the {@code all} {@link TagHolder} that is stored in {@link Tag}.
     * @return The {@link TagHolder} holding new {@link Tag} constructs.
     */
    public static TagHolder all() {
        return all;
    }

    /**
     * Checks to see if a given string is valid for use in a new {@link Tag} construct.
     * @param tag The tag to check for validity of use in a new {@link Tag} construct.
     * @return The given String, otherwise will throw an exception if invalid.
     */
    public static String tagCheck(String tag) {
        return all.tagCheck(tag);
    }

    // properties
    private final String title;
    private final Number count;

    private final String holderName;

    // constructor
    Tag(String name, String title, String description, String holderName) {
        super(tagCheck(name), description);
        this.title = title;
        count = new Number();
        this.holderName = holderName;
    }

    /**
     * Creates and defines a new tag.
     * @param name How the tag will be identified via a hash, is different from it's {@code identifier}.
     * @param title What the tag actually spells without the limitations of the {@code name}.
     * @param description The definition of the tag.
     */
    public Tag(String name, String title, String description) {
        super(tagCheck(name), description);
        this.title = title;
        count = new Number();
        all.add(this);
        holderName = "all";
    }

    // methods
    /**
     * @return The tag's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The name of the holder this Tag is in.
     */
    public String getHolderName() {
        return holderName;
    }

    /**
     * This is the amount of times the tag has been used on some data.
     * @return A {@link arlot.math.Number} representation of tag usage.
     */
    public Number count() {
        return count;
    }

    /**
     * The identifier of tag usage.
     * <br>
     * Will increase {@link #count()} by one.
     * <br>
     * Should only ever be used with in the calls adding this tag to an object.
     * @return This tag's holder and name. Formatted like: "[holder].[name]"
     */
    public String use() {
        count.add(1);
        return getHolderName()+"."+getName();
    }

    /**
     * Adds a tag to an object that is {@link Taggable}.
     * <br>
     * This will call the {@code .addTag(Tag)} to apply this
     * tag to the {@link Taggable} {@link Object}.
     * <br>
     * This method will not add one to {@link #count()},
     * that must be done with in {@code .addTag(Tag)} via the use of {@link #use()}.
     * @param obj The object to add this Tag to.
     */
    public void useOn(Object obj) {
        if (obj instanceof Taggable) {
            ((Taggable) obj).addTag(this);
        } else {
            throw new UpdateDeniedException("The provided object is not Taggable");
        }
    }
}
