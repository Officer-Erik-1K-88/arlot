package arlot.math;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The reference of a {@link Numeric} value as a fully processed fractional number.
 */
public final class Fraction extends Numeric implements Cloneable, Serializable {
    //static
    @Serial
    private static final long serialVersionUID = 253811340L;

    /**
     * This method takes the provided {@link Number} and converts
     * it into a {@link Fraction}
     * where the provided {@link Number} times "10" to the power of
     * decimal places in the {@link Number} is the numerator.
     * <br>
     * And "10" to the power of the number of decimal places in the {@link Number}
     * is the denominator.
     * @param num The {@link Number} to convert into a fraction.
     * @return A fraction.
     */
    public static Fraction parseFraction(Number num) {
        Number top = num.copy();
        Number bottom = new Number(1);
        while (top.toString().contains(".")) {
            top.multiply(10);
            bottom.multiply(10);
        }
        return new Fraction(top, bottom);
    }

    /**
     * Edits each of the provided {@code Fraction}s to have the least common denominator
     * as the {@code denominator}.
     * @param fractions Each {@code Fraction} to find the least common denominator from and edit.
     * @return Each {@code Fraction} that was given
     */
    public static Fraction[] lcd(Fraction... fractions) {
        List<Number> denominators = new ArrayList<>();
        Arrays.stream(fractions).toList().forEach((fraction) -> {
            denominators.add(fraction.getDenominator());
        });
        Number lcd = Advanced.lcm(denominators.toArray(new Number[0]));

        Arrays.stream(fractions).toList().forEach((fraction) -> {
            Number m = lcd.copy().divide(fraction.getDenominator());
            fraction.getDenominator().multiply(m);
            fraction.getNumerator().multiply(m);
        });

        return fractions;
    }

    // properties
    /**
     * This is the {@link Number} of parts of the whole {@link Number}.
     */
    private final Number numerator;
    /**
     * This is the {@link Number} of equal parts the whole {@link Number} is divided into.
     */
    private final Number denominator;
    /**
     * This is the stored negative value of this {@code Fraction}.
     */
    private boolean negative = false;

    // constructors
    public Fraction(Number numerator, Number denominator) {
        this.numerator = numerator.copy();
        this.denominator = denominator.copy();
        removeDecimals();
        negativeCheck();
    }
    public Fraction(String numerator, String denominator) {
        this.numerator = new Number(numerator);
        this.denominator = new Number(denominator);
        removeDecimals();
        negativeCheck();
    }
    public Fraction(Fraction fraction) {
        this.numerator = fraction.numerator.copy();
        this.denominator = fraction.denominator.copy();
        this.negative = fraction.negative;
        removeDecimals();
    }
    public Fraction(String fraction) {
        String[] fract = fraction.split("/");
        numerator = new Number(fract[0]);
        denominator = new Number(fract[1]);
        removeDecimals();
        negativeCheck();
    }

    // methods

    /**
     * Returns the {@link #numerator} that makes up the
     * fractional part of the {@code Fraction}.
     * @return The {@link #numerator}.
     */
    public Number getNumerator() {
        return numerator;
    }
    /**
     * Returns the {@link #denominator} that makes up the
     * fractional part of the {@code Fraction}.
     * @return The {@link #denominator}.
     */
    public Number getDenominator() {
        return denominator;
    }

    /**
     *
     * @return true if this {@code Fraction} is negative, otherwise false.
     */
    @Override
    public boolean isNegative() {
        return negative;
    }

    /**
     * Checks to see if the {@link #numerator} and/or {@link #denominator} is negative
     * and will flip the stored {@link #negative} value.
     */
    private void negativeCheck() {
        if (this.numerator.isNegative()) {
            this.numerator.abs();
            negative = !negative;
        }
        if (this.denominator.isNegative()) {
            this.denominator.abs();
            negative = !negative;
        }
    }

    /**
     * Will convert the {@link #numerator} and/or {@link #denominator} into whole numbers.
     */
    public void removeDecimals() {
        Fraction nume, deno;
        if (numerator.toString().contains(".") || denominator.toString().contains(".")) {
            nume = parseFraction(numerator);
            deno = parseFraction(denominator);

            nume.getNumerator().multiply(deno.getDenominator());
            nume.getDenominator().multiply(deno.getNumerator());
            numerator.update(nume.getNumerator());
            denominator.update(nume.getDenominator());
        }
    }

    /**
     * This method simplifies this {@code Fraction}.
     * @return This {@code Fraction}.
     */
    public Fraction simplify() {
        removeDecimals();
        if (denominator.symbols("eq", "0") && numerator.symbols("ne", "0")) {
            throw new ArithmeticException("A fraction cannot have a denominator of zero when the numerator isn't zero");
        }
        Number gcf = Advanced.gcf(numerator, denominator);
        numerator.divide(gcf);
        denominator.divide(gcf);
        return this;
    }

