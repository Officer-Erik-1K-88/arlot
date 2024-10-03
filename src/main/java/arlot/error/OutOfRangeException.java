package arlot.error;

/**
 * Throws a new OutOfRangeException.
 * <br>
 * Declares that some value is outside a stated range.
 */
public class OutOfRangeException extends IndexOutOfBoundsException {
    /**
     * Constructs an {@code OutOfRangeException} with no detail message.
     */
    public OutOfRangeException() {
        super();
    }

    /**
     * Constructs an {@code OutOfRangeException} with the specified detail
     * message.
     *
     * @param s the detail message
     */
    public OutOfRangeException(String s) {
        super(s);
    }

    /**
     * Constructs a new {@code OutOfRangeException} class with an
     * argument indicating the illegal value.
     *
     * <p>The value is included in this exception's detail message. The
     * exact presentation format of the detail message is unspecified.
     *
     * @param value the illegal value.
     * @since 9
     */
    public OutOfRangeException(int value) {
        super("Value out of range: " + value);
    }

    /**
     * Constructs a new {@code OutOfRangeException} class with an
     * argument indicating the illegal value.
     *
     * <p>The value is included in this exception's detail message. The
     * exact presentation format of the detail message is unspecified.
     *
     * @param value the illegal value.
     * @since 16
     */
    public OutOfRangeException(long value) {
        super("Value out of range: " + value);
    }
}
