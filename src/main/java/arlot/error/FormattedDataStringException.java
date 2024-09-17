package arlot.error;

import arlot.data.Data;

/**
 * Throws a new FormattedDataStringException.
 * <br>
 * Declares that something went wrong durring the formatting of {@link Data} class or
 * the conversion of a formatted {@link Data} class String to a {@link Data} class.
 */
public class FormattedDataStringException extends RuntimeException {
    // Default constructor
    public FormattedDataStringException() {
        super();
    }

    // Constructor that accepts a message
    public FormattedDataStringException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public FormattedDataStringException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public FormattedDataStringException(Throwable cause) {
        super(cause);
    }
}
