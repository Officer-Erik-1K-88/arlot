package arlot.math;

import arlot.error.BadNumberValueException;

import java.util.Arrays;
import java.util.Random;

public final class Basic {
    // numeric checking
    /**
     * The chars that are allowed in a {@link NumericString Numeric value}.
     */
    private static final char[] ALLOWED = {
            '0','1','2','3','4','5','6','7','8','9', // numbers 0-9
            '.','-','+','/', // non-numbers
            'x','X','*','e','E'// scientific only
    };
    /**
     * Returns an array of allowed {@link NumericString Numeric values} from zero to nine.
     *
     * @return an array of non-numeric allowed characters
     */
    public static char[] getNumAllowed() {
        char[] ret = new char[10];
        System.arraycopy(ALLOWED, 0, ret, 0, 10);
        return ret;
    }
    /**
     * Returns an array of characters that are allowed in a
     * {@link NumericString Numeric value},
     * excluding numeric characters.
     *
     * @return an array of non-numeric allowed characters
     */
    public static char[] getNonNumAllowed() {
        char[] ret = new char[ALLOWED.length-10];
        System.arraycopy(ALLOWED, 10, ret, 0, ALLOWED.length - 10);
        return ret;
    }
    /**
     * Returns a string representation of the characters that are allowed in a
     * {@link NumericString Numeric value}, formatted for display.
     * The string includes numeric characters
     * (0-9) and non-numeric allowed characters.
     *
     * @return a string representation of allowed characters
     */
    public static String getAllowedAsString() {
        return "[0-9, "+ Arrays.toString(getNonNumAllowed()).replace("[","").replace("]","")+"]";
    }

    /**
     * Checks if a String value can be translated into a {@link NumericString Numeric value}
     *
     * @param number The String value to check.
     * @return True if and only if the chars in the String value are
     * chars in the {@code ALLOWED} variable.
     */
    public static boolean isNumber(String number) {
        int dotCount = 2;
        int slashCount = 1;
        int scientificCount = 2;
        boolean good = false;
        for (int i=0; i<number.length(); i++) {
            for (char c : ALLOWED) {
                if (number.charAt(i) == c) {
                    if (c == '/') {
                        slashCount -= 1;
                    }
                    if (c == '.') {
                        dotCount -= 1;
                    }
                    if (c == 'x' || c == 'X' || c == '*' || c == 'e' || c == 'E') {
                        scientificCount -= 1;
                    }
                    good = true;
                    break;
                }
            }
            if (!good) {
                break;
            }
        }
        if (slashCount < 0) {
            good = false;
        } else if (slashCount == 0) {
            if (dotCount<0) {
                good = false;
            }
            if (scientificCount<0) {
                good = false;
            }
        } else {
            if (dotCount<1) {
                good = false;
            }
            if (scientificCount<1) {
                good = false;
            }
        }
        return good;
    } // end of isNumber
    /**
     * Checks if a StringBuilder value can be translated
     * into a {@link NumericString Numeric value}
     *
     * @param number The StringBuilder value to check.
     * @return True if and only if the chars in the StringBuilder value are
     * chars in the {@code ALLOWED} variable.
     */
    public static boolean isNumber(StringBuilder number) {
        return isNumber(number.toString());
    }
    /**
     * Checks if a {@link Numeric} is a {@link NumericString Numeric value}
     *
     * @param number The {@code Numeric} value to check.
     * @return True if and only if the chars in the {@code Numeric} value are
     * chars in the {@code ALLOWED} variable.
     */
    public static boolean isNumber(Numeric number) {
        return isNumber(number.asString());
    }

    /**
     * Creates and returns a BadNumberValueException with a message indicating
     * that the given argument is not a valid number.
     *
     * @param starttype the type of the initial argument
     * @param argname the name of the argument that caused the exception
     * @return a BadNumberValueException with a detailed message
     */
    private static BadNumberValueException illegalNumber(String starttype, String argname) {
        return new BadNumberValueException("The given `"+starttype+"` to `"+argname+"` is not a `Number`. Must only include "+getAllowedAsString()+".");
    }

