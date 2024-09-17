package arlot.error;

/**
 * Throws a new FileReadingException.
 * <br>
 * Declares that the stated file couldn't be read correctly.
 */
public class FileReadingException extends RuntimeException {
    // Default constructor
    public FileReadingException() {
        super();
    }

    // Constructor that accepts a message
    public FileReadingException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public FileReadingException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public FileReadingException(Throwable cause) {
        super(cause);
    }
}
