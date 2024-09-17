package arlot.error;

/**
 * Throws a new FileUpdateException.
 * <br>
 * Declares that the stated file couldn't be created/updated.
 */
public class FileUpdateException extends RuntimeException {
    // Default constructor
    public FileUpdateException() {
        super();
    }

    // Constructor that accepts a message
    public FileUpdateException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public FileUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public FileUpdateException(Throwable cause) {
        super(cause);
    }
}
