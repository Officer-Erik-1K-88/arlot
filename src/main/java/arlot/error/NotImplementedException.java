package arlot.error;

public class NotImplementedException extends RuntimeException {
    // Default constructor
    public NotImplementedException() {
        super();
    }

    // Constructor that accepts a message
    public NotImplementedException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public NotImplementedException(Throwable cause) {
        super(cause);
    }
}
