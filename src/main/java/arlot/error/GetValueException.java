package arlot.error;

/**
 * Throws a new GetValueException.
 * <br>
 * Declares that something went wrong during the process of getting a value.
 */
public class GetValueException extends RuntimeException {
    // Default constructor
    public GetValueException() {
        super();
    }

    // Constructor that accepts a message
    public GetValueException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public GetValueException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public GetValueException(Throwable cause) {
        super(cause);
    }
}