    private Fraction mathSet(Fraction fraction) {
        Objects.requireNonNull(fraction);
        fraction = fraction.copy();
        removeDecimals();
        fraction.removeDecimals();
        return fraction;
    }

    /**
     * Adds a {@code Numeric} value to this {@code Numeric} value.
     *
     * @param num The {@code Numeric} value to add.
     * @return Itself.
     */
    @Override
    public Fraction add(Numeric num) {
        return add(num.toFraction());
    }
    public Fraction add(Fraction fraction) {
        fraction = mathSet(fraction);
        if (isNegative() && fraction.isNegative()) {
            negative = false;
            fraction.negative = false;
            add(fraction);
            negative = true;
        } else if (isNegative()) {
            negative = false;
            fraction.subtract(this);
            numerator.update(fraction.getNumerator());
            denominator.update(fraction.getDenominator());
            negative = fraction.isNegative();
        } else if (fraction.isNegative()) {
            fraction.negative = false;
            subtract(fraction);
        } else {
            if (denominator.equals(fraction.getDenominator())) {
                numerator.add(fraction.getNumerator());
            } else {
                if (fraction.getDenominator().symbols("eq", "1")) {
                    numerator.add(denominator.copy().multiply(fraction.numerator));
                } else {
                    lcd(this, fraction);
                    numerator.add(fraction.getNumerator());
                }
            }
        }
        return this;
    }

    /**
     * Subtracts a {@code Numeric} value to this {@code Numeric} value.
     *
     * @param num The {@code Numeric} value to subtract.
     * @return Itself.
     */
    @Override
    public Fraction subtract(Numeric num) {
        return subtract(num.toFraction());
    }
    public Fraction subtract(Fraction fraction) {
        fraction = mathSet(fraction);
        if (isNegative() && fraction.isNegative()) {
            negative = false;
            fraction.negative = false;
            subtract(fraction);
            negative = true;
        } else if (isNegative()) {
            negative = false;
            fraction.add(this);
            numerator.update(fraction.getNumerator());
            denominator.update(fraction.getDenominator());
            negative = true;
        } else if (fraction.isNegative()) {
            fraction.negative = false;
            add(fraction);
        } else {
            if (denominator.equals(fraction.getDenominator())) {
                numerator.subtract(fraction.getNumerator());
            } else {
                if (fraction.getDenominator().symbols("eq", "1")) {
                    numerator.subtract(denominator.copy().multiply(fraction.numerator));
                } else {
                    lcd(this, fraction);
                    numerator.subtract(fraction.getNumerator());
                }
            }
        }
        return this;
    }

    /**
     * Multiplies a {@code Numeric} value to this {@code Numeric} value.
     *
     * @param num The {@code Numeric} value to multiply.
     * @return Itself.
     */
    @Override
    public Fraction multiply(Numeric num) {
        return multiply(num.toFraction());
    }
    public Fraction multiply(Fraction fraction) {
        fraction = mathSet(fraction);
        numerator.multiply(fraction.getNumerator());
        denominator.multiply(fraction.getDenominator());
        return this;
    }

    /**
     * divides a {@code Numeric} value to this {@code Numeric} value.
     *
     * @param num The {@code Numeric} value to divide.
     * @return Itself.
     */
    @Override
    public Fraction divide(Numeric num) {
        return divide(num.toFraction());
    }
    public Fraction divide(Fraction fraction) {
        fraction = mathSet(fraction);
        numerator.multiply(fraction.getDenominator());
        denominator.multiply(fraction.getNumerator());
        return this;
    }

    /**
     * This is redundant.
     * @return Itself
     */
    @Override
    public Fraction toFraction() {
        return this;
    }

    /**
     * Converts this {@code Fraction} into decimal format as a {@link Number}.
     * @return a {@link Number} representation of this {@code Fraction}.
     */
    @Override
    public Number toNumber() {
        Number num = numerator.copy();
        num.divide(denominator);
        if (isNegative()) {
            num.multiply(-1);
        }
        return num;
    }

    /**
     * Will give a String that is formatted like the following:
     * <br>
     * {@link #numerator}/{@link #denominator}
     * @return A formatted fraction string.
     */
    @Override
    public String toString() {
        String ret = "";
        if (isNegative()) {
            ret += "-";
        }
        ret += numerator+"/"+denominator;
        return ret;
    }

    /**
     * Makes a copy of this {@code Function}.
     * @return A copy of this {@code Function}.
     */
    @Override
    public Fraction copy() {
        Fraction fraction = new Fraction(numerator, denominator);
        fraction.negative = negative;
        return fraction;
    }

    /**
     * Creates and returns a copy of this {@code Fraction}.
     *
     * @return A cloned copy of this {@code Fraction}.
     */
    @Override
    public Fraction clone() {
        return (Fraction) super.clone();
    }
}
