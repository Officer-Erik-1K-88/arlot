package arlot.data.collect;

import arlot.data.Compares;
import arlot.data.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Holds a key and a value as a pair.
 *
 * @param <K> The {@code Object type} to be allowed as the key.
 * @param <V> The {@code Object type} to be allowed as the value.
 */
public class Pair<K, V> implements Cloneable, Serializable,
        Comparable<Pair<?, ?>>, Map.Entry<K, V> {
    @java.io.Serial
    private static final long serialVersionUID = 674280633L;

    /**
     * The key of this pair stored as {@link Data}.
     */
    private final Data<K> key;
    /**
     * The value of this pair stored as {@link Data}.
     */
    private final Data<V> value;

    protected Pair() {
        this.key = new Data<>();
        this.value = new Data<>();
    }

    /**
     * Creates a new key-value pair.
     *
     * @param key The {@code Object} to set as the key.
     * @param value The {@code Object} to set as the value.
     */
    public Pair(K key, V value) {
        this.key = new Data<>(key);
        this.value = new Data<>(value);
    }

    /**
     * The copy constructor.
     * @param pair The key-value pair to copy.
     */
    public Pair(Pair<K, V> pair) {
        this(pair.key, pair.value);
    }

    /**
     * Allows for setting a non-editable pair by allowing for self setting of
     * the actual {@link #key} and {@link #value}.
     *
     * @param key The {@link Data data value} to set as the key.
     * @param value The {@link Data data value} to set as the value.
     */
    public Pair(Data<K> key, Data<V> value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get the stored key of this pair.
     * @return The value of the {@link #key}.
     */
    @Override
    public final K getKey() {
        return key.getValue();
    }

    /**
     * Gets the true key that is stored in this pair.
     * @return the {@link Data} {@link #key} of this pair.
     */
    public final Data<K> actualKey() {
        return this.key;
    }

    /**
     * Get the stored value of this pair.
     * @return The value of the {@link #value}.
     */
    @Override
    public final V getValue() {
        return value.getValue();
    }

    /**
     * Gets the true value that is stored in this pair.
     * @return the {@link Data} {@link #value} of this pair.
     */
    public final Data<V> actualValue() {
        return this.value;
    }

    /**
     * Replaces the key corresponding to this pair with the specified value.
     *
     * @param key new key to be stored in this pair
     * @return old key corresponding to the pair
     */
    public K setKey(K key) {
        return this.key.update(key);
    }

    /**
     * Replaces the value corresponding to this pair with the specified value.
     *
     * @param value new value to be stored in this pair
     * @return old value corresponding to the pair
     */
    @Override
    public V setValue(V value) {
        return this.value.update(value);
    }

    /**
     * Updates both the {@link #key} and {@link #value} values of this pair.
     * @param key The new {@link #key} value.
     * @param value the new {@link #value} value.
     */
    public final void update(K key, V value) {
        setKey(key);
        setValue(value);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param   obj   the reference object with which to compare.
     * @return  {@code true} if this object is the same as the obj
     *          argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof Pair<?, ?> pair)) return false;
        return Objects.equals(getKey(), pair.getKey()) && Objects.equals(getValue(), pair.getValue());
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero,
     * or a positive integer as this object is less than,
     * equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @throws ClassCastException   {@inheritDoc}
     */
    @Override
    public int compareTo(Pair<?, ?> o) {
        if (o == null)
            throw new NullPointerException("The given Object must not be null.");
        return Compares.DEFAULT.compare(this, o);
    }

    /**
     * Returns a hash code value for the object.
     * This method is supported for the benefit
     * of hash tables such as those provided by {@link java.util.HashMap}.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }

    @Override
    public Pair<?, ?> clone() {
        try {
            return new Pair<>((Pair<?, ?>) super.clone());
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
