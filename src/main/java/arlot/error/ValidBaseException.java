package arlot.error;

/**
 * Throws a new ValidBaseException.
 * <br>
 * Declares that the stated base conversion of a string to number
 * is outside the valid count of characters in a character array.
 */
public class ValidBaseException extends RuntimeException {
    // Default constructor
    public ValidBaseException() {
        super();
    }

    // Constructor that accepts a message
    public ValidBaseException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public ValidBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public ValidBaseException(Throwable cause) {
        super(cause);
    }
}
