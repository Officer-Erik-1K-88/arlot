package arlot.error;

/**
 * Thrown to indicate that the an {@code Object} cannot be copied.
 *
 * @see arlot.data.Copyable
 */
public class NotCloneableException extends Exception {

    /**
     * Constructs a {@code NotCloneableException} with no
     * detail message.
     */
    public NotCloneableException() {
        super();
    }

    /**
     * Constructs a {@code NotCloneableException} with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public NotCloneableException(String s) {
        super(s);
    }

    // Constructor that accepts a cause
    public NotCloneableException(Throwable cause) {
        super(cause);
    }
}
