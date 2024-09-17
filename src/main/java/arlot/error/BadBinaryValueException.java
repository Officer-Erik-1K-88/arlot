package arlot.error;

/**
 * Throws a new BadBinaryValueException.
 * <br>
 * Declares that some value isn't binary.
 */
public class BadBinaryValueException extends RuntimeException {
    // Default constructor
    public BadBinaryValueException() {
        super();
    }

    // Constructor that accepts a message
    public BadBinaryValueException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public BadBinaryValueException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public BadBinaryValueException(Throwable cause) {
        super(cause);
    }
}
