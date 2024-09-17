package arlot.math;

import arlot.error.BadNumberValueException;

import java.io.Serializable;
import java.util.stream.IntStream;

/**
 * This holds a {@link String} that represents a {@link Numeric}.
 * <h3 id="numericValues">Numeric Values</h3>
 * The numeric value is the statement of a value being a valid {@code Numeric},
 * a value is only a valid {@code Numeric} if it follows the pattern stated in
 * <a href="#validation">Validation</a>.
 *
 * <h4 id="validChars">Valid Chars</h4>
 * There are several valid numeric char values,
 * here they are split into three categories, they include:
 * <h5>1. Digits:</h5>
 * <ul>
 *     <li>
 *         0, 1, 2, 3, 4, 5, 6, 7, 8, 9
 *         <ul>
 *             <li>The valid digits.</li>
 *         </ul>
 *     </li>
 * </ul>
 * <h5>2. Non-Digits:</h5>
 * <ul>
 *     <li>
 *         dot (.)
 *         <ul>
 *             <li>Symbolizes the decimal place's start.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         slash (/)
 *         <ul>
 *             <li>Symbolizes the separation of a fraction.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         dash (-)
 *         <ul>
 *             <li>Symbolizes that the numeric is negative.</li>
 *             <li>And, in scientific notation, symbolizes that the numeric has an `x` amount of spaces to move the decimal in terms of making the numeric smaller.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         plus (+), nothing
 *         <ul>
 *             <li>Symbolizes that the numeric is positive.</li>
 *             <li>And, in scientific notation, symbolizes that the numeric has an `x` amount of spaces to move the decimal in terms of making the numeric larger.</li>
 *         </ul>
 *     </li>
 * </ul>
 * <h5>3. Scientific:</h5>
 * <ul>
 *     <li>
 *         x, X, *
 *         <ul>
 *             <li>Symbolizes that the scientific notation being used is `a`^`x`. Where `a`, in most cases, is 10.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         e, E
 *         <ul>
 *             <li>Symbolizes that the scientific notation being used is e (or E) `+/-` `x`.</li>
 *         </ul>
 *     </li>
 * </ul>
 * Any other chars will be seen as invalid.
 * <h4 id="validation">Validation</h4>
 * When validating if a {@code NumericString} is {@code Numeric} using any of the
 * {@code isNumber} methods in {@link Basic}, a {@code Numeric} is defined as follows:
 * <br>
 * The {@code Numeric} only is allowed one slash (the definition for a fraction),
 * if the slash is present, then there may be two dots and two scientific notation marks.
 * However, if the slash isn't present, then there may only be one dot and one
 * scientific notation mark. If the char is not stated in any of the three categories,
 * then the {@code Numeric} is not a number.
 */
