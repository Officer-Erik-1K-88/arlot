package arlot.data.collect;

import arlot.data.Compares;

import java.io.IOException;
import java.nio.CharBuffer;

public class DataString implements Appendable, java.io.Serializable,
        Comparable<DataString>, CharSequence {
    private StringBuilder value;

    /**
     * Returns the length (character count).
     *
     * @return the length of the sequence of characters currently represented by this object
     */
    @Override
    public int length() {
        return value.length();
    }

    /**
     * Returns the {@code char} value in this sequence at the specified index.
     * The first {@code char} value is at index {@code 0}, the next at index
     * {@code 1}, and so on, as in array indexing.
     * <p>
     * The index argument must be greater than or equal to
     * {@code 0}, and less than the length of this sequence.
     *
     * <p>If the {@code char} value specified by the index is a
     * <a href="Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param      index   the index of the desired {@code char} value.
     * @return     the {@code char} value at the specified index.
     * @throws     IndexOutOfBoundsException  if {@code index} is
     *             negative or greater than or equal to {@code length()}.
     */
    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    /**
     * Returns a new {@code CharSequence} that contains a subsequence of
     * characters currently contained in this sequence. The
     * subSequence begins at the specified {@code start} and
     * extends to the character at index {@code end - 1}.
     *
     * @param      start    The beginning index, inclusive.
     * @param      end      The ending index, exclusive.
     * @return     The new {@code CharSequence}.
     * @throws     StringIndexOutOfBoundsException  if {@code start}
     *             or {@code end} are negative or greater than
     *             {@code length()}, or {@code start} is
     *             greater than {@code end}.
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    /**
     * Compares this object with the specified object for order. Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(DataString o) {
        return Compares.DEFAULT.compare(this, o);
    }

    public DataString append(String s) {
        value.append(s);
        return this;
    }

    public DataString append(StringBuilder sb) {
        value.append(sb);
        return this;
    }

    public DataString append(DataString ds) {
        value.append(ds);
        return this;
    }

    /**
     * Appends the specified character sequence to this {@code Appendable}.
     *
     * <p> Depending on which class implements the character sequence
     * {@code csq}, the entire sequence may not be appended.  For
     * instance, if {@code csq} is a {@link CharBuffer} then
     * the subsequence to append is defined by the buffer's position and limit.
     *
     * @param csq The character sequence to append.  If {@code csq} is
     *            {@code null}, then the four characters {@code "null"} are
     *            appended to this Appendable.
     * @return A reference to this {@code Appendable}
     * @throws IOException If an I/O error occurs
     */
    @Override
    public DataString append(CharSequence csq) throws IOException {
        value.append(csq);
        return this;
    }

    /**
     * Appends a subsequence of the specified character sequence to this
     * {@code Appendable}.
     *
     * <p> An invocation of this method of the form {@code out.append(csq, start, end)}
     * when {@code csq} is not {@code null}, behaves in
     * exactly the same way as the invocation
     *
     * <pre>
     *     out.append(csq.subSequence(start, end)) </pre>
     *
     * @param csq   The character sequence from which a subsequence will be
     *              appended.  If {@code csq} is {@code null}, then characters
     *              will be appended as if {@code csq} contained the four
     *              characters {@code "null"}.
     * @param start The index of the first character in the subsequence
     * @param end   The index of the character following the last character in the
     *              subsequence
     * @return A reference to this {@code Appendable}
     * @throws IndexOutOfBoundsException If {@code start} or {@code end} are negative, {@code start}
     *                                   is greater than {@code end}, or {@code end} is greater than
     *                                   {@code csq.length()}
     * @throws IOException               If an I/O error occurs
     */
    @Override
    public DataString append(CharSequence csq, int start, int end) throws IOException {
        value.append(csq, start, end);
        return this;
    }

    /**
     * Appends the specified character to this {@code Appendable}.
     *
     * @param c The character to append
     * @return A reference to this {@code Appendable}
     * @throws IOException If an I/O error occurs
     */
    @Override
    public DataString append(char c) throws IOException {
        value.append(c);
        return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