    /**
     * Checks if the given Number object is valid.
     * If not, throws a BadNumberValueException.
     *
     * @param num the Number object to be checked
     * @param starttype the type of the initial argument
     * @throws BadNumberValueException if the Number object is not valid
     */
    static void numberError(Number num, String starttype) {
        if (!isNumber(num)) {
            throw illegalNumber(starttype, "num");
        }
    }

    /**
     * Checks if the given string represents a valid number.
     * If not, throws a BadNumberValueException.
     *
     * @param num the string to be checked
     * @param starttype the type of the initial argument
     * @throws BadNumberValueException if the string does not represent a valid number
     */
    static void numberError(String num, String starttype) {
        if (!isNumber(num)) {
            throw illegalNumber(starttype, "num");
        }
    }

    // other

    /**
     * This function translates a long number called {@code index} to be within the limits of
     * {@code min} and {@code length}-1.
     * @param index The number to translate.
     * @param length The amount of items available. (exclusive)
     * @param min The smallest index that is valid. (inclusive)
     * @return The translated index.
     */
    public static long nortic(Long index, Long length, Long min) {
        if (length <= min) {
            throw new IllegalArgumentException("The 'length' value cannot be less than or equal to 'min' value: "+length+" <=! "+min);
        }
        if (index < length && index >= min) {
            return index;
        }
        long newindex;
        try {
            newindex = (long) (
                    ((Math.floor((double) Math.abs(index) /length)+1)
                            *length)
                            +index);
            if (newindex == length) {
                newindex = 0;
            } else {
                if (index >= length) {
                    if (newindex >= length) {
                        newindex = newindex % length;
                    } else {
                        newindex = length - newindex;
                    }
                }
            }
        } catch (Exception e) {
            newindex = min;
        }
        if (newindex > length-1) {
            newindex = length-1;
        } else if (newindex < min) {
            newindex = min;
        }
        return newindex;
    } // end of nortic
    /**
     * This function translates a long number called {@code index} to be within the limits of
     * zero and {@code length}-1.
     * @param index The number to translate.
     * @param length The amount of items available. (exclusive)
     * @return The translated index.
     */
    public static long nortic(Long index, Long length) {
        return nortic(index, length, 0L);
    }

    /**
     * This function translates a Number called {@code index} to be within the limits of
     * {@code min} and {@code length}-1.
     * @param index The Number to translate.
     * @param length The amount of items available. (exclusive)
     * @param min The smallest index that is valid. (inclusive)
     * @return The translated index.
     */
    public static Number nortic(Number index, Number length, Number min) {
        if (length.symbols("le", min)) {
            throw new IllegalArgumentException("The 'length' value cannot be less than or equal to 'min' value: "+length+" <=! "+min);
        }
        if (index.symbols("lt", length) && index.symbols("ge", min)) {
            return index;
        }
        Number newindex = new Number(index);
        try {
            newindex.abs()
                    .divide(length)
                    .floor()
                    .add(1)
                    .multiply(length)
                    .add(index);
            if (newindex.equals(length)) {
                newindex.update(new Number());
            } else {
                if (index.symbols("ge", length)) {
                    if (newindex.symbols("ge", length)) {
                        newindex.update(newindex.mod(length));
                    } else {
                        newindex.update(length.copy().subtract(newindex));
                    }
                }
            }
        } catch (Exception e) {
            newindex.update(min);
        }
        if (newindex.symbols("gt", length.subtract(1))) {
            newindex.update(length);
        } else if (newindex.symbols("lt", min)) {
            newindex.update(min);
        }
        return newindex;
    } // end of Number nortic
    /**
     * This function translates a Number called {@code index} to be within the limits of
     * zero and {@code length}-1.
     * @param index The Number to translate.
     * @param length The amount of items available. (exclusive)
     * @return The translated index.
     */
    public static Number nortic(Number index, Number length) {
        return nortic(index, length, new Number());
    }

    /**
     * The modulo operation finds the remainder of the division of the
     * {@code modulus} and {@code dividend}, where the
     * {@code dividend} is over the {@code modulus}.
     *
     * @param dividend The {@link Number} to be divided by the {@code modulus}.
     * @param modulus (divisor) The {@link Number} to divide the {@code dividend}.
     * @return The remainder from the division of <code>dividend/modulus</code>
     */
    public static Number modulo(Number dividend, Number modulus) {
        Number m = dividend.copy();
        Number ret = dividend.copy();
        m.divide(modulus);
        m.floor();
        m.multiply(modulus);
        ret.subtract(m);
        return ret;
    }

