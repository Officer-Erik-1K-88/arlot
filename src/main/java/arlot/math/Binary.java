package arlot.math;

import arlot.error.BadBinaryValueException;

import java.util.Arrays;

/**
 * Store a binary value.
 */
public class Binary {
    // properties
    /**
     * Holds the string representation of this binary value.
     */
    private StringBuilder bin;
    // constructors

    /**
     * Creates an empty binary value.
     */
    public Binary() {
        bin = new StringBuilder("0000000000000000");
    }

    /**
     * Creates a new binary instance using a string representation of a binary value.
     * @param bin The string representation of a binary value.
     */
    public Binary(String bin) {
        this.bin = new StringBuilder(bin);
        if (!isBinary()) {
            this.bin = new StringBuilder("0000000000000000");
            throw new BadBinaryValueException("Value provided is not binary: "+bin);
        }
        this.bin = new StringBuilder(digit16().toString());
    }

    /**
     * Creates a new binary instance using the binary instance of {@code bin}.
     * @param bin The binary instance to make a relative copy of.
     */
    public Binary(Binary bin) {
        if (!bin.isBinary()) {
            this.bin = new StringBuilder("0000000000000000");
            throw new BadBinaryValueException("Value provided is not binary: "+bin);
        }
        this.bin = new StringBuilder(bin.digit16().toString());
    }

    /**
     * Changes the value of this binary value with {@code bin}.
     * @param bin The binary value to replace this binary value with.
     */
    public void update(String bin) {
        String old = toString();
        this.bin = new StringBuilder(bin);
        if (!isBinary()) {
            this.bin = new StringBuilder(old);
            throw new BadBinaryValueException("Value provided is not binary: "+bin);
        }
        this.bin = new StringBuilder(digit16().toString());
    }

    // methods

