package arlot.math;

import arlot.data.Copyable;

import java.io.Serializable;

/**
 * The reference of a {@link Numeric} value as a fully processed number.
 */
public final class Number extends Numeric implements Cloneable, Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 431331266L;

    /**
     * Holds this {@code Number}'s String value.
     */
    private final NumericString numberHold;
    /**
     * Holds this {@code Number}'s original String value.
     */
    private final NumericString original;
    /**
     * The count of decimal places in a number,
     * given that the calculation ends up having decimal places.
     */
    private int decCount = 100;

    /**
     * Checks to see if itself is negative.
     * @return true if the {@code Number} is negative.
     */
    public boolean isNegative() {
        return toString().startsWith("-");
    }

    /**
     * Checks to see if itself has decimal places.
     * @return true if this {@code Number} has decimal places.
     */
    public boolean isDecimal() {
        return toString().contains(".");
    }

    // Constructors

    /**
     * Creates a new {@code Number} with the value of zero.
     */
    public Number() {
        this.numberHold = new NumericString();
        this.original = new NumericString();
    }

    /**
     * Creates a new {@code Number} using an instance of a {@code Number}.
     * <br>
     * Will do the same as the {@link #copy()} method.
     * @param num The {@code Number} to copy.
     */
    public Number(Number num) {
        this.numberHold = new NumericString(num);
        this.original = new NumericString(num.original);
        this.decCount = num.decCount;
    }
    /**
     * Creates a new Number using a char.
     * @param num The char to use.
     */
    public Number(char num) {
        this.numberHold = new NumericString(num);
        this.original = new NumericString(this.numberHold.toString());
    }
    /**
     * Creates a new Number using a String.
     * @param num The String to use.
     */
    public Number(String num) {
        if (num.isBlank()) {
            num = "0";
        }
        this.numberHold = new NumericString(num);
        this.original = new NumericString(this.numberHold.toString());
    }
    /**
     * Creates a new {@code Number} using a StringBuilder.
     * @param num The StringBuilder to use.
     */
    public Number(StringBuilder num) {
        if (num.isEmpty()) {
            num.append('0');
        }
        this.numberHold = new NumericString(num);
        this.original = new NumericString(this.numberHold.toString());
    }
    /**
     * Creates a new {@code Number} using an integer number.
     * @param num The integer number to use.
     */
    public Number(Integer num) {
        this.numberHold = new NumericString(num);
        this.original = new NumericString(this.numberHold.toString());
    }
    /**
     * Creates a new {@code Number} using a long number.
     * @param num The long number to use.
     */
    public Number(Long num) {
        this.numberHold = new NumericString(num);
        this.original = new NumericString(this.numberHold.toString());
    }
    /**
     * Creates a new {@code Number} using a short number.
     * @param num The short number to use.
     */
    public Number(Short num) {
        this.numberHold = new NumericString(num);
        this.original = new NumericString(this.numberHold.toString());
    }
    /**
     * Creates a new {@code Number} using a double number.
     * @param num The double number to use.
     */
    public Number(Double num) {
        this.numberHold = new NumericString(num);
        this.original = new NumericString(this.numberHold.toString());
    }
    /**
     * Creates a new {@code Number} using a float number.
     * @param num The float number to use.
     */
    public Number(Float num) {
        this.numberHold = new NumericString(num);
        this.original = new NumericString(this.numberHold.toString());
    }

    /**
     * Replace this {@code Number} with a new {@code Number}.
     * This will override the entire {@code Number}.
     * <br>
     * This is including the original number,
     * where it will be replaced with the new {@code Number}.
     * @param num The {@code Number} to replace this {@code Number} with.
     * @return The old {@code Number}.
     */
    public Number update(Number num) {
        Number ret = copy();
        this.numberHold.update(num);
        this.original.update(this.numberHold.toString());
        return ret;
    }

    /**
     *
     * @param num The string of a {@code Number} to replace this {@code Number} with.
     * @return The old {@code Number}.
     * @see #update(Number)
     */
    public Number update(String num) {
        return update(new Number(num));
    }

    /**
     * May not be exactly set to the set length.
     * <br>
     * Actual number is calculated with the following equation:
     * <br>
     * {@code decCount = (length/(100/59))+length}
     * <br>
     * The reason for this calculation is that when doing division,
     * the longest possible decimal count proceeding the dot is
     * about 59 characters when decCount is set to 100.
     * This calculation is to provide a more accurate decimal place length when doing division.
     * @param length The suspected length wanted.
     * @return The actual length that decCont was set to.
     */
    public int setDecLen(int length) {
        double actual = length/(100/59.0);
        decCount = (int) actual+length;
        return decCount;
    }

    /**
     * Unlike {@link #setDecLen(int)}, this method will set decCount,
     * without doing an accuracy correction,
     * to the provided {@code int}.
     *
     * @param length The actual length to be set.
     * @return The old decCount.
     */
    public int setDecCount(int length) {
        int old = decCount;
        decCount = length;
        return old;
    }

    private Number mathSet(Number num) {
        num = num.copy();
        Basic.numberError(num, "Number");
        return num;
    }

    /**
     * Adds a {@code Numeric} value to this {@code Numeric} value.
     *
     * @param num The {@code Numeric} value to add.
     * @return Itself.
     */
    @Override
    public Number add(Numeric num) {
        return add(num.toNumber());
    }
    /**
     * Adds a Number to this Number.
     * @param num The Number to add.
     * @return Itself.
     */
    public Number add(Number num) {
        num = mathSet(num);
        if (isNegative() && num.isNegative()) {
            this.numberHold.update(this.numberHold.toString().replaceFirst("-", ""));
            num = new Number(
                    num.toString().replaceFirst("-","")
            );
            add(num);
            this.numberHold.update(this.numberHold.toStringBuilder().insert(0, "-"));
        } else if (isNegative()) {
            num = num.subtract(new Number(toString().replaceFirst("-","")));
            this.numberHold.update(num.toString());
        } else if (num.isNegative()) {
            return subtract(new Number(num.toString().replaceFirst("-","")));
        } else {
            String num2 = this.numberHold.toString();
            String num3 = num.toString();
            StringBuilder newnum = new StringBuilder();
            String[] holder = Basic.addZeroPaddingToDec(num2, num3);
            num2 = holder[0];
            num3 = holder[1];
            if (num2.length() < num3.length()) {
                newnum = new StringBuilder(num2);
                num2 = num3;
                num3 = newnum.toString();
                newnum = new StringBuilder();
            }
            int carry = 0, nndif=num2.length()-num3.length();
            for (int i=num2.length()-1; i>=0; i--) {
                if (num2.charAt(i) == '.') {
                    newnum.insert(0, ".");
                    continue;
                }
                char c1 = num2.charAt(i);
                char c2 = (i-nndif>=0?num3.charAt(i-nndif):'0');
                int n = carry+Integer.parseInt(String.valueOf(c1))+Integer.parseInt(String.valueOf(c2));
                carry = 0;
                while (n > 9) {
                    carry += 1;
                    n -= 10;
                }
                newnum.insert(0, n);
                if (i-nndif < 0 && carry == 0) {
                    newnum.insert(0, num2.substring(0, i));
                    break;
                }
            }
            if (carry != 0) {
                newnum.insert(0, carry);
            }
            this.numberHold.update(new Number(newnum.toString()).toString());
        }
        return this;//new Number(toString());
    } // end of add
    /**
     * Adds a String to this Number.
     * @param num The String to add.
     * @return Itself.
     */
    public Number add(String num) {
        return add(new Number(num));
    }
    /**
     * Adds an integer number to this Number.
     * @param num The integer number to add.
     * @return Itself.
     */
    public Number add(Integer num) {
        return add(new Number(num));
    }
    /**
     * Adds a long number to this Number.
     * @param num The long number to add.
     * @return Itself.
     */
    public Number add(Long num) {
        return add(new Number(num));
    }
    /**
     * Adds a short number to this Number.
     * @param num The short number to add.
     * @return Itself.
     */
    public Number add(Short num) {
        return add(new Number(num));
    }
    /**
     * Adds a double number to this Number.
     * @param num The double number to add.
     * @return Itself.
     */
    public Number add(Double num) {
        return add(new Number(num));
    }
    /**
     * Adds a float number to this Number.
     * @param num The float number to add.
     * @return Itself.
     */
    public Number add(Float num) {
        return add(new Number(num));
    }

    /**
     * Subtracts a {@code Numeric} value to this {@code Numeric} value.
     *
     * @param num The {@code Numeric} value to subtract.
     * @return Itself.
     */
    @Override
    public Number subtract(Numeric num) {
        return subtract(num.toNumber());
    }
    /**
     * Subtracts a Number to this Number.
     * @param num The Number to subtract.
     * @return Itself.
     */
    public Number subtract(Number num) {
        num = mathSet(num);
        if (isNegative() && num.isNegative()) {
            this.numberHold.update("-"+
                    new Number(
                            num.toString().replaceFirst("-","")
                    ).subtract(
                            new Number(
                                    toString().replaceFirst("-","")
                            )
                    ).toString());
        } else if (isNegative()) {
            this.numberHold.update(this.numberHold.toString().replaceFirst("-", ""));
            this.numberHold.update("-"+add(num).toString());
        } else if (num.isNegative()) {
            return add(new Number(num.toString().replaceFirst("-","")));
        } else {
            StringBuilder num2 = new StringBuilder(this.numberHold.toString());
            StringBuilder num3 = new StringBuilder(num.toString());
            StringBuilder newnum = new StringBuilder();
            String[] holder = Basic.addZeroPaddingToDec(num2.toString(), num3.toString());
            num2 = new StringBuilder(holder[0]);
            num3 = new StringBuilder(holder[1]);
            if (num2.length() < num3.length()) {
                for (int i=num2.length(); i<num3.length(); i++) {
                    num2.insert(0, "0");
                }
                //newnum = new StringBuilder(num2.toString());
                //num2 = new StringBuilder(num3);
                //num3 = newnum.toString();
                //newnum = new StringBuilder();
            } else if (num3.length() < num2.length()) {
                for (int i=num3.length(); i<num2.length(); i++) {
                    num3.insert(0, "0");
                }
            }
            int carry = 0;
            for (int i=num2.length()-1; i>=0; i--) {
                if (num2.charAt(i) == '.') {
                    newnum.insert(0, ".");
                    continue;
                }
                int n1 = Integer.parseInt(String.valueOf(num2.charAt(i)))-carry;
                int n2 = Integer.parseInt(String.valueOf(num3.charAt(i)));
                int n = 0;
                if (n1 < n2) {
                    n = n1+(10-n2);
                    carry = 1;
                } else {
                    n = n1-n2;
                    carry = 0;
                }
                newnum.insert(0, n);
            }
            if (carry != 0) {
                newnum.insert(0, "-");
            }
            this.numberHold.update(new Number(newnum.toString()).toString());
        }
        return this;//new Number(toString());
    } // end of subtract
    /**
     * Subtracts a number string to this Number.
     * @param num The number string to subtract.
     * @return Itself.
     */
    public Number subtract(String num) {
        return subtract(new Number(num));
    }
    /**
     * Subtracts an integer number to this Number.
     * @param num The integer number to subtract.
     * @return Itself.
     */
    public Number subtract(Integer num) {
        return subtract(new Number(num));
    }
    /**
     * Subtracts a long number to this Number.
     * @param num The long number to subtract.
     * @return Itself.
     */
    public Number subtract(Long num) {
        return subtract(new Number(num));
    }
    /**
     * Subtracts a short number to this Number.
     * @param num The short number to subtract.
     * @return Itself.
     */
    public Number subtract(Short num) {
        return subtract(new Number(num));
    }
    /**
     * Subtracts a double number to this Number.
     * @param num The double number to subtract.
     * @return Itself.
     */
    public Number subtract(Double num) {
        return subtract(new Number(num));
    }
    /**
     * Subtracts a float number to this Number.
     * @param num The float number to subtract.
     * @return Itself.
     */
    public Number subtract(Float num) {
        return subtract(new Number(num));
    }

    /**
     * Multiplies a {@code Numeric} value to this {@code Numeric} value.
     *
     * @param num The {@code Numeric} value to multiply.
     * @return Itself.
     */
    @Override
    public Number multiply(Numeric num) {
        return multiply(num.toNumber());
    }
    /**
     * Multiplies a Number to this Number.
     * @param num The Number to multiply.
     * @return Itself.
     */
    public Number multiply(Number num) {
        num = mathSet(num);
        boolean isNeg = false;
        if (num.equals(new Number(-1))) {
            if (isNegative()) {
                this.numberHold.update(this.numberHold.toString().replaceFirst("-", ""));
            } else {
                this.numberHold.update(this.numberHold.toStringBuilder().insert(0, '-'));
            }
            return this;
        }
        if (isNegative() && num.isNegative()) {
            num = new Number(num.toString().replaceFirst("-",""));
            this.numberHold.update(this.numberHold.toString().replaceFirst("-", ""));
        } else if (isNegative()) {
            isNeg = true;
            this.numberHold.update(this.numberHold.toString().replaceFirst("-", ""));
        } else if (num.isNegative()) {
            isNeg = true;
            num = new Number(num.toString().replaceFirst("-",""));
        }

        String num2 = this.numberHold.toString();
        String num3 = num.toString();
        StringBuilder newnum = new StringBuilder();
        String[] holder = Basic.addZeroPaddingToDec(num2, num3);
        num2 = holder[0];
        num3 = holder[1];
        int carry, decamount, decdif=0;
        try {
            decamount = num2.split("\\.")[1].length()+1;
        } catch (ArrayIndexOutOfBoundsException e) {
            decamount = 0;
        }
        for (int i=num2.length()-1; i>=0; i--) {
            if (num2.charAt(i) == '.') {
                decdif = 1;
                continue;
            }
            for (int a=decdif; a < (num2.length()-1)-i; a++) {
                newnum.insert(0, "0");
            }
            carry = 0;
            int n1;
            n1 = Integer.parseInt(String.valueOf(num2.charAt(i)));
            for (int ii=num3.length()-1; ii>=0; ii--) {
                if (num3.charAt(ii) == '.') {
                    continue;
                }
                int n2 = Integer.parseInt(String.valueOf(num3.charAt(ii)));
                int n = (n1*n2)+carry;
                carry = 0;
                while (n > 9 && ii != 0) {
                    carry += 1;
                    n -= 10;
                }
                newnum.insert(0, n);
            }
            if (i != 0) {
                newnum.insert(0, "+");
            }
        }
        holder = newnum.toString().split("\\+");
        num = new Number();
        for (String s : holder) {
            num.add(new Number(s));
        }
        newnum = new StringBuilder(num.toString());
        if (decamount > 0) {
            newnum.insert((newnum.length())-decamount, ".");
        }

        this.numberHold.update(new Number(newnum.toString()).toString());
        if (isNeg) {
            this.numberHold.update(this.numberHold.toStringBuilder().insert(0, "-"));
        }
        return this;//new Number(toString());
    } // end ot multiply
    /**
     * Multiplies a number string to this Number.
     * @param num The number string to multiply.
     * @return Itself.
     */
    public Number multiply(String num) {
        return multiply(new Number(num));
    }
    /**
     * Multiplies an integer number to this Number.
     * @param num The integer number to multiply.
     * @return Itself.
     */
    public Number multiply(Integer num) {
        return multiply(new Number(num));
    }
    /**
     * Multiplies a long number to this Number.
     * @param num The long number to multiply.
     * @return Itself.
     */
    public Number multiply(Long num) {
        return multiply(new Number(num));
    }
    /**
     * Multiplies a short number to this Number.
     * @param num The short number to multiply.
     * @return Itself.
     */
    public Number multiply(Short num) {
        return multiply(new Number(num));
    }
    /**
     * Multiplies a double number to this Number.
     * @param num The double number to multiply.
     * @return Itself.
     */
    public Number multiply(Double num) {
        return multiply(new Number(num));
    }
    /**
     * Multiplies a float number to this Number.
     * @param num The float number to multiply.
     * @return Itself.
     */
    public Number multiply(Float num) {
        return multiply(new Number(num));
    }

    /**
     * divides a {@code Numeric} value to this {@code Numeric} value.
     *
     * @param num The {@code Numeric} value to divide.
     * @return Itself.
     */
    @Override
    public Number divide(Numeric num) {
        return divide(num.toNumber());
    }
    /**
     * divides a Number to this Number.
     * @param num The Number to divide.
     * @return Itself.
     */
    public Number divide(Number num) {
        if (num.equals(new Number())) {
            throw new ArithmeticException(" / by zero");
        } else if (num.equals(new Number(1))) {
            return this;
        }
        num = mathSet(num);
        boolean isNeg = false;
        if (isNegative() && num.isNegative()) {
            num = new Number(num.toString().replaceFirst("-",""));
            this.numberHold.update(this.numberHold.toString().replaceFirst("-", ""));
        } else if (isNegative()) {
            isNeg = true;
            this.numberHold.update(this.numberHold.toString().replaceFirst("-", ""));
        } else if (num.isNegative()) {
            isNeg = true;
            num = new Number(num.toString().replaceFirst("-",""));
        }

        String num2 = this.numberHold.toString();
        String num3 = num.toString().replace(".", "");
        StringBuilder num4 = new StringBuilder();
        StringBuilder newnum = new StringBuilder();
        num = new Number(num3);
        Number num5 = new Number();
        int carry, count = 0, cazcount = 0, counting = 0;
        int wholeamount = num2.split("\\.")[0].length();
        num2 = num2.replace(".", "");
        int i = 0;
        char cnum;
        do {
            cnum = (i>=num2.length()?'0':num2.charAt(i));
            if (cnum == '.') {
                i++;
                continue;
            }
            carry = 0;
            if (i<=num2.length()) {
                num4.append(cnum);
                num5 = new Number(num4);
            }
            if (wholeamount <= 0 && !newnum.toString().contains(".")) {
                if (newnum.isEmpty()) {
                    newnum.append("0.");
                } else {
                    newnum.append(".");
                }
            }
            while (num5.symbols("greater than or equals", num)) {
                num5.subtract(num);
                carry += 1;
                if (carry == 9) {
                    break;
                }
            }
            num4 = new StringBuilder(num5.toString().replace(".", ""));
            //System.out.println(num4);
            if (num4.toString().equals("0")) {
                num4 = new StringBuilder();
            }
            if (carry == 0 && i>num2.length()) {
                counting += 1;
                cazcount += 1;
                num4.append(cnum);
                num5 = new Number(num4);
            }
            if ((i<=num2.length() && !newnum.isEmpty())|| carry != 0 || counting == 2) {
                newnum.append(carry);
                counting = 0;
            }
            if (count >= decCount+num2.length()) {
                break;
            }
            count += 1;
            wholeamount -= 1;
            i++;
        } while (num5.symbols("not equals", "0") || wholeamount > 0);

        this.numberHold.update(new Number(newnum.toString()).toString());
        if (isNeg) {
            this.numberHold.update(this.numberHold.toStringBuilder().insert(0, "-"));
        }
        return this;//new Number(toString());
    } // end of divide
    /**
     * divides a number string to this Number.
     * @param num The number string to divide.
     * @return Itself.
     */
    public Number divide(String num) {
        return divide(new Number(num));
    }
    /**
     * divides an integer number to this Number.
     * @param num The integer number to divide.
     * @return Itself.
     */
    public Number divide(Integer num) {
        return divide(new Number(num));
    }
    /**
     * divides a long number to this Number.
     * @param num The long number to divide.
     * @return Itself.
     */
    public Number divide(Long num) {
        return divide(new Number(num));
    }
    /**
     * divides a short number to this Number.
     * @param num The short number to divide.
     * @return Itself.
     */
    public Number divide(Short num) {
        return divide(new Number(num));
    }
    /**
     * divides a double number to this Number.
     * @param num The double number to divide.
     * @return Itself.
     */
    public Number divide(Double num) {
        return divide(new Number(num));
    }
    /**
     * divides a float number to this Number.
     * @param num The float number to divide.
     * @return Itself.
     */
    public Number divide(Float num) {
        return divide(new Number(num));
    }

    /**
     * The modulo, finds the remainder of this Number and {@code num}.
     * <br>
     * This doesn't change the current number.
     * @param num The Number to divide.
     * @return The remainder as a Number.
     */
    public Number mod(Number num) {
        return Basic.modulo(this, num);
    } // end of modulo
    public Number mod(String num) {
        return mod(new Number(num));
    }
    public Number mod(Integer num) {
        return mod(new Number(num));
    }
    public Number mod(Long num) {
        return mod(new Number(num));
    }
    public Number mod(Short num) {
        return mod(new Number(num));
    }
    public Number mod(Double num) {
        return mod(new Number(num));
    }
    public Number mod(Float num) {
        return mod(new Number(num));
    }

    /**
     * The power function takes this Number and multiplies itself {@code num} times.
     * <br>
     * For example:
     * <pre>
     * Number number = new Number(2);
     * number.power(new Number(4))
     * System.out.println(number); // will print out `16`
     * </pre>
     * @param num The amount of times this Number will multiply itself.
     * @return itself
     */
    public Number power(Number num) {
        update(Basic.pow(this, num));
        return this;
    } // end of power
    /**
     * The power function takes this Number and multiplies itself {@code num} times.
     * @param num The amount of times this Number will multiply itself.
     * @return itself
     */
    public Number power(String num) {
        return power(new Number(num));
    }
    /**
     * The power function takes this Number and multiplies itself {@code num} times.
     * @param num The amount of times this Number will multiply itself.
     * @return itself
     */
    public Number power(Integer num) {
        return power(new Number(num));
    }
    /**
     * The power function takes this Number and multiplies itself {@code num} times.
     * @param num The amount of times this Number will multiply itself.
     * @return itself
     */
    public Number power(Long num) {
        return power(new Number(num));
    }
    /**
     * The power function takes this Number and multiplies itself {@code num} times.
     * @param num The amount of times this Number will multiply itself.
     * @return itself
     */
    public Number power(Short num) {
        return power(new Number(num));
    }
    /**
     * The power function takes this Number and multiplies itself {@code num} times.
     * @param num The amount of times this Number will multiply itself.
     * @return itself
     */
    public Number power(Double num) {
        return power(new Number(num));
    }
    /**
     * The power function takes this Number and multiplies itself {@code num} times.
     * @param num The amount of times this Number will multiply itself.
     * @return itself
     */
    public Number power(Float num) {
        return power(new Number(num));
    }

    /**
     * Gets the square root of this {@code Number}.
     * @return The square root of this {@code Number}.
     */
    public Number sqrt() {
        return Basic.root(this, new Number(2));
    }

    /**
     * The absolute function take this number and makes it positive.
     * @return Itself
     */
    public Number abs() {
        if (isNegative()) {
            this.numberHold.update(this.numberHold.toString().replaceFirst("-", ""));
        }
        return this;
    } // end of abs

    /**
     * Rounds this Number.
     * <br>
     * If {@code len} is positive, then will be in the decimals.
     * However, if {@code len} is negative, then will be in the integers.
     * @param len The amount of number places to keep.
     * @return Itself
     */
    public Number round(int len) {
        StringBuilder num = new StringBuilder();
        if (len < 0) {
            len = Math.abs(len);
            if (new Number(this.numberHold.toString().split("\\.")[0].charAt(len)).symbols("ge", "5")) {
                num.append("0".repeat(len));
                num.append("1");
                add(num.toString());
            }
            this.numberHold.update(this.numberHold.toString().split("\\.")[0]);
            num = new StringBuilder();
            for (int i = 0; i<this.numberHold.length(); i++) {
                if (i>len) {
                    num.append(this.numberHold.charAt(i));
                } else {
                    num.append("0");
                }
            }
            this.numberHold.update(num.toString());
        } else {
            if (this.numberHold.toString().split("\\.").length > 1) {
                if (new Number(this.numberHold.toString().split("\\.")[1].charAt(len)).symbols("ge", "5")) {
                    if (len != 0) {
                        num.append("0.");
                        num.append("0".repeat(Math.max(0, len - 1)));
                    }
                    num.append("1");
                    add(num.toString());
                }
                this.numberHold.update(this.numberHold.toString().substring(0, this.numberHold.length()-(this.numberHold.toString().split("\\.")[1].length()-len)));
            }
        }
        return this;
    } // end of round

    /**
     * Rounds down to the nearest integer.
     * <br>
     * ie Removes the decimal places.
     * @return Itself
     */
    public Number floor() {
        if (this.numberHold.toString().split("\\.").length > 1) {
            this.numberHold.update(this.numberHold.toString().split("\\.")[0]);
        }
        return this;
    } // end of floor

    /**
     * Rounds up to the nearest integer.
     * @return Itself
     */
    public Number ceiling() {
        if (this.numberHold.toString().split("\\.").length > 1) {
            this.numberHold.update(this.numberHold.toString().split("\\.")[0]);
            add("1");
        }
        return this;
    } // end of ceiling

    /**
     * Get this Number as a String.
     * @return The Number as a String.
     */
    @Override
    public String toString() {
        return this.numberHold.toString();
    } // end of toString

    /**
     * This is redundant.
     * @return Itself
     */
    @Override
    public Number toNumber() {
        return this;
    }

    @Override
    public Number that() {
        return this;
    }

    /**
     * Makes a copy of this Number.
     * @return A copy of this Number.
     */
    @Override
    public Number copy() {
        Number ret = new Number(this.numberHold.toString());
        ret.original.update(this.original.toString());
        ret.decCount = this.decCount;
        return ret;
    }

    /**
     * Creates and returns a copy of this {@code Number}.
     *
     * @return A cloned copy of this {@code Number}.
     */
    @Override
    public Number clone() {
        Number clone = (Number) super.clone();
        clone.numberHold.update(toString());
        clone.original.update(original.toString());
        return clone;
    }

    // getters

    /**
     * Get the number that this Number instance was created with.
     * @return A Number instance of the original number provided.
     */
    public Number getOriginal() {
        return new Number(original.toString());
    }

    /**
     * This method gets the decCount.
     * <br>
     * The decCount is the max count of decimal places in a number.
     * @return The decCount.
     */
    public int getDecCount() {
        return decCount;
    }


} // end of Number class
