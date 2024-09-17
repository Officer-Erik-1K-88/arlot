package arlot.math;

import arlot.data.Copyable;

import java.io.Serializable;
import java.util.Objects;

/**
 * States that an {@link Object} is a number.
 * <br>
 * All {@code Numeric values} are both {@link Serializable} and {@link Cloneable}.
 * Also, {@code Numeric values} extend {@link java.lang.Number java.lang.Number}.
 * @see Number
 * @see Fraction
 * @see NumericString
 */
public abstract class Numeric extends java.lang.Number
        implements Comparable<Numeric>, Copyable<Numeric>, Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 110420804L;

    // getters
    /**
     * Checks to see if this {@code Numeric} value is negative.
     * @return true if the {@code Numeric} value is negative, otherwise false.
     */
    public abstract boolean isNegative();

    /**
     * This method gets the {@link String} value of this {@code Numeric} value
     * based on the conversion to a {@link Number} and the return of it's {@link #toString()}
     * operation.
     * <br>
     * If, for what ever reason, an exception occurs during tha above operation,
     * then this method will return {@link #toString()}.
     * @return The {@link String} value of this {@code Numeric} value.
     */
    public final String asString() {
        try {
            return toNumber().toString();
        } catch (Exception e) {
            return toString();
        }
    }

    // math
    /**
     * Adds a {@code Numeric} value to this {@code Numeric} value.
     * @param num The {@code Numeric} value to add.
     * @return Itself.
     */
    public abstract Numeric add(Numeric num);

    /**
     * Subtracts a {@code Numeric} value to this {@code Numeric} value.
     * @param num The {@code Numeric} value to subtract.
     * @return Itself.
     */
    public abstract Numeric subtract(Numeric num);

    /**
     * Multiplies a {@code Numeric} value to this {@code Numeric} value.
     * @param num The {@code Numeric} value to multiply.
     * @return Itself.
     */
    public abstract Numeric multiply(Numeric num);

    /**
     * divides a {@code Numeric} value to this {@code Numeric} value.
     * @param num The {@code Numeric} value to divide.
     * @return Itself.
     */
    public abstract Numeric divide(Numeric num);

    // compare
    /**
     * Checks this {@code Numeric} value against another {@code Numeric} value.
     * <br><br>
     * The following is true about the returned boolean array:
     * <br>
     * <table>
     *     <tr>
     *         <th>Index</th>
     *         <th>Outputs</th>
     *     </tr>
     *     <tr>
     *         <td>0</td>
     *         <td>Equals</td>
     *     </tr>
     *     <tr>
     *         <td>1</td>
     *         <td>Not Equals</td>
     *     </tr>
     *     <tr>
     *         <td>2</td>
     *         <td>Less Than</td>
     *     </tr>
     *     <tr>
     *         <td>3</td>
     *         <td>Less Than or Equals</td>
     *     </tr>
     *     <tr>
     *         <td>4</td>
     *         <td>Greater Than</td>
     *     </tr>
     *     <tr>
     *         <td>5</td>
     *         <td>Greater Than or Equals</td>
     *     </tr>
     * </table>
     * @param num The {@code Numeric} value to check against.
     * @return An Array of boolean values.
     */
    public boolean[] symbols(Numeric num) {
        Objects.requireNonNull(num);
        boolean eq = false; // equals
        boolean ne = false; // not equals
        boolean lt = false; // less than
        boolean le = false; // less than or equals
        boolean gt = false; // greater than
        boolean ge = false; // greater than or equals
        boolean[] nubool = new boolean[] {isNegative(), num.isNegative()};
        String num2 = toNumber().toString().replaceFirst("-", "");
        String num3 = num.toNumber().toString().replaceFirst("-", "");
        if (nubool[1] && !nubool[0]) {
            ne = true;
            gt = true;
            ge = true;
        } else if (nubool[0] && !nubool[1]) {
            ne = true;
            lt = true;
            le = true;
        } else {
            if (num2.split("\\.")[0].length() > num3.split("\\.")[0].length()) {
                ne = true;
                if (nubool[0]) {
                    lt = true;
                    le = true;
                } else {
                    gt = true;
                    ge = true;
                }
            } else if (num2.split("\\.")[0].length() < num3.split("\\.")[0].length()) {
                ne = true;
                if (nubool[0]) {
                    gt = true;
                    ge = true;
                } else {
                    lt = true;
                    le = true;
                }
            } else {
                String longer;
                if (num2.length()<num3.length()) {
                    longer = num3;
                } else {
                    longer = num2;
                }
                for (int i=0; i<longer.length(); i++) {
                    if (i == num3.length()) {
                        ne = true;
                        if (nubool[0]) {
                            lt = true;
                            le = true;
                        } else {
                            gt = true;
                            ge = true;
                        }
                        break;
                    } else if (i == num2.length()) {
                        ne = true;
                        if (nubool[0]) {
                            gt = true;
                            ge = true;
                        } else {
                            lt = true;
                            le = true;
                        }
                        break;
                    }
                    if (num2.charAt(i) != num3.charAt(i)) {
                        ne = true;
                        if (num2.charAt(i) == '.') {
                            lt = true;
                            le = true;
                        } else if (num3.charAt(i) == '.') {
                            gt = true;
                            ge = true;
                        } else {
                            if (Integer.parseInt(String.valueOf(num2.charAt(i))) > Integer.parseInt(String.valueOf(num3.charAt(i)))) {
                                if (nubool[0]) {
                                    lt = true;
                                    le = true;
                                } else {
                                    gt = true;
                                    ge = true;
                                }
                            } else {
                                if (nubool[0]) {
                                    gt = true;
                                    ge = true;
                                } else {
                                    lt = true;
                                    le = true;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (!ne) {
            eq = true;
            le = true;
            ge = true;
        }
        return new boolean[]{eq, ne, lt, le, gt, ge};
    }
    /**
     * Checks this {@code Numeric} value against a {@link Number} String.
     * @param num The {@link Number} to check against.
     * @return An Array of boolean values.
     * @see #symbols(Numeric)
     */
    public boolean[] symbols(String num) {
        return symbols(new Number(num));
    }
    /**
     * Checks this {@code Numeric} value against another {@code Numeric} value.
     * <br><br>
     * The boolean returned is based upon the {@code type},
     * the {@code type} can be any of the following:
     * <br>
     * <ul>
     *     <li>equals, equal, eq</li>
     *     <li>not equals, not equal, ne</li>
     *     <li>less than, lt</li>
     *     <li>less than or equals, less than or equal, le</li>
     *     <li>greater than, gt</li>
     *     <li>greater than or equals, greater than or equal, ge</li>
     * </ul>
     * @param type The method type of the check.
     * @param num The {@code Numeric} value to check against.
     * @return A boolean value.
     */
    public boolean symbols(String type, Numeric num) {
        type = type.toLowerCase();
        boolean[] base = symbols(num);
        boolean ret = false;
        switch (type) {
            case "equals", "equal", "eq" -> ret = base[0];
            case "not equals", "not equal", "ne" -> ret = base[1];
            case "less than", "lt" -> ret = base[2];
            case "less than or equals", "less than or equal", "le" -> ret = base[3];
            case "greater than", "gt" -> ret = base[4];
            case "greater than or equals", "greater than or equal", "ge" -> ret = base[5];
        }
        return ret;
    } // end of typed symbols
    /**
     * Checks this {@code Numeric} value against a {@link Number} String.
     * <br>
     * The boolean returned is based upon the {@code type}
     * @param type The method type of the check.
     * @param num The {@link Number} to check against.
     * @return A boolean value.
     * @see #symbols(String, Numeric)
     */
    public boolean symbols(String type, String num) {
        return symbols(type, new Number(num));
    }

    /**
     * Checks if this {@code Numeric} value is equal to {@code num}.
     * @param num The value to check against.
     * @return {@code true} if this {@code Numeric} value and {@code num} are equal.
     */
    @Override
    public boolean equals(Object num) {
        if (num == null) return false;
        boolean equal = super.equals(num);
        if (!equal) {
            equal = Objects.equals(toString(), num.toString());
            if (!equal && num instanceof Numeric) {
                equal = Objects.equals(asString(), ((Numeric) num).asString());
                if (!equal) {
                    equal = symbols("eq", (Numeric) num);
                }
            }
        }
        return equal;
    }

    /**
     * Compares this {@code Numeric} with the specified {@code Numeric} for order.
     * Returns a negative integer, zero, or a positive integer
     * as this {@code Numeric} is less than, equal to, or greater than
     * the specified {@code Numeric}.
     *
     * @param num the {@code Numeric} to be compared.
     * @return a negative integer, zero, or a positive integer as this {@code Numeric}
     * is less than, equal to, or greater than the specified {@code Numeric}.
     */
    @Override
    public int compareTo(Numeric num) {
        boolean[] symbols = symbols(num);
        if (symbols[0]) {
            return 0;
        } else if (symbols[2]) {
            return -1;
        } else if (symbols[4]) {
            return 1;
        }
        throw new RuntimeException("A problem occurred.");
    }


    // conversions
    /**
     * Returns the value of the specified {@code Numeric} value as a {@code byte}.
     * <br>
     * This method uses {@link #toString()} directly.
     * @return the numeric value represented by this {@code Numeric} value after conversion
     * to type {@code byte}.
     */
    @Override
    public byte byteValue() {
        return Byte.parseByte(toString());
    }

    /**
     * Returns the value of the specified {@code Numeric} value as an {@code int}.
     * <br>
     * This method uses {@link #asString()}.
     * @return the numeric value represented by this {@code Numeric} value after conversion
     * to type {@code int}.
     */
    @Override
    public int intValue() {
        return Integer.parseInt(asString());
    }

    /**
     * Returns the value of the specified {@code Numeric} value as a {@code long}.
     * <br>
     * This method uses {@link #asString()}.
     * @return the numeric value represented by this {@code Numeric} value after conversion
     * to type {@code long}.
     */
    @Override
    public long longValue() {
        return Long.parseLong(asString());
    }

    /**
     * Returns the value of the specified {@code Numeric} value as a {@code short}.
     * <br>
     * This method uses {@link #asString()}.
     * @return the numeric value represented by this {@code Numeric} value after conversion
     * to type {@code short}.
     */
    @Override
    public short shortValue() {
        return Short.parseShort(asString());
    }

    /**
     * Returns the value of the specified {@code Numeric} value as a {@code float}.
     * <br>
     * This method uses {@link #asString()}.
     * @return the numeric value represented by this {@code Numeric} value after conversion
     * to type {@code float}.
     */
    @Override
    public float floatValue() {
        return Float.parseFloat(asString());
    }

    /**
     * Returns the value of the specified {@code Numeric} value as a {@code double}.
     * <br>
     * This method uses {@link #asString()}.
     * @return the numeric value represented by this {@code Numeric} value after conversion
     * to type {@code double}.
     */
    @Override
    public double doubleValue() {
        return Double.parseDouble(asString());
    }

    /**
     * Allows for this {@code Numeric} value to be converted into a {@link Number}.
     * @return a {@link Number} representation of this {@code Numeric} value.
     */
    public abstract Number toNumber();

    /**
     * Converts this {@code Numeric} value to a {@link Fraction}.
     * <br>
     * This method takes the {@link #toNumber()} method of this {@code Numeric} value as
     * the numerator and then puts a "1" as the denominator.
     * @return a {@link Fraction} representation of this {@code Numeric} value.
     */
    public Fraction toFraction() {
        return new Fraction(toNumber(), new Number(1));
    }

    /**
     * Converts this {@code Numeric} value to a {@link String}.
     * <br>
     * Should not return the {@code toString()} provided by {@link Object}.
     * @return a {@link String} representation of this {@code Numeric} value.
     */
    @Override
    public abstract String toString();

    /**
     * Get this Numeric value.
     *
     * @return Itself.
     */
    @Override
    public Numeric that() {
        return this;
    }

    // copying and cloning
    /**
     * Makes a copy of this {@code Numeric} value.
     * @return A copy of this {@code Numeric} value.
     */
    @Override
    public abstract Numeric copy();

    /**
     * Creates and returns a copy of this {@code Numeric} value.
     * <br>
     * If {@code clone()} given by {@link Object} was to throw an exception,
     * then this method will behave like {@link #copy()}.
     * @return A cloned copy of this {@code Numeric} value.
     */
    @Override
    public Numeric clone() {
        try {
            return (Numeric) super.clone();
        } catch (Exception e) {
            return copy();
        }
    }
}
