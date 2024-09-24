package arlot.data.collect;

import arlot.data.Data;
import arlot.error.IdentificationException;
import arlot.error.UpdateDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Options<T> {
    private final ListMap<String, Option<T>> opts;

    private final int capacity;

    /**
     * Creates a new Options with a max capacity of {@link Integer#MAX_VALUE}.
     */
    public Options() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Creates a new Options that as a max capacity.
     *
     * @param cap The max capacity to be applied to this Options.
     */
    public Options(int cap) {
        opts = new ListMap<>(cap);
        capacity = cap;
    }

    /**
     * Creates a new Options with the List that is provided,
     * if two or more stored Pairs share the same key, then only
     * the first Pair will be added to the Options.
     *
     * <br>
     *
     * The max capacity will be set to the number of items added
     * to this Options.
     *
     * @param c The List that is to be applied as the Objects of this Options.
     */
    public Options(List<? extends Map.Entry<String, ?>> c) {
        opts = new ListMap<>();
        AtomicInteger count = new AtomicInteger();
        c.forEach((entry) -> {
            try {
                if (!opts.containsKey(entry.getKey())) {
                    Pair<String, Option<T>> pair;
                    if (entry instanceof Option) {
                        pair = new Pair<>(entry.getKey(), (Option<T>) entry);
                    } else if (entry.getValue() instanceof List) {
                        pair = new Pair<>(entry.getKey(),
                                new Option<>(0, entry.getKey(),
                                        (List<T>) entry.getValue()));
                    } else {
                        pair = new Pair<>(entry.getKey(), new Option<>(0, entry.getKey(),
                                (T) entry.getValue()));
                    }
                    opts.add(pair);
                    count.getAndIncrement();
                }
            } catch (Exception ignored) {}
        });
        capacity = count.get();
    }

    /**
     * Gets the count of Objects stored in this Options.
     *
     * @return The amount of stored Objects.
     */
    public int size() {
        return opts.size();
    }

    /**
     * Checks if this Options has objects stored.
     *
     * @return true if there is no Objects stored.
     */
    public boolean isEmpty() {
        return opts.isEmpty();
    }

    /**
     * Gets the integer that represents the max amount of
     * Objects to be stored.
     *
     * @return The max amount of Objects to be stored.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Checks to see if this Options has space left to add more Objects.
     *
     * @return true if the {@link #size()} is less than {@link #getCapacity() capacity}.
     */
    public boolean hasSpace() {
        return size() < capacity;
    }

    /**
     * Checks to see if this Options has a defined max amount of Objects
     * that can be stored.
     *
     * @return true if this Options has a capacity.
     */
    public boolean hasCapacity() {
        return capacity < Integer.MAX_VALUE;
    }

    /**
     * Get the index of some key or value that is stored in this Options.
     *
     * @param o The Object to find the index of.
     * @return The index of the Object or -1 if the Object isn't stored in this Options.
     */
    public int indexOf(Object o) {
        if (o instanceof Option) {
            return opts.indexOf(((Option<?>) o).getKey());
        }
        return opts.indexOf(o);
    }

    /**
     * Gets an {@link Option} based off it's order in this Options.
     *
     * @param index The placement of the value to get.
     * @return The {@code Option} that is placed at the provided index.
     */
    public Option<T> get(int index) {
        return opts.get(index).getValue();
    }

    /**
     * Gets an {@link Option} associated with a key.
     *
     * @param key The key to get the value of.
     * @return The {@code Option} associated to the provided key.
     */
    public Option<T> get(String key) {
        return opts.get(key);
    }

    /**
     * Adds/updates this Options with the key and value stated.
     *
     * <br>
     *
     * If this Options has no space left, or the key already exists,
     * then this method will function exactly the same as
     * {@link #update(String, List)}.
     *
     *
     * @param key The key that is to be added.
     * @param value The value that is represented by the key.
     * @return true if this Options was changed.
     */
    public boolean put(String key, List<T> value) {
        Objects.requireNonNull(value);
        if (hasSpace()) {
            Option<T> option = new Option<>(size(), key, value);
            try {
                return opts.add(option.asPair());
            } catch (IdentificationException e) {
                try {
                    return opts.set(option.getIndex(), option.asPair()).getValue() != null;
                } catch (IdentificationException ignored) {}
            }
        } else {
            try {
                return update(key, value) != null;
            } catch (Exception ignored) {}
        }
        return false;
    }

    /**
     * Changes the value of the provided key.
     *
     * @param key The key to replace the value of.
     * @param value The value to replace the old value.
     * @return The old value associated with the key.
     */
    public List<T> update(String key, List<T> value) {
        if (opts.containsKey(key)) {
            Option<T> option = get(key);
            return option.setValue(value);
        }
        throw new UpdateDeniedException("Cannot update what doesn't exist.");
    }

    public final static class Option<V> implements Map.Entry<String, List<V>>, Iterable<V> {
        private final String key;
        private final int index;
        private List<V> value = new ArrayList<>();

        public Option(int index, String key, V value) {
            this.key = key;
            this.index = index;
            this.value.add(value);
        }

        public Option(int index, String key, List<V> value) {
            this.key = key;
            this.index = index;
            this.value = value;
        }

        public Option(int index, Map.Entry<String, List<V>> entry) {
            this(index, entry.getKey(), entry.getValue());
        }

        public int size() {
            return this.value.size();
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public List<V> getValue() {
            return value;
        }

        public V getSingle() {
            return get(0);
        }

        public V get(int index) {
            return this.value.get(index);
        }

        @Override
        public List<V> setValue(List<V> value) {
            List<V> old = this.value;
            this.value = value;
            return old;
        }

        public boolean add(V value) {
            return this.value.add(value);
        }

        public boolean addAll(List<? extends V> c) {
            return this.value.addAll(c);
        }

        public V remove(int index) {
            return this.value.remove(index);
        }

        public boolean remove(V o) {
            return this.value.remove(o);
        }

        public Pair<String, Option<V>> asPair() {
            return new Pair<>(key, this);
        }

        @Override
        public Iterator<V> iterator() {
            return new OptionItr();
        }

        class OptionItr implements Iterator<V> {
            int cursor;       // index of next element to return
            int lastRet = -1; // index of last element returned; -1 if no such

            // prevent creating a synthetic constructor
            OptionItr() {}

            @Override
            public boolean hasNext() {
                return cursor != Option.this.size();
            }

            @Override
            public V next() {
                int i = cursor;
                if (i >= Option.this.size())
                    throw new NoSuchElementException();
                cursor = i + 1;
                return Option.this.get(lastRet = i);
            }

            @Override
            public void remove() {
                if (lastRet < 0)
                    throw new IllegalStateException();

                try {
                    Option.this.remove(lastRet);
                    cursor = lastRet;
                    lastRet = -1;
                } catch (IndexOutOfBoundsException ex) {
                    throw new ConcurrentModificationException();
                }
            }

            @Override
            public void forEachRemaining(Consumer<? super V> action) {
                Objects.requireNonNull(action);
                final int size = Option.this.size();
                int i = cursor;
                if (i < size) {
                    for (; i < size; i++)
                        action.accept(Option.this.get(i));
                    // update once at end to reduce heap write traffic
                    cursor = i;
                    lastRet = i - 1;
                }
            }
        }
    }
}
