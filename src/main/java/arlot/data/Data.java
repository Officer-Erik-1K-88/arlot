package arlot.data;

import arlot.data.tag.Tag;
import arlot.data.tag.TagHolder;
import arlot.data.tag.Taggable;
import arlot.error.FormattedDataStringException;
import arlot.error.UpdateDeniedException;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Base64;

import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
/**
 * Holds a single piece of data for easy storage and manipulation.
 * @param <V> The type of data to be held.
 */
public class Data<V> extends Info implements Serializable, Taggable, Cloneable,
        Comparable<Data<V>> {
    @Serial
    private static final long serialVersionUID = 962372544L;

    /**
     * Converts the base64 value provided by {@link #formattedString()}
     * in a {@code Data} value back into a {@code Data} value.
     * @param b64Val The base64 value representation of a {@code Data} value.
     * @return The {@code Data} value of the provided base64 value.
     */
    public static Data<?> toData(String b64Val) {
        byte[] data = Base64.getDecoder().decode(b64Val);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (Data<?>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new FormattedDataStringException("The provided base64 value isn't proper data: "+e.getMessage());
        }
    }

    /**
     * The held value.
     */
    private V value;

    /**
     * The held tags.
     */
    private final List<String> tags = new ArrayList<>();

    /**
     * Constructs a new {@code Data Class} with a null value.
     */
    public Data() {
        super("Null", "N/A");
        this.value = null;
    }
    /**
     * Constructs a new {@code Data Class} with a null value.
     * @param editable The state of being mutable or not, set to true to be mutable.
     */
    public Data(boolean editable) {
        super("Null", "N/A", editable);
        this.value = null;
    }

    /**
     * Constructs a new {@code Data Class} with the provided value.
     * @param value The value to be held by the {@code Data Class}.
     */
    public Data(V value) {
        super((Info.getClass(value)!=null?Info.getClass(value).getName():"Null"), "N/A");
        this.value = value;
    }
    /**
     * Constructs a new {@code Data Class} with the provided value.
     * @param value The value to be held by the {@code Data Class}.
     * @param editable The state of being mutable or not, set to true to be mutable.
     */
    public Data(V value, boolean editable) {
        super((Info.getClass(value)!=null?Info.getClass(value).getName():"Null"), "N/A", editable);
        this.value = value;
    }

    /**
     * Constructs a new {@code Data Class} with the provided value and have a custom name.
     * @param value The value to be held by the {@code Data Class}.
     * @param name The custom name to refer to the held value as.
     */
    public Data(V value, String name) {
        super(name, "N/A");
        this.value = value;
    }
    /**
     * Constructs a new {@code Data Class} with the provided value and have a custom name.
     * @param value The value to be held by the {@code Data Class}.
     * @param name The custom name to refer to the held value as.
     * @param editable The state of being mutable or not, set to true to be mutable.
     */
    public Data(V value, String name, boolean editable) {
        super(name, "N/A", editable);
        this.value = value;
    }

    /**
     * Copies a {@code Data} value.
     * @param data The {@code Data} value to be copied.
     */
    public Data(Data<V> data) {
        super(data.getName(), data.getDescription(), data.isEditable());
        this.value = data.getValue();
    }
    /**
     * Copies a {@code Data} value.
     * @param data The {@code Data} value to be copied.
     * @param editable The state of being mutable or not, set to true to be mutable.
     */
    public Data(Data<V> data, boolean editable) {
        super(data.getName(), data.getDescription(), editable);
        this.value = data.getValue();
    }

    /**
     * Copies a {@code Data} value and replaces the name.
     * @param data The {@code Data} value to be copied.
     * @param name The name to replace the copied {@code Data} value's name.
     */
    public Data(Data<V> data, String name) {
        super(name, data.getDescription(), data.isEditable());
        this.value = data.getValue();
    }
    /**
     * Copies a {@code Data} value and replaces the name.
     * @param data The {@code Data} value to be copied.
     * @param name The name to replace the copied {@code Data} value's name.
     * @param editable The state of being mutable or not, set to true to be mutable.
     */
    public Data(Data<V> data, String name, boolean editable) {
        super(name, data.getDescription(), editable);
        this.value = data.getValue();
    }

    /**
     * Gets the value that is being held by this object.
     * @return The held value.
     */
    public V getValue() {
        return value;
    }

    /**
     * The name to be returned by this method is either the name of the class provided by {@code dataClass().getName()}
     * or by a provided name at the construction of this object.
     * @return The data's provided name.
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Gets an array of strings containing all tags added to this data.
     * @return a String Array of all added tag names.
     */
    @Override
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Gets the boolean value that states whether this data is mutable.
     * <br>
     * This data is mutable given if the set value can be changed.
     * @return true if this data is mutable.
     */
    @Override
    public boolean isEditable() {
        return super.isEditable();
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
     * Updates the currently held value to something new.
     * @param value The value to change the currently held value to.
     * @return The previously held value before the change.
     */
    public V update(V value) {
        if (isEditable()) {
            V old = this.value;
            this.value = value;
            if (getName().equals("Null") && dataClass() != null) {
                updateName(dataClass().getName());
            }
            return old;
        } else {
            throw new UpdateDeniedException("This data is not mutable.");
        }
    }

    /**
     * Returns the runtime class of the held value.
     * The returned {@code Class} object is the object that is locked by
     * {@code static} synchronized methods of the represented class.
     * @return The {@code Class} object that represents the runtime class of the held value.
     */
    public Class<?> dataClass() {
        return Info.getClass(value);
    }

    /**
     * Checks to see if the value is of a primitive data type, i.e., is of one of the following:
     * <br>
     * int, double, float, long, short, byte, char, and boolean.
     *
     * <br><br>
     *
     * Or of one of the wrapper classes, which includes:
     * <br>
     * Integer, Double, Float, Long, Short, Byte, Character, and Boolean.
     *
     * @return Is true if the given data is primitive.
     */
    public boolean isPrimitive() {
        if (isEmpty()) {
            return false;
        }
        try {
            return value.getClass().isPrimitive();
        } catch (Exception e) {
            return isInteger() ||
                    isDouble() ||
                    isFloat() ||
                    isLong() ||
                    isShort() ||
                    isByte() ||
                    isChar() ||
                    isBoolean();
        }
    }

    /**
     * Checks to see if the held value is a String.
     *
     * @return true if the held value is a String. Otherwise, false.
     */
    public boolean isString() {
        return value instanceof String;
    }

    /**
     * Checks to see if the held value is a boolean.
     * @return true if the held value is a boolean. Otherwise, false.
     */
    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    /**
     * Checks to see if the held value is an integer.
     * @return true if the held value is an integer. Otherwise, false.
     */
    public boolean isInteger() {
        return value instanceof Integer;
    }

    /**
     * Checks to see if the held value is a long number.
     * @return true if the held value is a long number. Otherwise, false.
     */
    public boolean isLong() {
        return value instanceof Long;
    }

    /**
     * Checks to see if the held value is a short number.
     * @return true if the held value is a short number. Otherwise, false.
     */
    public boolean isShort() {
        return value instanceof Short;
    }

    /**
     * Checks to see if the held value is a double.
     * @return true if the held value is a double. Otherwise, false.
     */
    public boolean isDouble() {
        return value instanceof Double;
    }

    /**
     * Checks to see if the held value is a float.
     * @return true if the held value is a float. Otherwise, false.
     */
    public boolean isFloat() {
        return value instanceof Float;
    }

    /**
     * Checks to see if the held value is a byte.
     * @return true if the held value is a byte. Otherwise, false.
     */
    public boolean isByte() {
        return value instanceof Byte;
    }

    /**
     * Checks to see if the held value is a char.
     * @return true if the held value is a char. Otherwise, false.
     */
    public boolean isChar() {
        return value instanceof Character;
    }

    /**
     * Checks to see if the held value is a number.
     * @return true if the held value is a number. Otherwise, false.
     */
    public boolean isNumber() {
        return value instanceof Number;
    }

    /**
     * Checks to see if the held value is {@link arlot.math.Numeric numeric}.
     * @return true if the held value is {@link arlot.math.Numeric numeric}. Otherwise, false.
     */
    public boolean isNumeric() {
        return value instanceof arlot.math.Numeric;
    }

    /**
     * Checks to see if the held value is a set.
     * @return true if the held value is a set. Otherwise, false.
     */
    public boolean isSet() {
        return value instanceof java.util.Set;
    }

    /**
     * Checks to see if the held value is a list.
     * @return true if the held value is a list. Otherwise, false.
     */
    public boolean isList() {
        return value instanceof List;
    }

    /**
     * Checks to see if the held value is an array.
     * @return true if the held value is an array. Otherwise, false.
     */
    public boolean isArray() {
        return value instanceof java.lang.reflect.Array || value.getClass().isArray();
    }

    /**
     * Checks to see if the held value is a collection.
     * @return true if the held value is a collection. Otherwise, false.
     */
    public boolean isCollection() {
        return value instanceof java.util.Collection;
    }

    /**
     * Checks to see if the held value is a map.
     * @return true if the held value is a map. Otherwise, false.
     */
    public boolean isMap() {
        return value instanceof java.util.Map;
    }

    /**
     * Checks to see if the held value is null.
     * @return If the held value is null, then true is returned, otherwise, false.
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     *
     * @param obj The object to check.
     * @return true if {@code obj} is an instance of (or equals) the provided data value.
     * @see #equals(Object)
     */
    public boolean instance(Object obj) {
        boolean isInst = Objects.equals(this, obj);
        if (!isInst) {
            isInst = Objects.equals(value, obj);
            if (!isInst) {
                if (dataClass() != null) {
                    isInst = dataClass().isInstance(obj);
                } else {
                    if (obj == null) {
                        isInst = true;
                    }
                }
            }
        }
        return isInst;
    }

    /**
     * Indicates whether some other {@code Data} object is "equal to" this one
     * or if the held data values are equal.
     *
     * @param data The Data object with which to compare.
     * @return true if the provided object is "equal to" this one
     * or the data values are equal. Otherwise, false.
     * @see #equals(Object)
     */
    public boolean equals(Data<V> data) {
        boolean eq = equals((Object) data);
        if (!eq) {
            eq = Objects.equals(value, data.getValue());
        }
        return eq;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <br>
     * Data objects are equal given that they have contained values that are equal
     * and the same {@link Tag tags}.
     *
     * @param object the reference object with which to compare.
     * @return true if the provided object is "equal to" this one. Otherwise, false.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Data<?> data)) return false;
        return Objects.equals(getValue(), data.getValue()) && Objects.equals(getTags(), data.getTags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getTags());
    }

    /**
     * Compares this data with the specified data for order. Returns a
     * negative integer, zero, or a positive integer as this data is less
     * than, equal to, or greater than the specified data.
     *
     * @param o the data to be compared.
     * @return a negative integer, zero, or a positive integer as this data
     * is less than, equal to, or greater than the specified data.
     * @throws NullPointerException if the specified Data class is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Data<V> o) {
        Objects.requireNonNull(o);
        if (equals((Object) o)) {
            return 0;
        }
        Compares compares = Compares.DEFAULT;
        int valCompare = compares.compare(value, o.getValue());
        int tagCompare = compares.compare(getTags(), o.getTags());
        int added = valCompare + tagCompare;
        if (added < 0) {
            return -1;
        } else if (added > 0) {
            return 1;
        }
        return valCompare;
    }

    /**
     * Get the string value of {@link #getValue()}.
     * @return The string value of the provided value that is retrieved by {@link #getValue()}.
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Converts this {@code Data} value into a base64 String value.
     * Useful when needing to save non-primitive data to a file without
     * the hassle of potential data loss.
     * @return A base64 representation of this {@code Data} value.
     */
    public String formattedString() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(this);
            byte[] serializedBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(serializedBytes);
        } catch (IOException e) {
            throw new FormattedDataStringException("Data to byte String failure: "+e.getMessage());
        }
    }

    @Override
    public Data<?> clone() {
        return new Data<>((Data<?>) super.clone(), isEditable());
    }
}
