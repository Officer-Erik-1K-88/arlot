package arlot.data.collect;

import arlot.error.IdentificationException;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Registry holders hold a registry of {@link V} objects where no two objects
 * can have the same id.
 *
 * @param <V> The type of value to make a Registry of.
 */
public class Registry<V> extends ListMap<char[], V> {
    /**
     * The base defined Registry holder.
     * This constant may only hold objects that implement {@link Identifiable}.
     */
    public static final Registry<Identifiable> MAIN = new Registry<>();

    /* ------------------------------------------------------------ */
    // constructors
    /**
     * Creates a new Registry holder.
     */
    public Registry() {
        super();
    }

    /* ------------------------------------------------------------ */
    // checkers

    /**
     * Checks to see if the stated identifier has an object tied to it.
     * @param identifier The char array or string identifier to look for.
     * @return true if the provided identifier can be found in the
     * `id registry` and in the `identifier list`.
     */
    @Override
    public boolean containsKey(Object identifier) {
        if (identifier instanceof char[]) {
            return super.containsKey(identifier);
        } else if (identifier instanceof String) {
            return super.containsKey(((String) identifier).toCharArray());
        }
        return false;
    }

    @Override
    public boolean contains(Object identifier, Object ident) {
        if (!checkValueType(ident)) {
            return false;
        }
        if (containsKey(identifier)) {
            if (identifier instanceof char[]) {
                return Objects.equals(get(identifier), ident);
            } else if (identifier instanceof String) {
                return Objects.equals(get(((String) identifier).toCharArray()), ident);
            }
        }
        return false;
    }

    @Override
    public boolean contains(Object value) {
        if (value instanceof char[] || value instanceof String) {
            return containsKey(value);
        } else if (value instanceof Entry<?, ?> entry) {
            return contains(entry.getKey(), entry.getValue());
        }
        return this.containsValue(value);
    }

    /* ------------------------------------------------------------ */
    // getters

    @Override
    public int indexOf(Object o) {
        if (o instanceof String) {
            return super.indexOf(((String) o).toCharArray());
        } else if (o instanceof char[]) {
            return super.indexOf(o);
        } else if (o instanceof Map.Entry<?,?>) {
            Object key = ((Entry<?, ?>) o).getKey();
            Object value = ((Entry<?, ?>) o).getValue();
            if (contains(key, value)) {
                return indexOf(key);
            }
        } else if (o != null) {
            return super.indexOf(o);
        }
        return -1;
    }

    /**
     * Gets the unique identifier at the provided index.
     * @param index index of the identifier to return
     * @return The string representation of the identifier.
     */
    public String getIdentifierAt(int index) {
        char[] identifier = get(index).getKey();
        return String.valueOf(identifier);
    }

    /**
     * Gets the object that is identified by the provided unique identifier.
     * @param identifier The string or char array identifier to look for.
     * @return The object of the identifier. Or {@link null} if the provided
     * identifier has no object linked to it.
     */
    @Override
    public V get(Object identifier) {
        if (identifier instanceof char[]) {
            return super.get(identifier);
        } else if (identifier instanceof String) {
            return super.get(((String) identifier).toCharArray());
        }
        return null;
    }

    /* ------------------------------------------------------------ */
    // add and insertion
    private V add(BiConsumer<char[], V> operation, Class<? extends V> identClass, Object... parameters)
            throws IdentificationException {
        Class<?>[] paramClasses = new Class[parameters.length];
        for (int i=0; i< parameters.length; i++) {
            paramClasses[i] = parameters[i].getClass();
        }
        try {
            V ident = identClass.getConstructor(paramClasses)
                    .newInstance(parameters);
            if (ident instanceof Identifiable) {
                ((Identifiable) ident).createID(this);

                char[] id = ((Identifiable) ident).identifier();

                operation.accept(id, ident);
            } else {
                operation.accept(createID(), ident);
            }

            return ident;
        } catch (Exception e) {
            throw new IdentificationException("Cannot create identification due to an exception: "+e.getMessage());
        }
    }