    /**
     * The power function takes a Number ({@code base}) and multiplies itself by a set amount ({@code to}) of times.
     * <br>
     * This function just casts {@code power(Number)} on a copy of {@code base}.
     * <br>
     * For example:
     * <pre>
     * Number base = new Number(2);
     * Number to = new Number(4);
     *
     * System.out.println(Basic.pow(base, to)); // will print out `16` and doesn't change `base`
     * </pre>
     * @param base The Number that will multiply itself.
     * @param to The amount of times {@code base} will multiply itself.
     * @return The Number that is {@code base} to the power of {@code to}.
     */
    public static Number pow(Number base, Number to) {
        base = base.copy();
        if (to.equals(new Number())) {
            base.update(new Number(1));
        } else if (to.equals(new Number(1))) {
            return base;
        } else {
            Number ori;
            if (to.isDecimal()) {
                Fraction fraction = Fraction.parseFraction(to);
                base.update(pow(base, fraction.getNumerator()));
                ori = base.copy();
                for (Number i=new Number(1); i.symbols("lt", fraction.getDenominator()); i.add("1")) {
                    base.divide(ori);
                }
            } else {
                ori = base.copy();
                for (Number i=new Number(1); i.symbols("lt", to); i.add("1")) {
                    base.multiply(ori);
                }
            }
        }
        return base;
    }

    /**
     * This method calculates the
     * <code>n<sup>th</sup> root of x</code> where when another number {@code b} is
     * multiplied by itself {@code n} times, equals {@code a}.
     * @param x The value that <code>b<sup>n</sup></code> equals.
     * @param n The number of times that {@code b} is multiplied to get {@code x}.
     * @return The value of {@code b}
     */
    public static Number root(Number x, Number n) {
        Number base = x.copy();
        if (base.equals(new Number()) || base.equals(new Number(1))) {
            base = new Number();
        } else {
            Number ori;
            if (n.isDecimal()) {
                Fraction fraction = Fraction.parseFraction(n);
                base = root(base, fraction.getNumerator());
                ori = base.copy();
                for (Number i=new Number(1); i.symbols("lt", fraction.getDenominator()); i.add("1")) {
                    base.multiply(ori);
                }
            } else {
                ori = base.copy();
                for (Number i=new Number(1); i.symbols("lt", n); i.add("1")) {
                    base.divide(ori);
                }
            }
        }
        return base;
    }

    /**
     * Generates a random number between {@code min} and {@code max}.
     * @param min The smallest value to be returned.
     * @param max The largest value to be returned.
     * @param allowDecimal Set to true if wishing for decimal places.
     * @return A Number between {@code min} and {@code max}.
     */
    public static Number random(Number min, Number max, boolean allowDecimal) {
        if (max.symbols("le", min)) {
            throw new IllegalArgumentException("The 'max' value cannot be less than or equal to 'min' value: "+max+" <=! "+min);
        }
        Random randex = new Random();
        StringBuilder holder;
        int bound = max.copy().abs().toString().split("\\.")[0].length();
        boolean isNeg = false;
        if (min.isNegative() && (Math.random() < 0.5 || max.symbols("eq", "0"))) {
            bound = min.copy().abs().toString().split("\\.")[0].length();
            isNeg = true;
        }
        int count, actualCount;
        try {
            actualCount = randex.nextInt(1, bound);
        } catch (Exception e) {
            actualCount = bound;
        }
        if (bound <= 4) {
            actualCount = bound;
        }
        int next;
        int repeat = 0;
        Number rand;
        do {
            count = actualCount;
            holder = new StringBuilder();
            if (isNeg) {
                holder.append('-');
            }
            for (int i=1; i<=count; i++) {
                next = randex.nextInt(0, 10);
                holder.append(next);
                if (allowDecimal && (i == (count-4) || count<=4) && randex.nextInt(0, 2) == 1 && !holder.toString().contains(".")) {
                    holder.append('.');
                    count += count+16;
                }
                if (!holder.toString().endsWith(".") && randex.nextInt(0, 10) == 1) {
                    break;
                }
            }
            rand = new Number(holder);
            if (repeat >= 200) {
                break;
            }
            repeat++;
        } while (!(rand.symbols("ge", min) && rand.symbols("le", max)));
        System.out.println(repeat);
        if (rand.symbols("lt", min)) {
            rand.update(min);
        } else if (rand.symbols("gt",max)) {
            rand.update(max);
        }
        return rand;
    }