    /**
     * Checks if the current binary value is binary.
     * @return True if the binary value is binary.
     */
    protected boolean isBinary() {
        for (char c : toString().toCharArray()) {
            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }

    /**
     * Pads the binary value with leading zeros until its length is a multiple of 16.
     * <br><br>
     * For example if this binary value has a length of 6 then the binary value returned will have a length of 16.
     * <br>
     * For example if this binary value has a length of 17 then the binary value returned will have a length of 32.
     * <br>
     * For example if this binary value has a length of 40 then the binary value returned will have a length of 64.
     * @return A new {@code Binary()}.
     */
    public Binary digit16() {
        Binary newBin = new Binary(toString());
        if (length() % 16 != 0) {
            int length = length();
            int count = 1;
            while (length>=16) {
                count++;
                length -= 16;
            }
            newBin.addZeros((count*16)-length());
        }
        return newBin;
    }

    /**
     * Add an {@code amount} of zeros to the start of this binary value.
     * @param amount The amount of zeros to add to the binary value.
     */
    public void addZeros(int amount) {
        char[] zeros = new char[amount];
        Arrays.fill(zeros, '0');
        bin.insert(0, zeros);
    }

    /**
     * Add a {@code binary} to this binary value.
     * @param binary The binary value to add with this binary value.
     * @return A new {@code Binary()}.
     */
    public Binary add(Binary binary) {
        Binary newBinary = new Binary(toString());
        if (newBinary.length() > binary.length()) {
            binary.addZeros(newBinary.length()-binary.length());
        } else if (newBinary.length() < binary.length()) {
            addZeros(binary.length()-newBinary.length());
        }
        StringBuilder newBin = new StringBuilder();
        char carry;
        if (newBinary.charAt(newBinary.length()-1)=='1' && binary.charAt(newBinary.length()-1)=='1') {
            carry = '1';
            newBin.insert(0, '0');
        } else if (newBinary.charAt(newBinary.length()-1)=='1' || binary.charAt(newBinary.length()-1)=='1') {
            carry = '0';
            newBin.insert(0, '1');
        } else if (newBinary.charAt(newBinary.length()-1)=='0' && binary.charAt(newBinary.length()-1)=='0') {
            carry = '0';
            newBin.insert(0, '0');
        } else {
            throw new BadBinaryValueException("One or both binary values have illegal values.");
        }
        for (int i=newBinary.length()-1; i>=0; i--) {
            if (newBinary.charAt(i)=='1' && binary.charAt(i)=='1') {
                if (carry == '1') {
                    newBin.insert(0, '1');
                } else {
                    newBin.insert(0, '0');
                }
                carry = '1';
            } else if (newBinary.charAt(i)=='1' || binary.charAt(i)=='1') {
                if (carry == '1') {
                    newBin.insert(0, '0');
                } else {
                    newBin.insert(0, '1');
                }
            } else if (newBinary.charAt(i)=='0' && binary.charAt(i)=='0') {
                if (carry == '1') {
                    newBin.insert(0, '1');
                } else {
                    newBin.insert(0, '0');
                }
                carry = '0';
            } else {
                throw new BadBinaryValueException("One or both binary values have illegal values.");
            }
        }
        if (carry == '1') {
            newBin.insert(0, '1');
        }
        return new Binary(newBin.toString());
    }

    /**
     * A NOT gate reverses the logic state.
     * <br>
     * If the input is 1, then the output is 0. If the input is 0, then the output is 1.
     * @return A new {@code Binary()} that is the opposite of the original.
     */
    public Binary not() {
        Binary newBinary = new Binary(toString());
        StringBuilder newBin = new StringBuilder();
        for (int i=newBinary.length()-1; i>=0; i--) {
            if (newBinary.charAt(i)=='1') {
                newBin.insert(0, '0');
            } else {
                newBin.insert(0, '1');
            }
        }
        return new Binary(newBin.toString());
    }

    /**
     * The AND gate is named so because, if 0 is false and 1 is true, the gate acts in the same way as the logical "and" operator.
     * <br>
     * The output is "true" when both inputs are "true."
     * Otherwise, the output is "false."
     * In other words, the output is 1 only when both inputs are 1.
     * <br>
     * The following table represents what is given based on each individual char comparisons:
     * <table>
     *     <tr>
     *         <th>Input 1</th>
     *         <th>Input 2</th>
     *         <th>Output</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>0</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>0</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>1</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>1</td>
     *         <td>1</td>
     *     </tr>
     * </table>
     * @param binary The binary value to check against this binary value.
     * @return A new {@code Binary()}.
     */
    public Binary and(Binary binary) {
        Binary newBinary = new Binary(toString());
        if (newBinary.length() > binary.length()) {
            binary.addZeros(newBinary.length()-binary.length());
        } else if (newBinary.length() < binary.length()) {
            addZeros(binary.length()-newBinary.length());
        }
        StringBuilder newBin = new StringBuilder();
        for (int i=newBinary.length()-1; i>=0; i--) {
            if (newBinary.charAt(i)=='1' && binary.charAt(i)=='1') {
                newBin.insert(0, '1');
            } else {
                newBin.insert(0, '0');
            }
        }
        return new Binary(newBin.toString());
    }

    /**
     * The OR gate gets its name from behaving like the logical inclusive "or."
     * The output is true if one or both of the inputs are true.
     * If both inputs are false, then the output is false.
     * In other words, for the output to be 1, at least one input must be 1.
     * <br>
     * The following table represents what is given based on each individual char comparisons:
     * <table>
     *     <tr>
     *         <th>Input 1</th>
     *         <th>Input 2</th>
     *         <th>Output</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>0</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>0</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>1</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>1</td>
     *         <td>1</td>
     *     </tr>
     * </table>
     * @param binary The binary value to check against this binary value.
     * @return A new {@code Binary()}.
     */
    public Binary or(Binary binary) {
        Binary newBinary = new Binary(toString());
        if (newBinary.length() > binary.length()) {
            binary.addZeros(newBinary.length()-binary.length());
        } else if (newBinary.length() < binary.length()) {
            addZeros(binary.length()-newBinary.length());
        }
        StringBuilder newBin = new StringBuilder();
        for (int i=newBinary.length()-1; i>=0; i--) {
            if (newBinary.charAt(i)=='1' || binary.charAt(i)=='1') {
                newBin.insert(0, '1');
            } else {
                newBin.insert(0, '0');
            }
        }
        return new Binary(newBin.toString());
    }

    /**
     * The XOR (exclusive-OR) gate acts in the same way as the logical "either/or."
     * The output is true if either, but not both, of the inputs are true.
     * The output is false if both inputs are "false" or if both inputs are true.
     * Similarly, the output is 1 if the inputs are different but 0 if the inputs are the same.
     * <br>
     * The following table represents what is given based on each individual char comparisons:
     * <table>
     *     <tr>
     *         <th>Input 1</th>
     *         <th>Input 2</th>
     *         <th>Output</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>0</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>0</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>1</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>1</td>
     *         <td>0</td>
     *     </tr>
     * </table>
     * @param binary The binary value to check against this binary value.
     * @return A new {@code Binary()}.
     */
    public Binary xor(Binary binary) {
        Binary newBinary = new Binary(toString());
        if (newBinary.length() > binary.length()) {
            binary.addZeros(newBinary.length()-binary.length());
        } else if (newBinary.length() < binary.length()) {
            addZeros(binary.length()-newBinary.length());
        }
        StringBuilder newBin = new StringBuilder();
        for (int i=newBinary.length()-1; i>=0; i--) {
            if (
                    (newBinary.charAt(i)=='1' && binary.charAt(i)=='0') ||
                    (binary.charAt(i)=='1' && newBinary.charAt(i)=='0')
            ) {
                newBin.insert(0, '1');
            } else {
                newBin.insert(0, '0');
            }
        }
        return new Binary(newBin.toString());
    }

    /**
     * The NAND (Negated AND) gate operates as an AND gate followed by a NOT gate.
     * It acts in the manner of the logical operation "and" followed by negation.
     * The output is false if both inputs are true. Otherwise, the output is true.
     * <br>
     * The following table represents what is given based on each individual char comparisons:
     * <table>
     *     <tr>
     *         <th>Input 1</th>
     *         <th>Input 2</th>
     *         <th>Output</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>0</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>0</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>1</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>1</td>
     *         <td>0</td>
     *     </tr>
     * </table>
     * @param binary The binary value to check against this binary value.
     * @return A new {@code Binary()}.
     */
    public Binary nand(Binary binary) {
        Binary newBinary = new Binary(toString());
        if (newBinary.length() > binary.length()) {
            binary.addZeros(newBinary.length()-binary.length());
        } else if (newBinary.length() < binary.length()) {
            addZeros(binary.length()-newBinary.length());
        }
        StringBuilder newBin = new StringBuilder();
        for (int i=newBinary.length()-1; i>=0; i--) {
            if (newBinary.charAt(i)=='1' && binary.charAt(i)=='1') {
                newBin.insert(0, '0');
            } else {
                newBin.insert(0, '1');
            }
        }
        return new Binary(newBin.toString());
    }

    /**
     * The NOR (NOT OR) gate is a combination OR gate followed by an inverter.
     * Its output is true if both inputs are false. Otherwise, the output is false.
     * <br>
     * The following table represents what is given based on each individual char comparisons:
     * <table>
     *     <tr>
     *         <th>Input 1</th>
     *         <th>Input 2</th>
     *         <th>Output</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>0</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>0</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>1</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>1</td>
     *         <td>0</td>
     *     </tr>
     * </table>
     * @param binary The binary value to check against this binary value.
     * @return A new {@code Binary()}.
     */
    public Binary nor(Binary binary) {
        Binary newBinary = new Binary(toString());
        if (newBinary.length() > binary.length()) {
            binary.addZeros(newBinary.length()-binary.length());
        } else if (newBinary.length() < binary.length()) {
            addZeros(binary.length()-newBinary.length());
        }
        StringBuilder newBin = new StringBuilder();
        for (int i=newBinary.length()-1; i>=0; i--) {
            if (newBinary.charAt(i)=='0' && binary.charAt(i)=='0') {
                newBin.insert(0, '1');
            } else {
                newBin.insert(0, '0');
            }
        }
        return new Binary(newBin.toString());
    }

    /**
     * The XNOR (exclusive-NOR) gate is a combination of an XOR gate followed by an inverter.
     * Its output is true if the inputs are the same and false if the inputs are different.
     * <br>
     * The following table represents what is given based on each individual char comparisons:
     * <table>
     *     <tr>
     *         <th>Input 1</th>
     *         <th>Input 2</th>
     *         <th>Output</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>0</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>0</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>1</td>
     *         <td>0</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>1</td>
     *         <td>1</td>
     *     </tr>
     * </table>
     * @param binary The binary value to check against this binary value.
     * @return A new {@code Binary()}.
     */
    public Binary xnor(Binary binary) {
        Binary newBinary = new Binary(toString());
        if (newBinary.length() > binary.length()) {
            binary.addZeros(newBinary.length()-binary.length());
        } else if (newBinary.length() < binary.length()) {
            addZeros(binary.length()-newBinary.length());
        }
        StringBuilder newBin = new StringBuilder();
        for (int i=newBinary.length()-1; i>=0; i--) {
            if (
                    (newBinary.charAt(i)=='0' && binary.charAt(i)=='0') ||
                            (binary.charAt(i)=='1' && newBinary.charAt(i)=='1')
            ) {
                newBin.insert(0, '1');
            } else {
                newBin.insert(0, '0');
            }
        }
        return new Binary(newBin.toString());
    }

    /**
     * The left shift inserts the specified number of 0's from the right and let the same amount of leftmost bits fall off.
     * @param amount The amount of zeros to add from the right.
     * @return A new {@code Binary()}.
     */
    public Binary leftShift(int amount) {
        Binary newBin = digit16();
        if (newBin.length()<amount) {
            newBin.bin = new StringBuilder("0000000000000000");
        } else {
            newBin.bin.delete(0, amount);
            newBin.bin.repeat("0", amount);
        }
        return newBin;
    }

    /**
     * The right shift inserts the specified number of 0's from the left and let the same amount of rightmost bits fall off.
     * @param amount The amount of zeros to add from the left.
     * @return A new {@code Binary()}.
     */
    public Binary rightShift(int amount) {
        Binary newBin = digit16();
        if (newBin.length()<amount) {
            newBin.bin = new StringBuilder("0000000000000000");
        } else {
            int lastInd = this.length()-1;
            newBin.bin.delete(lastInd-amount, lastInd);
            char[] zeros = new char[amount];
            Arrays.fill(zeros, '0');
            newBin.bin.insert(0, zeros);
        }
        return newBin;
    }

    /**
     * Returns the length (character count).
     * @return The length of the binary sequence of characters currently represented by this object.
     */
    public int length() {
        return this.bin.length();
    }

    /**
     * Returns the char value in this binary sequence at the specified index.
     * The first char value is at index 0, the next at index 1, and so on, as in array indexing.
     * <br>
     * The index argument must be greater than or equal to 0, and less than the length of this binary sequence.
     * @param index The index of the desired char value.
     * @return The char value at the specified index.
     */
    public char charAt(int index) {
        return this.bin.charAt(index);
    }

    /**
     *
     * @return The binary value as a String.
     */
    @Override
    public String toString() {
        return this.bin.toString();
    }
}