public final class NumericString
        implements Cloneable, Serializable, CharSequence {
    @java.io.Serial
    private static final long serialVersionUID = 310806933L;

    /**
     * Holds the String value to this {@code Numeric} value.
     */
    private StringBuilder numberHold;

    public NumericString() {
        this.numberHold = new StringBuilder("0");
    }

    public NumericString(Object object) {
        update(object);
    }

    public void update(Object object) {
        this.numberHold = new StringBuilder(String.valueOf(object));
        if (this.numberHold.toString().isBlank()) {
            this.numberHold = new StringBuilder("0");
        } else {
            String starttype = object.getClass().getSimpleName();
            if (starttype.isBlank()) {
                starttype = "Object";
            }
            numberError(starttype);
            removeUnnecessaries();
        }
    }

    public int length() {
        return this.numberHold.length();
    }

    public char charAt(int index) {
        return this.numberHold.charAt(index);
    }

    /**
     * Returns {@code true} if this character sequence is empty.
     *
     * @return {@code true} if {@link #length()} is {@code 0}, otherwise
     * {@code false}
     * @implSpec The default implementation returns the result of calling {@code length() == 0}.
     * @since 15
     */
    @Override
    public boolean isEmpty() {
        return CharSequence.super.isEmpty();
    }

    /**
     * Returns a new character sequence that is a subsequence of this sequence.
     * <br>
     * This method is provided so that this class can
     * implement the {@link CharSequence} interface.
     * <br>
     *
     * @param      start   the start index, inclusive.
     * @param      end     the end index, exclusive.
     * @return     the specified subsequence.
     *
     * @throws  IndexOutOfBoundsException
     *          if {@code start} or {@code end} are negative,
     *          if {@code end} is greater than {@code length()},
     *          or if {@code start} is greater than {@code end}
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return new NumericString(this.numberHold.substring(start, end));
    }

    /**
     * Checks if the stored numberHold is a valid number. If not, throws a BadNumberValueException.
     *
     * @param starttype the type of the initial argument
     * @throws BadNumberValueException if the stored numberHold is not a valid number
     */
    private void numberError(String starttype) {
        Basic.numberError(this.numberHold.toString(), starttype);
    }

    /**
     * This function removes the things that are not needed,
     * like zeros at the front of the number,
     * and other things like that.
     */
    private void removeUnnecessaries() {
        this.numberHold = new StringBuilder(
                Basic.reorganize(this.numberHold)
        );
    }

    /**
     * Checks to see if itself is a number.
     * @return {@code true} if itself is a number.
     */
    boolean isNumber() {
        return Basic.isNumber(this.numberHold);
    }

    /**
     * Checks to see if itself is negative.
     * @return {@code true} if the {@code Numeric} is negative.
     */
    public boolean isNegative() {
        return toString().startsWith("-");
    }

    /**
     * Checks to see if itself has decimal places.
     * <br>
     * This check may return false when {@link #isFraction()} is true.
     *
     * @return {@code true} if this {@code Numeric} has decimal places.
     */
    public boolean isDecimal() {
        return toString().contains(".");
    }

    /**
     * Checks to see if itself is a fraction.
     * <br>
     * This check may return false when {@link #isDecimal()} is true.
     *
     * @return {@code true} if this {@code Numeric} is a fraction.
     */
    public boolean isFraction() {
        return toString().contains("/");
    }

    /**
     * Converts this {@code NumericString} to a {@link Fraction}.
     *
     * @return a new {@code Fraction}.
     */
    public Fraction toFraction() {
        return new Fraction(toString());
    }

    /**
     * Converts this {@code NumericString} to a {@link Number}.
     *
     * @return a new {@code Number}.
     */
    public Number toNumber() {
        if (isFraction()) {
            return toFraction().toNumber();
        } else {
            return new Number(toString());
        }
    }

    /**
     * Get this {@code NumericString} as a String.
     * @return The {@code NumericString} as a String.
     */
    @Override
    public String toString() {
        return this.numberHold.toString();
    } // end of toString

    /**
     * Returns a stream of {@code int} zero-extending the {@code char} values
     * from this sequence.  Any char which maps to a
     * surrogate code point is passed through uninterpreted.
     *
     * <p>The stream binds to this sequence when the terminal stream operation
     * commences (specifically, for mutable sequences the spliterator for the
     * stream is <a href="../util/Spliterator.html#binding"><em>late-binding</em></a>).
     * If the sequence is modified during that operation then the result is
     * undefined.
     *
     * @return an IntStream of char values from this sequence
     */
    @Override
    public IntStream chars() {
        return this.numberHold.chars();
    }

    /**
     * Returns a stream of code point values from this sequence.  Any surrogate
     * pairs encountered in the sequence are combined as if by {@linkplain
     * Character#toCodePoint Character.toCodePoint} and the result is passed
     * to the stream. Any other code units, including ordinary BMP characters,
     * unpaired surrogates, and undefined code units, are zero-extended to
     * {@code int} values which are then passed to the stream.
     *
     * <p>The stream binds to this sequence when the terminal stream operation
     * commences (specifically, for mutable sequences the spliterator for the
     * stream is <a href="../util/Spliterator.html#binding"><em>late-binding</em></a>).
     * If the sequence is modified during that operation then the result is
     * undefined.
     *
     * @return an IntStream of Unicode code points from this sequence
     */
    @Override
    public IntStream codePoints() {
        return this.numberHold.codePoints();
    }

    /**
     * Converts this {@code NumericString} to a new character array.
     *
     * @return a newly allocated character array whose length is the length
     * of this {@code NumericString} and whose contents are initialized to contain
     * the character sequence represented by this {@code NumericString}.
     */
    public char[] toCharArray() {
        return this.numberHold.toString().toCharArray();
    }

    public StringBuilder toStringBuilder() {
        return new StringBuilder(toString());
    }

    /**
     * Makes a copy of this {@code NumericString}.
     * @return the clone of this {@code NumericString}.
     */
    @Override
    protected NumericString clone() {
        try {
            NumericString clone = (NumericString) super.clone();
            clone.update(toString());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