    public final V add(Class<? extends V> identClass, Object... parameters)
            throws IdentificationException {
        return add(this::put, identClass, parameters);
    }

    public final V add(int index, Class<? extends V> identClass, Object... parameters)
            throws IdentificationException {
        return add((id, ident) -> put(index, id, ident),
                identClass, parameters);
    }

    /* ------------------------------------------------------------ */
    // removal
    public V remove(String id) {
        return remove(id.toCharArray());
    }

    public V remove(char[] identifier) {
        return remove((Object) identifier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object value) {
        V removed = null;
        char[] identifier;
        if (value instanceof Map.Entry<?,?>) {
            if (contains(value)) {
                Object key = ((Entry<?, ?>) value).getKey();
                V ident = (V) ((Entry<?, ?>) value).getValue();
                if (key instanceof String) {
                    identifier = ((String) key).toCharArray();
                } else {
                    identifier = (char[]) key;
                }
                removed = get(identifier);
                if (!remove(identifier, ident)) {
                    removed = null;
                }
            }
            return removed;
        }
        if (containsKey(value)) {
            if (value instanceof String) {
                identifier = ((String) value).toCharArray();
            } else {
                identifier = (char[]) value;
            }
            removed = super.remove(identifier);
        } else if (containsValue(value)) {
            identifier = getIdentifierAt(indexOf(value)).toCharArray();
            removed = super.remove(identifier);
        }

        return removed;
    }

    @Override
    public boolean remove(Object id, Object ident) {
        char[] identifier;
        if (containsKey(id)) {
            if (id instanceof String) {
                identifier = ((String) id).toCharArray();
            } else {
                identifier = (char[]) id;
            }
            if (ident != null) {
                return super.remove(identifier, ident);
            }
        }

        return false;
    }

    /* ------------------------------------------------------------ */
    // ID Creation
    /**
     * Creates an identifier to the provided length;
     * it comprises only the chars provided by {@code chars}.
     * <br>
     * All created identifiers are unique,
     * meaning that each identifier can only be used for one object.
     * @param chars The characters that are allowed in the created identifier.
     * @param length The length of the created identifier.
     * @return The created identifier.
     */
    public char[] createIdentifier(char[] chars, int length) {
        char[] identifier = new char[length];

        Random random = new Random();
        do {
            for (int i = 0; i<identifier.length; i++) {
                identifier[i] = chars[random.nextInt(chars.length)];
            }
        } while (contains(identifier));
        return identifier;
    }

    /**
     * This method will create an identifier.
     * <br><br>
     * It is recommended to have a 20-character long identifier created,
     * as well as, being recommended of only using the chars given as the first
     * argument of the {@link Function}.
     * <br><br>
     * The first {@link Function} argument is an array of {@link Character}s
     * that are recommended for use in the identifier,
     * it is numbers '0' to '9' and both uppercase and lowercase letters from 'A' to 'Z'.
     * <br><br>
     * The {@link Function} must return a char Array that is the created identifier.
     * <br><br>
     * If the provided {@link Function} is {@code null},
     * then the identifier will be created with {@link #createIdentifier(char[], int)}.
     * <br><br>
     * The method will continue to run till the identifier to be returned is unique.
     * <br>
     * All created identifiers must be unique,
     * meaning that each identifier can only be used for one object.
     * @param idFunction The function to apply to the creation of unique identifiers.
     * @return The created identifier.
     */
    public final char[] createID(Function<char[], char[]> idFunction) {
        if (idFunction == null) {
            idFunction = (c) -> createIdentifier(c, 20);
        }
        Objects.requireNonNull(idFunction);
        // valid identifier chars
        char[] chars = new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // numbers 0-9
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', // letters a-z, lower case
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' // letters A-Z, upper case
        };

        char[] id;
        do {
            id = idFunction.apply(chars);
        } while(contains(id));

        return id;
    }

    public char[] createID() {
        return createID(null);
    }

    /* ------------------------------------------------------------ */
    // Unsupported Operations
}
