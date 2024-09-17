package arlot.error;

/**
 * Thrown to indicate that the an {@code Object} implementing
 * {@link arlot.data.Copyable Copyable} cannot be duplicated.
 */
public class NotDuplicatableException extends NotCloneableException {

    /**
     * Constructs a {@code NotDuplicatableException} with no
     * detail message.
     */
    public NotDuplicatableException() {
        super();
    }

    /**
     * Constructs a {@code NotDuplicatableException} with the
     * specified detail message.
     *
     * @param s the detail message.
     */
    public NotDuplicatableException(String s) {
        super(s);
    }

    // Constructor that accepts a cause
    public NotDuplicatableException(Throwable cause) {
        super(cause);
    }
}
