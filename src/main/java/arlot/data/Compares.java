package arlot.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Compares is a Comparator that follows the following rules by {@link #DEFAULT}:
 * <ul>
 *     <li>{@code false} and {@code null} are both considered the lowest,
 *     but {@code null} is considered lower.</li>
 *
 *     <li>{@code true} is considered to be the highest.</li>
 *
 *     <li>If both Objects are Collections (including Arrays), then will loop through
 *     the Collections until the final Object of the smallest Collection is tested
 *     or until the Objects of the same index aren't the same.
 *     If the last Object of the smallest Collection is the same as the Object at the same
 *     index in the larger Collection, then the largest Collection is considered the
 *     greatest.</li>
 *
 *     <li>If both Objects are Maps, then they are split between the keys and values,
 *     where the keySets of each Map are compared, then the value Collections are
 *     compared to each other.
 *     The comparison of the keySets and value Collections are added together,
 *     if the return is negative, then the first Object is less than the second.
 *     If the return is positive, then the first Object is greater than the second.
 *     If the return is zero, then the comparison of the keySets is given.</li>
 *
 *     <li>For all other cases, then their string representation's will
 *     be compared for this comparison.</li>
 * </ul>
 * Also, the above can be described with the following:
 * {@code new Compares(false, null, null, null)}
 *
 * @see #Compares(boolean, Comparator, Comparator, BiFunction)
 * @see #Compares(Compares)
 */
public final class Compares implements Comparator<Object> {
    /**
     * The default Compares Comparator.
     */
    public static final Compares DEFAULT = new Compares(false, null, null, null);

    /**
     * The reversed default Compares Comparator.
     */
    public static final Compares REVERSE = new Compares(true, null, null, null);

    private int high = 1;
    private int low = -1;

    private final boolean[] customCompares = new boolean[]{true, true, true};

    private final Comparator<Object> sortComparator;
    private final Comparator<Object> listComparator;

    private final BiFunction<Data<Object>, Data<Object>, Integer> anyCompare;

    /**
     * Create a Compares Comparator.
     *
     * @param flip Whether to flip if {@link #compare(Object, Object)}
     *             returns 1 for greater than and -1 for less than
     *             to -1 for greater than and 1 for less than.
     * @param sortComparator The Comparator that will be used to sort lists when
     *                       comparing Collections.
     * @param listComparator The Comparator that will be used to compare each Object
     *                       of Collections (including Arrays), going up to the last
     *                       index of the smallest Collection.
     * @param anyCompare The BiFunction that is to be used to compare two Objects, given
     *                   that they are not a pair of Collections, Arrays, or Maps.
     *                   Along with neither Object being a boolean or {@code null}.
     */
    public Compares(boolean flip,
                    Comparator<Object> sortComparator,
                    Comparator<Object> listComparator,
                    BiFunction<Data<Object>, Data<Object>, Integer> anyCompare) {
        this((flip?-1:1), (flip?1:-1), sortComparator, listComparator, anyCompare);
    }

    /**
     * Create a clone of a Compares Comparator.
     *
     * @param c The Compares to copy.
     */
    public Compares(Compares c) {
        this(c.high, c.low, c.sortComparator, c.listComparator, c.anyCompare);
    }

    private Compares(int high, int low,
                     Comparator<Object> sortComparator,
                     Comparator<Object> listComparator,
                     BiFunction<Data<Object>, Data<Object>, Integer> anyCompare) {
        this.high = high;
        this.low = low;
        if (sortComparator == null) {
            sortComparator = this;
            customCompares[0] = false;
        }
        this.sortComparator = sortComparator;
        if (listComparator == null) {
            listComparator = this;
            customCompares[1] = false;
        }
        this.listComparator = listComparator;
        if (anyCompare == null) {
            anyCompare = (object1, object2) -> {
                String s1 = String.valueOf(object1.getValue());
                String s2 = String.valueOf(object2.getValue());
                int comparison = s1.compareTo(s2);
                if (comparison > 0) {
                    return this.high;
                } else if (comparison < 0) {
                    return this.low;
                }
                return 0;
            };
            customCompares[2] = false;
        }
        this.anyCompare = anyCompare;
    }

    /**
     * The integer that represents greater than.
     *
     * @return an integer that can either be 1 or -1.
     */
    public int getHigh() {
        return high;
    }

    /**
     * The integer that represents less than.
     *
     * @return an integer that can either be -1 or 1.
     */
    public int getLow() {
        return low;
    }

    /**
     * The Comparator that is used to sort lists when comparing Collections.
     *
     * @return a Comparator used in sorting items in a Collection for this Comparator's
     * Collection comparison system.
     */
    public Comparator<Object> getSortComparator() {
        return sortComparator;
    }

    /**
     * The Comparator that is to be applied to comparing each Object in the Collections
     * that are to be compared.
     *
     * @return a Comparator used in comparing each Object, going up to the last index
     * of the smallest Collection.
     */
    public Comparator<Object> getListComparator() {
        return listComparator;
    }

    /**
     * The function used to compare the two Objects, given that the two Objects are not
     * a pair of Collections, Arrays, or Maps. Also, given that neither Object is a
     * boolean or {@code null}.
     *
     * @return a BiFunction used in comparing the two Objects.
     */
    public BiFunction<Data<Object>, Data<Object>, Integer> getAnyCompare() {
        return anyCompare;
    }

    private boolean collections(Data<Object> object1, Data<Object> object2) {
        return (object1.isList() || object1.isArray() || object1.isSet() || object1.isCollection()) &&
                (object2.isList() || object2.isArray() || object2.isSet() || object2.isCollection());
    }

    private int collectionCompare(Data<Object> object1, Data<Object> object2) {
        List<?> l1;
        List<?> l2;
        if (object1.isList()) {
            l1 = (List<?>) object1.getValue();
        } else if (object1.isSet()) {
            l1 = Arrays.asList(((Set<?>) object1.getValue()).toArray());
        } else if (object1.isArray()) {
            l1 = Arrays.asList((Object[])object1.getValue());
        } else {
            l1 = Arrays.asList(((Collection<?>) object1.getValue()).toArray());
        }
        if (object2.isList()) {
            l2 = (List<?>) object2.getValue();
        } else if (object2.isSet()) {
            l2 = Arrays.asList(((Set<?>) object2.getValue()).toArray());
        } else if (object2.isArray()) {
            l2 = Arrays.asList((Object[])object2.getValue());
        } else {
            l2 = Arrays.asList(((Collection<?>) object2.getValue()).toArray());
        }
        l1.sort(sortComparator);
        l2.sort(sortComparator);
        for (int i = 0; i < Math.min(l1.size(), l2.size()); i++) {
            int comp = listComparator.compare(l1.get(i), l2.get(i));
            if (comp != 0) {
                return comp;
            }
        }
        int finalCompare = Integer.compare(l1.size(), l2.size());
        if (finalCompare > 0) {
            return high;
        } else if (finalCompare < 0) {
            return low;
        }
        return 0;
    }

    private int mapCompare(Data<Object> object1, Data<Object> object2) {
        Map<?, ?> m1 = (Map<?, ?>) object1.getValue();
        Map<?, ?> m2 = (Map<?, ?>) object2.getValue();
        int keysCompared = compare(m1.keySet(), m2.keySet());
        int valuesCompared = compare(m1.values(), m2.values());
        int added = keysCompared + valuesCompared;
        if (added < 0) {
            return low;
        } else if (added > 0) {
            return high;
        }
        return keysCompared;
    }

    private int compare(Data<Object> object1, Data<Object> object2) {
        if (object1.equals(object2)) {
            return 0;
        }
        if (object1.isEmpty() && !object2.isEmpty()) {
            return low;
        } else if (object2.isEmpty() && !object1.isEmpty()) {
            return high;
        }
        if (object1.isBoolean()) {
            Boolean b1 = (Boolean) object1.getValue();
            if (object2.isBoolean()) {
                Boolean b2 = (Boolean) object2.getValue();
                if (b1 == b2) {
                    return 0;
                }
            }
            if (b1) {
                return high;
            }
            return low;
        }
        if (collections(object1, object2)) {
            return collectionCompare(object1, object2);
        }
        if (object1.isMap() && object2.isMap()) {
            return mapCompare(object1, object2);
        }
        return anyCompare.apply(object1, object2);
    }

    /**
     * Compares its two arguments for order. Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the
     * second.
     */
    @Override
    public int compare(Object o1, Object o2) {
        Data<Object> object1 = new Data<>(o1);
        Data<Object> object2 = new Data<>(o2);
        return compare(object1, object2);
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Compares compares)) return false;
        return getHigh() == compares.getHigh() &&
                getLow() == compares.getLow() &&
                Objects.equals(getSortComparator(), compares.getSortComparator()) &&
                Objects.equals(getListComparator(), compares.getListComparator()) &&
                Objects.equals(getAnyCompare(), compares.getAnyCompare());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHigh(), getLow(), getSortComparator(), getListComparator(), getAnyCompare());
    }

    /**
     * Returns a string representation of the object.
     *
     * <br><br>
     *
     * The returned string is as follows:
     * <pre>
     * Compares{
     *     high="The integer that represents greater than",
     *     low="The integer that represents less than",
     *     comparison="either -1, 0, 1 that represent less than, equal to, or greater than",
     *     customSortComparator?="Whether or not this Compares has a custom sort Comparator",
     *     customListComparator?="Whether or not this Compares has a custom Collection Comparator",
     *     customAnyCompare?="Whether or not this Compares has a custom mixed compare BiFunction",
     * }
     * </pre>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Compares{" +
                "high=" + high +
                ", low=" + low +
                ", customSortComparator?=" + customCompares[0] +
                ", customListComparator?=" + customCompares[1] +
                ", customAnyCompare?=" + customCompares[2] +
                '}';
    }
}
