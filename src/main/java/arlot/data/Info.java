package arlot.data;

import arlot.error.UpdateDeniedException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Just generic information like the name and description of any data.
 */
public class Info implements Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = 615985975L;

    public static Class<?> getClass(Object value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case Integer i -> int.class;
            case Boolean b -> boolean.class;
            case Byte b -> byte.class;
            case Character c -> char.class;
            case Double v -> double.class;
            case Float v -> float.class;
            case Long l -> long.class;
            case Short i -> short.class;
            default -> value.getClass();
        };
    }

    // properties
    private String name, description;

    /**
     * State of mutability.
     */
    private final boolean editable;

    // constructor

    /**
     * Creates and defines new information.
     * @param name The reference of information.
     * @param description The body of information.
     */
    public Info(String name, String description) {
        this.name = name;
        this.description = description;
        editable = true;
    }

    /**
     * Creates and defines new information.
     * @param name The reference of information.
     * @param description The body of information.
     * @param editable The state of being mutable or not, set to true to be mutable.
     */
    public Info(String name, String description, boolean editable) {
        this.name = name;
        this.description = description;
        this.editable = editable;
    }

    /**
     * Makes a copy of an {@code Info}.
     * @param info The info to copy.
     */
    public Info(Info info) {
        this.name = info.getName();
        this.description = info.getDescription();
        this.editable = info.isEditable();
    }

    /**
     * Makes a copy of an {@code Info}.
     * <br>
     * Allows for the change of editable value in the copy.
     * @param info The info to copy.
     * @param editable The state of being mutable or not, set to true to be mutable.
     */
    public Info(Info info, boolean editable) {
        this.name = info.getName();
        this.description = info.getDescription();
        this.editable = editable;
    }

    // methods

    /**
     * Checks to see if this info is mutable.
     * <br>
     * This info is mutable given if the set name and description can be changed.
     * @return true if this info is mutable.
     */
    public boolean isEditable() {
        return editable;
    };

    /**
     * @return The object's name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The object's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Replaces the current name with a new one.
     * @param name The name to replace the old name.
     * @return The old name.
     */
    public String updateName(String name) {
        if (isEditable()) {
            String oldName = this.name;
            this.name = name;
            return oldName;
        } else {
            throw new UpdateDeniedException("This info is not mutable.");
        }
    }

    /**
     * Replaces the current description with a new one.
     * @param description The description to replace the old description.
     * @return The old description.
     */
    public String updateDescription(String description) {
        if (isEditable()) {
            String oldDescription = this.description;
            this.description = description;
            return oldDescription;
        } else {
            throw new UpdateDeniedException("This info is not mutable.");
        }
    }

    @Override
    public Info clone() {
        try {
            return (Info) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
}
