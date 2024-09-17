package arlot.error;

/**
 * Throws a new OutOfRangeException.
 * <br>
 * Declares that some value is outside a stated range.
 */
public class OutOfRangeException extends RuntimeException {
    // Default constructor
    public OutOfRangeException() {
        super();
    }

    // Constructor that accepts a message
    public OutOfRangeException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public OutOfRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public OutOfRangeException(Throwable cause) {
        super(cause);
    }
}
