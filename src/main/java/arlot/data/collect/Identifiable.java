package arlot.data.collect;

import arlot.error.IdentificationException;

/**
 * States that a class can be identified with an identifier.
 */
public interface Identifiable {
    /**
     * The registered identification of this {@code Identifiable} Object.
     *
     * @return the natural identification of an {@code Identifiable} Object.
     */
    char[] identifier();

    /**
     * Creates the natural identification of this {@code Identifiable} Object.
     * This method should only ever be called by a call
     * to {@link Registry#add(Class, Object...)}.
     *
     * @param identHolder The {@link Registry} that will hold this {@code Identifiable} Object
     *                    in its registry.
     * @throws IdentificationException If this {@code Identifiable} Object has an identifier.
     */
    <V> void createID(Registry<V> identHolder) throws IdentificationException;

    /**
     * Returns the state of if {@link #id()} should throw an {@link IdentificationException}.
     *
     * @apiNote The state of this method should not, but is recommended to,
     * reflect [or determine] the state of whether this {@code Identifiable} Object has
     * an {@link #identifier() identifier}.
     *
     * @return true if the object has an {@link #id() id}, otherwise false.
     */
    boolean hasID();

    /**
     * Returns the unique String identifier associated with the object.
     *
     * @apiNote This method should return the String representation of the unique char array
     * that is recognized as the return of {@link #identifier()}.
     * However, the unique String identifier doesn't need to be equal to
     * <code>String.valueOf(identifier())</code>, but is recommended.
     *
     * @return The object's identifier as a string.
     * @throws IdentificationException If there is no identifier attributed to the object
     * or if {@link #hasID()} is {@code false}.
     */
    default String id() throws IdentificationException {
        if (!hasID()) {
            throw new IdentificationException("The Object has no identification.");
        }
        if (identifier() == null) {
            throw new IdentificationException("The Object has no identifier.");
        }
        return String.valueOf(identifier());
    }
}