    /**
     * This function removes things that are not needed in a number,
     * like zeros at the front of the number,
     * and other things like that.
     * <br>
     * This also converts the number out of scientific notation,
     * given that it formatted in scientific notation.
     * @param num The StringBuilder representation of the number.
     */
    public static String reorganize(StringBuilder num) {
        /*if (num.charAt(num.length()-1) == '.') {
            num.deleteCharAt(num.length()-1);
        }*/
        if (num.toString().equals("-0")) {
            return "0";
        }
        num = new StringBuilder(
                Convert.scientific(num.toString())
        );
        StringBuilder ori = new StringBuilder(num.toString());
        if (!num.toString().startsWith("0.")) {
            int differ = 0;
            for (int i=0; i<ori.length(); i++) {
                if (num.charAt(i-differ) != '0') {
                    break;
                }
                num.deleteCharAt(i-differ);
                differ += 1;
            }
        }
        boolean removedot = false;
        if (num.toString().split("\\.").length > 1) {
            if (num.toString().split("\\.")[1].isEmpty()) {
                removedot = true;
            } else {
                try {
                    int h = Integer.parseInt(num.toString().split("\\.")[1]);
                    if (h==0) {
                        removedot = true;
                    } else {
                        int i = num.length()-1;
                        while (num.charAt(i) == '0') {
                            //System.out.println(num+" : "+num.length()+" : "+i);
                            num.deleteCharAt(i);
                            i -= 1;
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        } else {
            removedot = true;
        }
        if (removedot) {
            num = new StringBuilder(num.toString().split("\\.")[0]);
        }
        if (num.isEmpty()) {
            num.append("0");
        }
        if (num.charAt(0) == '.') {
            num.insert(0, "0");
        }
        return num.toString();
    }
    /**
     * This function removes things that are not needed in a number,
     * like zeros at the front of the number,
     * and other things like that.
     * <br>
     * This also converts the number out of scientific notation,
     * given that it formatted in scientific notation.
     * @param num The StringBuilder representation of the number.
     */
    public static String reorganize(String num) {
        return reorganize(new StringBuilder(num));
    }

    /**
     * Adds zero padding to the decimal part of two numeric strings to make their lengths equal.
     * If either of the input strings does not contain a decimal point, ".0" is appended.
     * If the input strings are not valid numbers, an exception is thrown.
     *
     * @param num1 the first numeric string
     * @param num2 the second numeric string
     * @return an array of two strings, with equal-length decimal parts
     * @throws BadNumberValueException if either input string is not a valid number
     */
    public static String[] addZeroPaddingToDec(String num1, String num2) {
        if (!isNumber(num1)) {
            throw illegalNumber("String", "num1");
        }
        if (!isNumber(num2)) {
            throw illegalNumber("String", "num2");
        }
        String[][] holder = new String[2][2];
        if (num1.contains(".") && num2.contains(".")) {
            holder[0] = num1.split("\\.");
            holder[1] = num2.split("\\.");
        } else if (num1.contains(".")) {
            holder[0] = num1.split("\\.");
            holder[1][0] = num2;
            holder[1][1] = "0";
        } else if (num2.contains(".")) {
            holder[0][0] = num1;
            holder[0][1] = "0";
            holder[1] = num2.split("\\.");
        } else {
            return new String[]{num1, num2};
        }
        if (holder[0][1].length() < holder[1][1].length()) {
            int addit = holder[1][1].length()-holder[0][1].length();
            for (int i=0; i<addit; i++) {
                holder[0][1] += "0";
            }
        } else if (holder[0][1].length() > holder[1][1].length()) {
            int addit = holder[0][1].length()-holder[1][1].length();
            for (int i=0; i<addit; i++) {
                holder[1][1] += "0";
            }
        }
        return new String[]{
                holder[0][0]+"."+holder[0][1],
                holder[1][0]+"."+holder[1][1]
        };
    }
}
