package arlot.data.collect;

import arlot.data.Data;
import arlot.error.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * This behaves like an {@link ArrayList},
 * all data is stored as {@link Data}.
 *
 * @param <E> The type of elements stored as {@link Data} in this list.
 */
public class DataList<E> implements List<E>, Iterable<E>, //RandomAccess,
        Cloneable, java.io.Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 246367371L;

    private final ArrayList<Data<E>> elementData;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public DataList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new ArrayList<>(initialCapacity);
        } else if (initialCapacity == 0) {
            this.elementData = new ArrayList<>();
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public DataList() {
        this.elementData = new ArrayList<>();
    }

    /**
     * Constructs a DataList containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this DataList.
     * @throws NullPointerException if the specified collection is null
     */
    public DataList(Collection<? extends E> c) {
        if (!c.isEmpty()) {
            this.elementData = new ArrayList<>(c.size());
            c.forEach((item) -> {
                this.elementData.add(new Data<>(item));
            });
        } else {
            // replace with empty array.
            this.elementData = new ArrayList<>();
        }
    }

    public DataList(DataCollection<? extends Data<E>> c) {
        if (!c.isEmpty()) {
            this.elementData = new ArrayList<>(c);
        } else {
            // replace with empty array.
            this.elementData = new ArrayList<>();
        }
    }

    @Override
    public DataList<?> clone() {
        try {
            return new DataList<>((DataList<?>) super.clone());
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    public interface DataCollection<V> extends Collection<V> {}

    public int size() {
        return elementData.size();
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return elementData.isEmpty();
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * {@code Objects.equals(o, e)}.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        boolean good = elementData.contains(o);
        if (!good) {
            good = elementData.contains(new Data<>(o));
        }
        return good;
    }

    /**
     * Returns {@code true} if this list contains all of the elements of the
     * specified collection.
     *
     * @param c collection to be checked for containment in this list
     * @return {@code true} if this list contains all of the elements of the
     * specified collection
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @see #contains(Object)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        boolean good = true;
        for (Object item : c) {
            if (!contains(item)) {
                good = false;
                break;
            }
        }
        return good;
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this list all of its elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO: Make an implementation.
        throw new NotImplementedException("Has not been Implemented.");
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        int index = elementData.indexOf(o);
        if (index == -1) {
            elementData.indexOf(new Data<>(o));
        }
        return index;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object o) {
        int index = elementData.lastIndexOf(o);
        if (index == -1) {
            elementData.lastIndexOf(new Data<>(o));
        }
        return index;
    }

    public E get(int index) {
        return elementData.get(index).getValue();
    }

    public Data<E> getActual(int index) {
        return elementData.get(index);
    }

    /**
     * Calls {@link #getActual(int)} and sends the {@code element}
     * to the {@link Data#update(Object)} method.
     * @param index The index of the {@link Data} to edit.
     * @param element The element to set the {@link Data} to.
     * @return The old value of the {@link Data}.
     */
    public E set(int index, E element) {
        return getActual(index).update(element);
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        return elementData.add(new Data<>(e));
    }

    /**
     * Appends the specified {@link Data} element directly to the end of
     * the used {@link ArrayList}.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     * @see #add(Object)
     */
    public boolean add(Data<E> e) {
        return elementData.add(e);
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        elementData.add(index, new Data<>(element));
    }

    /**
     * Inserts the specified {@link Data} element at the specified position in
     * the used {@link ArrayList}.
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see #add(int, Object)
     */
    public void add(int index, Data<E> element) {
        elementData.add(index, element);
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(Collection<? extends E> c) {
        if (!c.isEmpty()) {
            c.forEach(this::add);
            return true;
        }
        return false;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        if (!c.isEmpty()) {
            c.forEach((item) -> add(index, item));
            return true;
        }
        return false;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present (optional operation).  If this list does not contain
     * the element, it is unchanged.  More formally, removes the element with
     * the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))}
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list changed
     * as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        boolean good = elementData.remove(o);
        if (!good) {
            good = elementData.remove(new Data<>(o));
        }
        return good;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        return elementData.remove(index).getValue();
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection (optional operation).
     *
     * @param c collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        AtomicBoolean good = new AtomicBoolean(false);
        c.forEach((item) -> {
            if (remove(item)) {
                good.set(true);
            }
        });
        return good.get();
    }

    /**
     * Removes all of the elements from this list.
     * The list will be empty after this call returns.
     */
    public void clear() {
        elementData.clear();
    }

    public void sort(Comparator<? super E> c) {
        if (c == null) {
            sortActual(null);
        }
        // TODO: Actually create this sorting method
        throw new NotImplementedException("Sorting this way hasn't been implemented.");
    }

    public void sortActual(Comparator<? super Data<E>> c) {
        elementData.sort(c);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public ListIterator<E> listIterator(int index) {
        return new ListItr(index);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * @see #listIterator(int)
     */
    public ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations supported
     * by this list.<p>
     * <p>
     * This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>{@code
     *      list.subList(from, to).clear();
     * }</pre>
     * Similar idioms may be constructed for {@code indexOf} and
     * {@code lastIndexOf}, and all of the algorithms in the
     * {@code Collections} class can be applied to a subList.<p>
     * <p>
     * The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex   high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        // TODO: Make an implementation.
        throw new NotImplementedException("Has not been Implemented.");
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must
     * allocate a new array even if this list is backed by an array).
     * The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in proper
     * sequence
     */
    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray() {
        Object[] ret = new Object[size()];
        for (int i=0; i<size(); i++) {
            ret[i] = get(i);
        }
        return (E[]) ret;
    }

    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     *
     * <p>If the list fits in the specified array with room to spare (i.e.,
     * the array has more elements than the list), the element in the array
     * immediately following the end of the list is set to {@code null}.
     * (This is useful in determining the length of the list <i>only</i> if
     * the caller knows that the list does not contain any null elements.)
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose {@code x} is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of {@code String}:
     *
     * <pre>{@code
     *     String[] y = x.toArray(new String[0]);
     * }</pre>
     * <p>
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of this list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of this list
     * @throws ArrayStoreException  if the runtime type of the specified array
     *                              is not a supertype of the runtime type of every element in
     *                              this list
     * @throws NullPointerException if the specified array is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        Object[] ret;
        if (a.length >= size()) {
            ret = new Object[a.length];
        } else {
            ret = new Object[size()];
        }
        for (int i=0; i<size(); i++) {
            Data<E> check = getActual(i);
            if (check.instance(a)) {
                throw new ArrayStoreException("The stored data is different from array being put into.");
            }
            ret[i] = check.getValue();
        }
        return (T[]) ret;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (E e : this) action.accept(e);
    }

    /**
     * Copy of the iterator provided by the {@link ArrayList} used for {@link Data} storage.
     */
    private class Itr implements Iterator<E> {
        Iterator<Data<E>> elementIterator = elementData.iterator();

        // prevent creating a synthetic constructor
        Itr() {}

        public boolean hasNext() {
            return elementIterator.hasNext();
        }

        public E next() {
            return elementIterator.next().getValue();
        }

        public void remove() {
            elementIterator.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            elementIterator.forEachRemaining((item) -> {
                action.accept(item.getValue());
            });
        }
    }

    /**
     * Copy of the list iterator provided by
     * the {@link ArrayList} used for {@link Data} storage.
     */
    private class ListItr extends Itr implements ListIterator<E> {
        ListIterator<Data<E>> elementListIterator;
        ListItr(int index) {
            super();
            elementListIterator = elementData.listIterator(index);
        }

        public boolean hasPrevious() {
            return elementListIterator.hasPrevious();
        }

        public int nextIndex() {
            return elementListIterator.nextIndex();
        }

        public int previousIndex() {
            return elementListIterator.previousIndex();
        }

        public E previous() {
            return elementListIterator.previous().getValue();
        }

        public void set(E e) {
            elementListIterator.set(new Data<>(e));
        }

        public void add(E e) {
            elementListIterator.add(new Data<>(e));
        }
    }
}
