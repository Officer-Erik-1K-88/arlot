package arlot.error;

/**
 * Throws a new UpdateDeniedException.
 * <br>
 * Declares that some value cannot be changed.
 */
public class UpdateDeniedException extends IdentificationException {
    // Default constructor
    public UpdateDeniedException() {
        super();
    }

    // Constructor that accepts a message
    public UpdateDeniedException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public UpdateDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public UpdateDeniedException(Throwable cause) {
        super(cause);
    }
}
