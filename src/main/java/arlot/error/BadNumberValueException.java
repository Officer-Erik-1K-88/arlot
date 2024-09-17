package arlot.error;

/**
 * Throws a new BadNumberValueException.
 * <br>
 * Declares that some value isn't a number.
 */
public class BadNumberValueException extends RuntimeException {
    // Default constructor
    public BadNumberValueException() {
        super();
    }

    // Constructor that accepts a message
    public BadNumberValueException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public BadNumberValueException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public BadNumberValueException(Throwable cause) {
        super(cause);
    }
}
