package arlot.data.collect;

import arlot.error.IdentificationException;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * ListMaps hold a list of key-value pairs where no two-values can have the same key.
 *
 * @param <K> The type of key that a ListMap has.
 * @param <V> The type of value that a ListMap has.
 */
public class ListMap<K, V> implements SequencedMap<K, V>,
        Iterable<Pair<K, V>> {
    final class Node extends Pair<K, V> {
        private int index;

        public Node(int index, K key, V value) {
            super(key, value);
            this.index = index;
        }

        public Node(int index, Pair<K, V> pair) {
            super(pair);
            this.index = index;
        }

        @Override
        public K setKey(K key) {
            K ret = super.setKey(key);
            ListMap.this.hashMap.remove(ret);
            ListMap.this.hashMap.put(getKey(), getValue());
            ListMap.this.keys.set(index, getKey());
            return ret;
        }

        @Override
        public V setValue(V value) {
            V ret = super.setValue(value);
            ListMap.this.hashMap.put(getKey(), value);
            return ret;
        }

        public void update(int index, K key, V value) {
            update(key, value);
            this.index = index;
        }
    }

    final Node nodeOf(int index, K key, V value) {
        return new Node(index, key, value);
    }

    final Node nodeOf(int index, Pair<K, V> pair) {
        return new Node(index, pair);
    }

    /**
     * Holds all unique keys.
     */
    private final ArrayList<K> keys;
    /**
     * Holds the map of objects.
     */
    private final HashMap<K, V> hashMap;

    /* ------------------------------------------------------------ */
    // constructors
    /**
     * Creates a new ListMap.
     */
    public ListMap() {
        keys = new ArrayList<>();
        hashMap = new HashMap<>();
    }

    public ListMap(int initialCapacity) {
        keys = new ArrayList<>(initialCapacity);
        hashMap = new HashMap<>(initialCapacity);
    }

    public ListMap(Map<? extends K, ? extends V> m) {
        this(m.size());
        putAll(m);
    }

    public ListMap(Collection<? extends Pair<K, V>> c) {
        this(c.size());
        addAll(c);
    }

    /* ------------------------------------------------------------ */
    // checkers

    protected final boolean checkValueType(Object value) {
        if (isEmpty() || value == null) {
            return false;
        }
        V v = this.getFirst().getValue();
        return value.getClass().isInstance(v);
    }

    protected final boolean checkKeyType(Object key) {
        if (isEmpty() || key == null) {
            return false;
        }
        K v = this.keys.getFirst();
        return key.getClass().isInstance(v);
    }

    /**
     * Checks to see if the stated key has an object tied to it.
     * @param key The char array key to look for.
     * @return true if the provided key can be found in the
     * `id registry` and in the `key list`.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        if (checkKeyType(key)) {
            return keys.contains((K) key) && hashMap.containsKey(key);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (checkValueType(value)) {
            return hashMap.containsValue(value);
        }
        return false;
    }

    public boolean contains(Object key, Object value) {
        if (!checkValueType(value)) {
            return false;
        }
        if (containsKey(key)) {
            return Objects.equals(this.get(key), value);
        }
        return false;
    }

    public boolean contains(Object value) {
        if (checkKeyType(value)) {
            return containsKey(value);
        } else if (value instanceof Map.Entry<?, ?> entry) {
            return contains(entry.getKey(), entry.getValue());
        }
        return this.containsValue(value);
    }

    public boolean containsAll(Collection<?> c) {
        for (Object value : c) {
            if (!contains(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty() && hashMap.isEmpty();
    }

    /* ------------------------------------------------------------ */
    // getters

    /**
     * Get the count of items stored in this ListMap.
     *
     * @return The number of objects stored.
     */
    @Override
    public int size() {
        return keys.size();
    }

    /**
     * Get the index of some key or value that is stored in this ListMap.
     *
     * @param o The object to find the index of, can be a key, a value, or a key-value Pair.
     * @return The index of the Object or -1 if the Object isn't stored in this ListMap.
     */
    @SuppressWarnings("unchecked")
    public int indexOf(Object o) {
        if (checkKeyType(o)) {
            return keys.indexOf((K) o);
        } else if (o instanceof Map.Entry<?,?>) {
            Object key = ((Map.Entry<?, ?>) o).getKey();
            Object value = ((Map.Entry<?, ?>) o).getValue();
            if (contains(key, value)) {
                return indexOf(key);
            }
        } else if (o != null) {
            for (int i = 0; i< keys.size(); i++) {
                if (Objects.equals(this.get(keys.get(i)), o)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public Pair<K, V> get(int index) {
        K key = keys.get(index);
        return new Node(index, key, hashMap.get(key));
    }

    /**
     * Gets the object that is identified by the provided unique key.
     *
     * @param key The string key to look for.
     * @return The object of the key. Or {@link null} if the provided
     * key has no object linked to it.
     */
    @Override
    public V get(Object key) {
        if (checkKeyType(key)) {
            return hashMap.get(key);
        }
        return null;
    }

    public Pair<K, V> getFirst() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return this.get(0);
        }
    }

    public Pair<K, V> getLast() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return this.get(this.size() - 1);
        }
    }

    /* ------------------------------------------------------------ */
    // add and insertion

    public boolean add(Pair<K, V> pair) {
        if (containsKey(pair.getKey())) {
            throw new IdentificationException("Cannot add this Object to the ListMap, key already exists.");
        }
        boolean boolt = keys.add(pair.getKey());
        hashMap.put(pair.getKey(), pair.getValue());
        return boolt;
    }

    public void add(int index, Pair<K, V> pair) {
        if (containsKey(pair.getKey())) {
            throw new IdentificationException("Cannot add this Object to the ListMap, key already exists.");
        }
        keys.add(index, pair.getKey());
        hashMap.put(pair.getKey(), pair.getValue());
    }

    public boolean addAll(Collection<? extends Pair<K, V>> pairCollection) {
        boolean changed = false;

        for (Pair<K, V> pair : pairCollection) {
            if (containsKey(pair.getKey())) {
                continue;
            }
            changed = true;
            keys.add(pair.getKey());
            hashMap.put(pair.getKey(), pair.getValue());
        }

        return changed;
    }

    public boolean addAll(int index, Collection<? extends Pair<K, V>> pairCollection) {
        boolean changed = false;

        int currIndex = index;
        for (Pair<K, V> pair : pairCollection) {
            if (containsKey(pair.getKey())) {
                continue;
            }
            changed = true;
            keys.add(currIndex, pair.getKey());
            hashMap.put(pair.getKey(), pair.getValue());
            currIndex++;
        }

        return changed;
    }

    @Override
    public V put(K key, V value) {
        if (containsKey(key)) {
            throw new IdentificationException("Cannot add this Object to the ListMap, key already exists.");
        }
        keys.add(key);
        return hashMap.put(key, value);
    }

    public V put(int index, K key, V value) {
        if (containsKey(key)) {
            throw new IdentificationException("Cannot add this Object to the ListMap, key already exists.");
        }
        keys.add(index, key);
        return hashMap.put(key, value);
    }

    /**
     * Inserts the given mapping into the map if it is not already present.
     * After this operation completes normally,
     * the given mapping will be present in this map, and it will be the
     * first mapping in this map's encounter order.
     *
     * @param k the key
     * @param v the value
     * @return the value previously associated with k, or null if none
     * @throws IdentificationException if this map already has the given key.
     */
    @Override
    public V putFirst(K k, V v) {
        return put(0, k, v);
    }

    /**
     * Inserts the given mapping into the map if it is not already present.
     * After this operation completes normally,
     * the given mapping will be present in this map, and it will be the
     * last mapping in this map's encounter order.
     *
     * @param k the key
     * @param v the value
     * @return the value previously associated with k, or null if none
     * @throws IdentificationException if this map already has the given key.
     */
    @Override
    public V putLast(K k, V v) {
        return put(size()-1, k, v);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((key, value) -> {
            try {
                put(key, value);
            } catch (IdentificationException ignored) {}
        });
    }

    public void putAll(int index, Map<? extends K, ? extends V> m) {
        AtomicInteger currIndex = new AtomicInteger(index);
        m.forEach((key, value) -> {
            try {
                put(currIndex.get(), key, value);
                currIndex.getAndIncrement();
            } catch (IdentificationException ignored) {}
        });
    }

    public Pair<K, V> set(int index, Pair<K, V> pair) {
        if (Objects.equals(keys.get(index), pair.getKey())) {
            return new Node(index, pair.getKey(), hashMap.put(pair.getKey(), pair.getValue()));
        }
        if (containsKey(pair.getKey())) {
            throw new IdentificationException("Cannot add this Object to the ListMap, key already exists and is not at "+index+" index.");
        }
        Node ret;
        K past = keys.set(index, pair.getKey());
        ret = new Node(index, past, hashMap.remove(past));
        hashMap.put(pair.getKey(), pair.getValue());
        return ret;
    }

    public Pair<K, V> set(int index, K key, V value) {
        return set(index, new Pair<>(key, value));
    }

    /* ------------------------------------------------------------ */
    // removal
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object value) {
        V removed = null;
        K key;
        if (value instanceof Map.Entry<?, ?>) {
            if (contains(value)) {
                key = (K) ((Map.Entry<?, ?>) value).getKey();
                V val = (V) ((Map.Entry<?, ?>) value).getValue();
                removed = this.get(key);
                if (!remove(key, val)) {
                    removed = null;
                }
            }
            return removed;
        }
        if (containsKey(value)) {
            key = (K) value;
            removed = hashMap.remove(key);
            keys.remove(key);
        } else if (containsValue(value)) {
            key = keys.get(indexOf(value));
            removed = hashMap.remove(key);
            keys.remove(key);
        }

        return removed;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object id, Object value) {
        K key;
        if (containsKey(id)) {
            key = (K) id;
            if (value != null) {
                if (hashMap.remove(key, value)) {
                    keys.remove(key);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean removeAll(Collection<?> c) {
        boolean changed = false;

        for (Object value : c) {
            if (remove(value) != null) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        keys.clear();
        hashMap.clear();
        keySet = null;
        values = null;
        entrySet = null;
    }

    /* ------------------------------------------------------------ */
    // iteration

    /**
     * Returns a reverse-ordered <a href="Collection.html#view">view</a> of this map.
     * The encounter order of mappings in the returned view is the inverse of the encounter
     * order of mappings in this map. The reverse ordering affects all order-sensitive operations,
     * including those on the view collections of the returned view. If the implementation permits
     * modifications to this view, the modifications "write through" to the underlying map.
     * Changes to the underlying map might or might not be visible in this reversed view,
     * depending upon the implementation.
     *
     * @return a reverse-ordered view of this map
     */
    @Override
    public SequencedMap<K, V> reversed() {
        ListMap<K, V> newListMap = new ListMap<>(this.size());
        forEach(newListMap::putFirst);
        return null;
    }

    /**
     * Fills an array with this map values and returns it. This method assumes
     * that input array is big enough to fit all the values. Use
     * {@link #prepareArray(Object[])} to ensure this.
     *
     * @param a an array to fill
     * @param <T> type of array elements
     * @return supplied array
     */
    <T> T[] valuesToArray(T[] a) {
        Object[] r = a;
        AtomicInteger idx = new AtomicInteger();
        if (size() > 0) {
            forEach((key, value) -> {
                r[idx.getAndIncrement()] = value;
            });
        }
        return a;
    }

    /**
     * Prepares the array for {@link Collection#toArray(Object[])} implementation.
     * If supplied array is smaller than this map size, a new array is allocated.
     * If supplied array is bigger than this map size, a null is written at size index.
     *
     * @param a an original array passed to {@code toArray()} method
     * @param <T> type of array elements
     * @return an array ready to be filled and returned from {@code toArray()} method.
     */
    @SuppressWarnings("unchecked")
    final <T> T[] prepareArray(T[] a) {
        int size = this.size();
        if (a.length < size) {
            return (T[]) java.lang.reflect.Array
                    .newInstance(a.getClass().getComponentType(), size);
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }


    private Set<K> keySet = null;
    private Collection<V> values = null;
    private Set<Map.Entry<K, V>> entrySet = null;

    /**
     * Returns a list iterator over the elements in this ListMap (in proper
     * sequence).
     *
     * @return a list iterator over the elements in this ListMap (in proper
     *         sequence)
     */
    public ListIterator<Pair<K, V>> listIterator() {
        return new ListItr(0);
    }

    /**
     * Returns a list iterator over the elements in this ListMap (in proper
     * sequence), starting at the specified position in the ListMap.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     *
     * @param index index of the first element to be returned from the
     *        list iterator (by a call to {@link ListIterator#next next})
     * @return a list iterator over the elements in this ListMap (in proper
     *         sequence), starting at the specified position in the ListMap
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index > size()})
     */
    public ListIterator<Pair<K, V>> listIterator(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return new ListItr(index);
    }

    /**
     * Creates an {@code Iterator} that iterates over a {@link Pair}
     * that has a key of {@link K} with a value of {@link V}.
     *
     * @return An {@code Iterator} that iterates over a {@link Pair}.
     */
    @Override
    public Iterator<Pair<K, V>> iterator() {
        return new Itr();
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations. It does not support the {@code add} or {@code addAll}
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own {@code remove} operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     *
     * @return a view of the values contained in this map
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     *
     * @return a set view of the mappings contained in this map
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        hashMap.forEach(action);
    }

    public void forEachIndex(BiConsumer<Integer, Pair<K, V>> action) {
        Objects.requireNonNull(action);
        for (int i=0; i<size(); i++) {
            action.accept(i, new Node(i, get(i)));
        }
    }

    /* ------------------------------------------------------------ */
    // iterators
    private class Itr implements Iterator<Pair<K, V>> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        // prevent creating a synthetic constructor
        Itr() {}

        public boolean hasNext() {
            return cursor != size();
        }

        public Pair<K, V> next() {
            int i = cursor;
            if (i >= size())
                throw new NoSuchElementException();
            cursor = i + 1;
            int lr = (lastRet = i);
            K id = keys.get(lr);
            return new Node(lr, id, ListMap.this.get(id));
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();

            try {
                ListMap.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Pair<K, V>> action) {
            Objects.requireNonNull(action);
            final int size = ListMap.this.size();
            int i = cursor;
            if (i < size) {
                for (; i < size; i++) {
                    int lr = (lastRet = i);
                    K id = keys.get(lr);
                    action.accept(new Node(lr, id, ListMap.this.get(id)));
                }
                // update once at end to reduce heap write traffic
                cursor = i;
                lastRet = i - 1;
            }
        }
    }

    private class ListItr extends Itr implements ListIterator<Pair<K, V>> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public Pair<K, V> previous() {
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            cursor = i;
            int lr = (lastRet = i);
            K id = keys.get(lr);
            return new Node(lr, id, ListMap.this.get(id));
        }

        public void set(Pair<K, V> e) {
            if (lastRet < 0)
                throw new IllegalStateException();

            try {
                ListMap.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(Pair<K, V> e) {
            try {
                int i = cursor;
                ListMap.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    abstract class ListMapIterator {
        Node next;        // next entry to return
        Node current;     // current entry
        int index;             // current slot

        ListMapIterator() {
            current = next = null;
            index = 0;
            K key = keys.get(index);
            next = new Node(index, key, ListMap.this.get(key));
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Node nextNode() {
            index++;
            K key;
            try {
                key = keys.get(index);
            } catch (IndexOutOfBoundsException e) {
                next = null;
                throw new NoSuchElementException();
            }
            next.update(index, key, ListMap.this.get(key));

            return next;
        }

        public final void remove() {
            Node p = current;
            if (p == null)
                throw new IllegalStateException();
            current = null;
            ListMap.this.remove(p.getKey());
        }
    }

    final class KeyIterator extends ListMapIterator
            implements Iterator<K> {
        public K next() {
            return Objects.requireNonNull(nextNode()).getKey();
        }
    }

    final class ValueIterator extends ListMapIterator
            implements Iterator<V> {
        public V next() {
            return Objects.requireNonNull(nextNode()).getValue();
        }
    }

    final class EntryIterator extends ListMapIterator
            implements Iterator<Map.Entry<K, V>> {
        public Map.Entry<K, V> next() {
            return nextNode();
        }
    }

    /* ------------------------------------------------------------ */
    // sets and collections
    final class KeySet extends AbstractSet<K> {
        public int size() {
            return ListMap.this.size();
        }
        public void clear() {
            ListMap.this.clear();
        }
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        public boolean contains(Object o) { return containsKey(o); }
        public boolean remove(Object key) {
            return ListMap.this.remove(key) != null;
        }
        public Spliterator<K> spliterator() {
            return new KeySpliterator<>(ListMap.this, 0, -1, 0);
        }

        public Object[] toArray() {
            return ListMap.this.keys.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return ListMap.this.keys.toArray(a);
        }

        public void forEach(Consumer<? super K> action) {
            ListMap.this.forEach((key, value) -> action.accept(key));
        }
    }

    final class Values extends AbstractCollection<V> {
        public int size() {
            return ListMap.this.size();
        }
        public void clear() {
            ListMap.this.clear();
        }
        public final Iterator<V> iterator() {
            return new ValueIterator();
        }
        public final boolean contains(Object o) {
            return containsValue(o);
        }
        public final Spliterator<V> spliterator() {
            return new ValueSpliterator<>(ListMap.this, 0, -1, 0);
        }

        public Object[] toArray() {
            return valuesToArray(new Object[size()]);
        }

        public <T> T[] toArray(T[] a) {
            return valuesToArray(prepareArray(a));
        }

        public final void forEach(Consumer<? super V> action) {
            ListMap.this.forEach((key, value) -> action.accept(value));
        }
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        public int size() {
            return ListMap.this.size();
        }
        public void clear() {
            ListMap.this.clear();
        }
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        public boolean contains(Object o) {
            if (!(o instanceof Pair<?, ?> e))
                return false;
            Object key = e.getKey();
            Object value = e.getValue();
            return ListMap.this.contains(key, value);
        }
        public boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?> e) {
                Object key = e.getKey();
                Object value = e.getValue();
                return ListMap.this.remove(key, value);
            }
            return false;
        }
        public Spliterator<Map.Entry<K, V>> spliterator() {
            return new EntrySpliterator<>(ListMap.this, 0, -1, 0);
        }
        public void forEach(Consumer<? super Map.Entry<K, V>> action) {
            ListMap.this.forEachIndex((index, pair) -> action.accept(new Node(index, pair)));
        }
    }

    /* ------------------------------------------------------------ */
    // spliterators

    static class ListMapSpliterator<K, V> {
        final ListMap<K, V> map;
        ListMap<K, V>.Node current;          // current node
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate

        ListMapSpliterator(ListMap<K, V> m, int origin,
                         int fence, int est) {
            this.map = m;
            this.index = origin;
            this.fence = fence;
            this.est = est;
        }

        final int getFence() { // initialize fence and size on first use
            int hi;
            if ((hi = fence) < 0) {
                ListMap<K, V> m = map;
                est = m.size();
                hi = fence = m.keys.size();
            }
            return hi;
        }

        public final long estimateSize() {
            getFence(); // force init
            return (long) est;
        }
    }

    static final class KeySpliterator<K, V>
        extends ListMapSpliterator<K, V>
        implements Spliterator<K> {
        KeySpliterator(ListMap<K, V> m, int origin, int fence, int est) {
            super(m, origin, fence, est);
        }

        public KeySpliterator<K, V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                new KeySpliterator<>(map, lo, index = mid, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super K> action) {
            int i, hi;
            if (action == null)
                throw new NullPointerException();
            ListMap<K, V> m = map;
            if ((hi = fence) < 0) {
                hi = fence = m.keys.size();
            }
            if ( m.keys.size() >= hi &&
                (i = index) >= 0 && (i < (index = hi) || current != null)) {
                K id = null;
                if (current != null) {
                    id = current.getKey();
                }
                current = null;
                do {
                    if (id != null) {
                        action.accept(id);
                    }
                    try {
                        id = m.keys.get(i++);
                    } catch (IndexOutOfBoundsException e) {
                        id = null;
                    }
                } while (id != null || i < hi);
            }
        }

        public boolean tryAdvance(Consumer<? super K> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            ListMap<K, V> m = map;
            if (m.keys.size() >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    K id;
                    if (current == null) {
                        int lr = index++;
                        id = m.keys.get(lr);
                        current = m.nodeOf(lr, id, m.get(id));
                    } else {
                        K k = current.getKey();
                        if (index+1 < m.keys.size()) {
                            id = m.keys.get(index+1);
                            current = m.nodeOf(index+1, id, m.get(id));
                        } else {
                            current = null;
                        }
                        action.accept(k);
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size() ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT;
        }
    }

    static final class ValueSpliterator<K, V>
        extends ListMapSpliterator<K, V>
        implements Spliterator<V> {
        ValueSpliterator(ListMap<K, V> m, int origin, int fence, int est) {
            super(m, origin, fence, est);
        }

        public ValueSpliterator<K, V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                new ValueSpliterator<>(map, lo, index = mid, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super V> action) {
            int i, hi;
            if (action == null)
                throw new NullPointerException();
            ListMap<K, V> m = map;
            if ((hi = fence) < 0) {
                hi = fence = m.keys.size();
            }
            if ( m.keys.size() >= hi &&
                    (i = index) >= 0 && (i < (index = hi) || current != null)) {
                K id = null;
                if (current != null) {
                    id = current.getKey();
                }
                current = null;
                do {
                    if (id != null) {
                        action.accept(m.get(id));
                    }
                    try {
                        id = m.keys.get(i++);
                    } catch (IndexOutOfBoundsException e) {
                        id = null;
                    }
                } while (id != null || i < hi);
            }
        }

        public boolean tryAdvance(Consumer<? super V> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            ListMap<K, V> m = map;
            if (m.keys.size() >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    K id;
                    if (current == null) {
                        int lr = index++;
                        id = m.keys.get(lr);
                        current = m.nodeOf(lr, id, m.get(id));
                    } else {
                        V v = current.getValue();
                        if (index+1 < m.keys.size()) {
                            id = m.keys.get(index+1);
                            current = m.nodeOf(index+1, id, m.get(id));
                        } else {
                            current = null;
                        }
                        action.accept(v);
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size() ? Spliterator.SIZED : 0);
        }
    }

    static final class EntrySpliterator<K, V>
        extends ListMapSpliterator<K, V>
        implements Spliterator<Map.Entry<K, V>> {
        EntrySpliterator(ListMap<K, V> m, int origin, int fence, int est) {
            super(m, origin, fence, est);
        }

        public EntrySpliterator<K, V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                new EntrySpliterator<>(map, lo, index = mid, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            int i, hi;
            if (action == null)
                throw new NullPointerException();
            ListMap<K, V> m = map;
            if ((hi = fence) < 0) {
                hi = fence = m.keys.size();
            }
            if ( m.keys.size() >= hi &&
                    (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Pair<K, V> p = current;
                current = null;
                do {
                    if (p != null) {
                        action.accept(p);
                    }
                    try {
                        int lr = i++;
                        K id = m.keys.get(lr);
                        p = m.nodeOf(lr, id, m.get(id));
                    } catch (IndexOutOfBoundsException e) {
                        p = null;
                    }
                } while (p != null || i < hi);
            }
        }

        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            ListMap<K, V> m = map;
            if (m.keys.size() >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    K id;
                    if (current == null) {
                        int lr = index++;
                        id = m.keys.get(lr);
                        current = m.nodeOf(lr, id, m.get(id));
                    } else {
                        Pair<K, V> e = current;
                        if (index+1 < m.keys.size()) {
                            id = m.keys.get(index+1);
                            current = m.nodeOf(index+1, id, m.get(id));
                        } else {
                            current = null;
                        }
                        action.accept(e);
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size() ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT;
        }
    }

    /* ------------------------------------------------------------ */
    // Unsupported Operations


    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("Cannot do replace operations with a ListMap.");
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException("Cannot do replace operations with a ListMap.");
    }

    @Override
    public V replace(K key, V value) {
        throw new UnsupportedOperationException("Cannot do replace operations with a ListMap.");
    }
}
