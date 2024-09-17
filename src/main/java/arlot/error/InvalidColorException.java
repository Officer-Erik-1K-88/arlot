package arlot.error;

/**
 * Is thrown when a color is not a color.
 */
public class InvalidColorException extends RuntimeException {
    // Default constructor
    public InvalidColorException() {
        super();
    }

    // Constructor that accepts a message
    public InvalidColorException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public InvalidColorException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public InvalidColorException(Throwable cause) {
        super(cause);
    }
}
