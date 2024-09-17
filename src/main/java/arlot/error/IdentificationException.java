package arlot.error;

/**
 * Throws a new IdentificationException.
 * <br>
 * Declares that something went wrong during the process of identifying an object.
 */
public class IdentificationException extends RuntimeException {
    // Default constructor
    public IdentificationException() {
        super();
    }
    // Constructor that accepts a message
    public IdentificationException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public IdentificationException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public IdentificationException(Throwable cause) {
        super(cause);
    }
}
