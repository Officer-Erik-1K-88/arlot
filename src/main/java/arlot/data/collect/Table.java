package arlot.data.collect;

import arlot.data.Data;
import arlot.math.Number;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Table<K, V> implements SequencedMap<K, V> {
    static class Node<K, V> extends Pair<K, V> implements Iterator<Node<K, V>> {
        private Node<K, V> next = null;
        private Node<K, V> previous = null;
        private Number index;

        public Node(K key, V value) {
            super(key, value);
        }

        public Node(Pair<K, V> pair) {
            super(pair);
        }

        public Node(Data<K> key, Data<V> value) {
            super(key, value);
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public final Node<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("There are no more nodes in this Table: Reached the last node.");
            }
            return next;
        }
        /**
         * Returns the previous element in the iteration.
         *
         * @return the previous element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        public final Node<K, V> previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException("There are no more nodes in this Table: Reached the first node.");
            }
            return previous;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return a node rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public final boolean hasNext() {
            return next != null;
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #previous()} would
         * return a node rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        public final boolean hasPrevious() {
            return previous != null;
        }

        /**
         * Adds/changes what the next node will be.
         *
         * @param next The node to proceed after this node.
         */
        protected final void setNext(Node<K, V> next) {
            if (this.next != null) {
                next.setNext(this.next);
                next.setPrevious(this);
                this.next.setPrevious(next);
            }
            this.next = next;
        }

        /**
         * Adds/changes what the node before this one will be.
         *
         * @param previous The node to proceed before this node.
         */
        protected final void setPrevious(Node<K, V> previous) {
            if (this.previous != null) {
                previous.setPrevious(this.previous);
                previous.setNext(this);
                this.previous.setNext(previous);
            }
            this.previous = previous;
        }
    }

    private final HashMap<K, Number> keys = new HashMap<>();

    private Node<K, V> first;
    private Node<K, V> last;

    private Node<K, V> current = null;

    private final Number size;

    public Table() {
        size = new Number();
    }

    /**
     * Set the first pair to a prestated pair.
     * @param pair The key-value pair to set the first pair to.
     */
    public Table(Pair<K, V> pair) {
        this.first = new Node<>(pair);
        this.first.index = new Number();
        this.last = this.first;
        keys.put(this.first.getKey(), this.first.index);
        size = new Number(1);
    }

    /**
     * Creates a new key-value pair for the first pair.
     *
     * @param key The {@code Object} to set as the key of the first pair.
     * @param value The {@code Object} to set as the value of the first pair.
     */
    public Table(K key, V value) {
        this(new Pair<>(key, value));
    }

    /**
     * Allows for setting a non-editable pair by allowing for self setting of
     * the actual key and value of the first pair.
     *
     * @param key The {@link Data data value} to set as the key of the first pair.
     * @param value The {@link Data data value} to set as the value of the first pair.
     */
    public Table(Data<K> key, Data<V> value) {
        this(new Pair<>(key, value));
    }

    /**
     * Gets the item count.
     *
     * @return The Numeric value count of Pairs.
     * @see #size()
     */
    public Number count() {
        return size.copy();
    }

    /**
     * Gets the item count.
     *
     * @return The integer value of the amount of Pairs.
     * @see #count()
     */
    @Override
    public int size() {
        return size.intValue();
    }

    /**
     * Checks to see if this Table has any stored key-value Pairs.
     *
     * @return true if this Table is empty.
     */
    @Override
    public boolean isEmpty() {
        return size.symbols("eq", "0");
    }

    /* ------------------------------------------------------------ */
    // editing

    /**
     * Adds an already existing pair to the end of the table.
     * <br>
     * The pair given will not be copied.
     *
     * @param pair The key-value pair to set the next pair to.
     * @return the previous value associated with the pair's key, or null if there was
     * no mapping for the key.
     */
    public V put(Pair<K, V> pair) {
        if (containsKey(pair.getKey())) {
            return fetch(keys.get(pair.getKey())).setValue(pair.getValue());
        }
        Node<K, V> newPair = new Node<>(pair);
        newPair.index = new Number(size);
        if (size.symbols("eq", "0")) {
            this.first = newPair;
        } else {
            this.last.setNext(newPair);
            newPair.setPrevious(this.last);
        }
        keys.put(newPair.getKey(), newPair.index);
        this.last = newPair;
        size.add(1);
        return null;
    }

    /**
     * Adds a new pair to the end of the table.
     *
     * @param key   The {@code Object} to set as the key of the next pair.
     * @param value The {@code Object} to set as the value of the next pair.
     * @return the previous value associated with key, or null if there was
     * no mapping for key.
     */
    @Override
    public V put(K key, V value) {
        return put(new Pair<>(key, value));
    }

    /**
     * Adds a new pair to the end of the table, allowing for self setting of
     * the actual key and value of the next pair.
     *
     * @param key The {@link Data data value} to set as the key of the next pair.
     * @param value The {@link Data data value} to set as the value of the next pair.
     * @return the previous value associated with key, or null if there was
     * no mapping for key.
     */
    public V put(Data<K> key, Data<V> value) {
        return put(new Pair<>(key, value));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        t.forEach(this::put);
    }

    @Override
    public V putFirst(K key, V value) {
        return insert(new Number(), key, value);
    }

    @Override
    public V putLast(K key, V value) {
        V retVal = null;
        if (containsKey(key)) {
            retVal = remove(keys.get(key)).getValue();
        }
        put(key, value);
        return retVal;
    }

    /**
     * Puts the provided key-value Pair at the stated index,
     * if the key already exists, then will move that key to the new index and replace
     * its value with the value of the stated pair.
     *
     * <br>
     *
     * Shifts all other Nodes by one position forward.
     *
     * @param index The location to put the key-value Pair.
     * @param pair The key-value Pair to store at the stated index.
     */
    public V insert(Number index, Pair<K, V> pair) {
        if (index.symbols("gt", size)) {
            throw new IndexOutOfBoundsException("The provided index is larger than the currently allowed top insertion bound of `"+size+"`.");
        } else if (index.symbols("lt", "0")) {
            throw new IndexOutOfBoundsException("The provided index is smaller the allowed bottom insertion bound of `zero`.");
        }
        if (index.symbols("eq", size)) {
            return put(pair);
        }
        V retVal = null;
        if (containsKey(pair.getKey())) {
            retVal = remove(keys.get(pair.getKey())).getValue();
        }
        Node<K, V> newPair = new Node<>(pair);
        newPair.index = new Number(index);
        Node<K, V> node = this.first;
        boolean nodeSet = false;
        if (index.symbols("eq", this.last.index)) {
            this.last.index.add(1);
            newPair.setNext(this.last);
            newPair.setPrevious(this.last.previous());
            this.last.setPrevious(newPair);
            size.add(1);
            keys.put(newPair.getKey(), newPair.index);
            keys.put(this.last.getKey(), this.last.index);
            return retVal;
        }
        while (node.index.symbols("lt", size)) {
            if (node.index.symbols("eq", index) && !nodeSet) {
                newPair.setNext(node);
                if (node.hasPrevious()) {
                    node.previous().setNext(newPair);
                    newPair.setPrevious(node.previous());
                } else {
                    this.first = newPair;
                }
                node.setPrevious(newPair);
                node.index.add(1);
                nodeSet = true;
                size.add(1);
                keys.put(newPair.getKey(), newPair.index);
                keys.put(node.getKey(), node.index);
            }
            node = node.next();
            if (nodeSet) {
                node.index.add(1);
                keys.put(node.getKey(), node.index);
            }
        }
        return retVal;
    }

    /**
     * Puts the provided key and value pair at the stated index,
     * if the key already exists, then will move that key to the new index and replace
     * its value with the stated value.
     *
     * <br>
     *
     * Shifts all other Nodes by one position forward.
     *
     * @param index The location to put the key and value pair.
     * @param key The value that is the key of the pair.
     * @param value The value of the pair.
     */
    public V insert(Number index, K key, V value) {
        return insert(index, new Pair<>(key, value));
    }

    /**
     * Puts the provided key and value pair at the stated index,
     * allowing for self-setting of the actual key and value of the pair,
     * if the key already exists, then will move that key to the new index and replace
     * its value with the stated value.
     *
     * <br>
     *
     * Shifts all other Nodes by one position forward.
     *
     * @param index The location to put the key and value pair.
     * @param key The {@link Data data value} to set as the key of the pair.
     * @param value The {@link Data data value} to set as the value of the pair.
     */
    public V insert(Number index, Data<K> key, Data<V> value) {
        return insert(index, new Pair<>(key, value));
    }

    /* ------------------------------------------------------------ */
    // deletion

    public Pair<K, V> remove(Number index) {
        Pair<K, V> removed = null;
        Node<K, V> node = this.first;
        while (node.index.symbols("lt", size)) {
            if (node.index.symbols("eq", index) && removed == null) {
                keys.remove(node.getKey(), node.index);
                removed = node;
                size.subtract(1);
                if (node.hasPrevious() && node.hasNext()) {
                    node.next().setPrevious(node.previous());
                    node.previous().setNext(node.next());
                } else if (node.hasPrevious()) {
                    node.previous().setNext(null);
                    this.last = node.previous();
                    break;
                } else {
                    node.next().setPrevious(null);
                    this.first = node.next();
                }
                //node = node.previous().next();
            }
            node = node.next();
            if (removed != null) {
                node.index.subtract(1);
                keys.put(node.getKey(), node.index);
            }
        }
        return removed;
    }

    @Override
    public V remove(Object key) {
        return remove(keys.get(key)).getValue();
    }

    @Override
    public void clear() {
        first = null;
        current = null;
        last = null;
        size.update("0");
        keys.clear();
    }

    /* ------------------------------------------------------------ */
    // getters

    /**
     * Gets the Node at the stated index.
     *
     * @param index The location the node is held in.
     * @return The Node that is at the provided index.
     */
    private Node<K, V> fetch(Number index) {
        if (index.symbols("eq", "0")) {
            return this.first;
        } else if (index.symbols("eq", this.last.index)) {
            return this.last;
        } else if (index.symbols("ge", size)) {
            throw new IndexOutOfBoundsException("The provided index is larger than the currently allowed top retrieval bound of `"+this.last.index+"`.");
        } else if (index.symbols("lt", "0")) {
            throw new IndexOutOfBoundsException("The provided index is smaller than the allowed bottom retrieval bound of `zero`.");
        }
        if (hasCurrent()) {
            if (index.symbols("eq", current.index)) {
                return current;
            }
        }
        Node<K, V> node = this.first;
        while (node.index.symbols("ne", index)) {
            node = node.next();
        }
        return node;
    }

    /**
     * Gets the stored value based on its insertion index.
     *
     * @param index The indexed location of the stored value.
     * @return The stored value.
     */
    public V get(Number index) {
        return fetch(index).getValue();
    }

    /**
     * Gets the stored value based on its associated key.
     *
     * @param key the key whose associated value is to be returned
     * @return The stored value.
     * @see #get(Number)
     */
    @Override
    public V get(Object key) {
        Number index = keys.get(key);
        if (index != null) {
            return fetch(index).getValue();
        }
        return null;
    }

    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public Collection<V> values() {
        return new Values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    public Table<K, V> reversed() {
        Table<K, V> reverse = new Table<>();
        forEach(reverse::putFirst);
        return reverse;
    }

    /* ------------------------------------------------------------ */
    // Sets and Collections

    final class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return Table.this.size();
        }
    }

    final class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return Table.this.size();
        }
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return Table.this.size();
        }
    }

    /* ------------------------------------------------------------ */
    // iterators

    abstract class TableIterator {
        Node<K,V> current;     // current entry

        TableIterator() {
            current = Table.this.first;
        }

        public final boolean hasNext() {
            return current.hasNext();
        }

        final Node<K,V> nextNode() {
            return current.next();
        }

        public final void remove() {
            Node<K,V> p = current;
            if (p == null)
                throw new IllegalStateException();
            current = Table.this.first;
            Table.this.remove(p.index);
        }
    }

    final class KeyIterator extends TableIterator implements Iterator<K> {
        public final K next() { return nextNode().getKey(); }
    }

    final class ValueIterator extends TableIterator implements Iterator<V> {
        public final V next() { return nextNode().getValue(); }
    }

    final class EntryIterator extends TableIterator
        implements Iterator<Map.Entry<K, V>> {
        public final Map.Entry<K, V> next() { return nextNode(); }
    }

    /* ------------------------------------------------------------ */
    // Node control

    /**
     * Makes sure that the last pair is actually the last pair in the table.
     */
    private void checkLastIsLast() {
        while (this.last.hasNext()) {
            this.last = this.last.next();
        }
    }

    /**
     * Gets the first pair in this table.
     * @return the first pair in this table.
     */
    public Pair<K, V> getFirst() {
        return first;
    }
    /**
     * Gets the last pair in this table.
     * @return the last pair in this table.
     */
    public Pair<K, V> getLast() {
        return last;
    }

    /**
     * Gets the currently selected pair.
     * @return The currently selected pair,
     * if none are selected then {@code null} will be returned.
     */
    public Pair<K, V> getSelected() {
        return current;
    }

    public void deselect() {
        current = null;
    }

    /**
     * Sets what the currently selected pair is.
     * @param index the stored index of the pair to be selected.
     * @return The selected pair.
     */
    public Pair<K, V> select(Number index) {
        Node<K, V> node = fetch(index);
        current = node;
        return node;
    }

    /**
     * Returns the next pair in the table.
     * <br>
     * If this table has no more pairs proceeding forward,
     * then the next pair to be returned will be the first pair.
     *
     * @return the next pair in the table
     */
    public Pair<K, V> next() {
        if (hasNext()) {
            current = current.next();
        } else {
            current = this.first;
        }
        return current;
    }
    /**
     * Returns the previous pair in the table.
     * <br>
     * If this table has no more pairs proceeding backwards,
     * then the next pair to be returned will be the last pair.
     *
     * @return the previous pair in the table
     */
    public Pair<K, V> previous() {
        if (hasPrevious()) {
            current = current.previous();
        } else {
            current = this.last;
        }
        return current;
    }

    /**
     * Returns {@code true} if this table has a pair selected.
     * @return the state of whether this table has a pair selected.
     */
    public boolean hasCurrent() {
        return this.current != null;
    }

    /**
     * Returns {@code true} if {@link #next} would
     * return the next pair rather than the first pair.
     * <br>
     * By default, if this table has no pair selected,
     * then {@code false} will always be returned.
     *
     * @return {@code true} if the table has more pairs
     */
    public boolean hasNext() {
        if (hasCurrent()) {
            return this.current.hasNext();
        }
        return false;
    }
    /**
     * Returns {@code true} if {@link #previous()} would
     * return the previous pair rather than the last pair.
     * <br>
     * By default, if this table has no pair selected,
     * then {@code false} will always be returned.
     *
     * @return {@code true} if the table has more pairs
     */
    public boolean hasPrevious() {
        if (hasCurrent()) {
            return this.current.hasPrevious();
        }
        return false;
    }

    /**
     * Returns true if this table contains a mapping for the specified key.
     *
     * @param key The key whose presence in this table is to be tested.
     * @return true if this table contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(Object key) {
        return keys.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Node<K, V> node = this.first; node.hasNext(); node = node.next()) {
            if (Objects.equals(node.getValue(), value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the key and value already exist as a pair.
     *
     * @param key The key whose presence in this table is to be tested.
     * @param value The value whose presence in this map is to be tested
     * @return true if the pair of key and value already exists.
     */
    public boolean contains(Object key, Object value) {
        for (Node<K, V> node = this.first; node.hasNext(); node = node.next()) {
            if (Objects.equals(node.getKey(), key) &&
                    Objects.equals(node.getValue(), value)) {
                return true;
            }
        }
        return false;
    }

    /* ------------------------------------------------------------ */
    // Loops

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Node<K, V> node = this.first; node.hasNext(); node = node.next()) {
            action.accept(node.getKey(), node.getValue());
        }
    }

    public void forEachRemaining(Consumer<? super Pair<K, V>> action) {
        Objects.requireNonNull(action);
        Pair<K, V> node = current;
        if (node == null) {
            node = next();
        }
        for (; hasNext(); node = next()) {
            action.accept(node);
        }
        action.accept(node);
    }

    /* ------------------------------------------------------------ */
    // comparison

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Table<?, ?> table)) return false;
        boolean same = Objects.equals(keys, table.keys) &&
                Objects.equals(getFirst(), table.getFirst()) &&
                Objects.equals(getLast(), table.getLast()) &&
                Objects.equals(size, table.size);
        if (!same) {
            return false;
        }
        Number i = count();
        Node<K, V> node1 = this.first;
        Node<?, ?> node2 = table.first;
        while (i.symbols("ne", "0")) {
            if (!Objects.equals(node1, node2)) {
                same = false;
                break;
            }
            i.subtract(1);
        }
        return same;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys, getFirst(), getLast(), size);
    }
}
