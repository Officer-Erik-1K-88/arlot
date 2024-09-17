package arlot.math;

import arlot.error.BadBinaryValueException;
import arlot.error.OutOfRangeException;
import arlot.error.ValidBaseException;

public final class Convert {
    public static final char[] VALIDCHARS = new char[]{
            '0','1','2','3','4','5','6','7','8','9', // numbers 0-9
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' // letters a-z
    };
    public static Number toNumber(String num, int base) {
        if (base < 2 || base > VALIDCHARS.length) {
            throw new ValidBaseException("The base of Convert.toNumber must be between 2 and "+VALIDCHARS.length+": "+base);
        }
        if (base==10) {
            return new Number(num);
        } else if (base==2) {
            return outBinary(new Binary(num));
        } else {
            Number number = new Number();
            int countdown = num.length()+1;
            boolean notgood;
            for (int i=0; i<num.length(); i++) {
                countdown -= 1;
                notgood = true;
                for (char c : VALIDCHARS) {
                    if (num.charAt(i) == c) {
                        notgood = false;
                        break;
                    }
                }
                if (notgood) {
                    throw new OutOfRangeException("Number value given to Convert.toNumber was out of range: "+num);
                }
                number.add(
                        new Number(num.charAt(i)).multiply(
                                new Number(base).power(Integer.toString(countdown))
                        )
                );
            }
            return number;
        }
    }

    /**
     * Converts a Number into binary.
     * @param num A Number to be converted into binary.
     * @return A binary representation of the provided Number.
     */
    public static Binary toBinary(Number num) {
        StringBuilder bin = new StringBuilder();
        while (num.symbols("gt", "0")) {
            bin.insert(0, num.mod("2").toString());
            num.divide("2").floor();
        }
        return new Binary(bin.toString());
    } // end of toBinary

    /**
     * Converts a String into binary.
     * This function isn't suitable for converting numbers to binary.
     * Refer to {@link #toBinary(Number)} for converting numbers.
     * @param value A String to be converted into binary.
     * @return An Array of binary values that make up the provided string.
     */
    public static Binary[] toBinary(String value) {
        Binary[] binArray = new Binary[value.length()];
        int binQ;
        StringBuilder newBin;
        for (int i=0; i<value.length(); i++) {
            binQ = value.charAt(i);
            newBin = new StringBuilder();
            while (binQ!=0) {
                binQ /= 2;
                newBin.insert(0, binQ % 2);
            }
            binArray[i] = new Binary(newBin.toString());
        }
        return binArray;
    }

    /**
     * Converts Binary to Number.
     * @param bin The binary representation of a number.
     * @return The number that the binary represented.
     */
    public static Number outBinary(Binary bin) {
        Number num = new Number();
        int countdown = bin.length()+1;
        for (int i=0; i<bin.length(); i++) {
            countdown -= 1;
            if (bin.charAt(i) != '0' || bin.charAt(i) != '1') {
                throw new BadBinaryValueException("Value given to Convert.outBinary wasn't binary: "+bin);
            }
            num.add(
                    new Number(bin.charAt(i)).multiply(
                            new Number(2).power(Integer.toString(countdown))
                    )
            );
        }
        return num;
    } // end of outBinary

    /**
     * Converts Binary to String.
     * @param bins An Array of binary values that make up a string.
     * @return The string that the Array of binaries made up.
     */
    public static String outBinary(Binary[] bins) {
        StringBuilder string = new StringBuilder();
        double num;
        for (Binary bin : bins) {
            num = 0;
            for (int i=0; i<bin.length(); i++) {
                num += Integer.parseInt(String.valueOf(bin.charAt(i)));
                num *= Math.pow(2, bin.length()-i);
            }
            string.append((char)(int) num);
        }
        return string.toString();
    }

    /**
     * Converts a scientific number into a normal number.
     * <br>
     * The allowed separators are as follows:
     * <br>
     * 'x', 'X', '*', 'e', and 'E'
     * @param num The scientific notated number.
     * @param separator The separator that the scientific notation uses.
     * @return A string representation of the number.
     */
    public static String scientific(String num, char separator) {
        String[] split = num.split((separator=='*'?"\\*":String.valueOf(separator)));
        if (split.length != 2) {
            throw new NumberFormatException("The given number String isn't in scientific notation.");
        } else {
            int zeros;
            if (separator == 'x' || separator == 'X' || separator == '*') {
                zeros = Integer.parseInt(split[1].split("\\^")[1]);
            } else if (separator == 'e' || separator == 'E') {
                zeros = Integer.parseInt(split[1]);
            } else {
                throw new IllegalArgumentException("The provided separator isn't allowed: \""+separator+"\"");
            }
            StringBuilder ret = new StringBuilder();
            split = split[0].split("\\.");
            ret.append(split[0]);
            char cnum;
            if (zeros>0) {
                while (zeros>0) {
                    if (split[1].isBlank()) {
                        cnum = '0';
                    } else {
                        cnum = split[1].charAt(0);
                        split[1] = split[1].substring(1);
                    }
                    ret.append(cnum);
                    zeros -= 1;
                }
                if (!split[1].isBlank()) {
                    ret.append('.').append(split[1]);
                }
            } else {
                ret.append(split[1]);
                int places = -split[1].length();
                while (zeros<0) {
                    ret.insert(0, '0');
                    zeros += 1;
                    places += 1;
                }
                if (places<0) {
                    ret.insert(places+ret.length(), '.');
                }
            }
            return ret.toString();
        }
    }
    /**
     * Converts a scientific number into a normal number.
     * <br>
     * Automatically determinants what the separator used was.
     * <br>
     * Useful when the separator used is unknown.
     * @param num The scientific notated number.
     * @return A string representation of the number.
     */
    public static String scientific(String num) {
        char[] seps = new char[]{
                'x', 'X', '*', 'e', 'E'
        };
        String[] split;
        for (char sep : seps) {
            split = num.split((sep=='*'?"\\*":String.valueOf(sep)));
            if (split.length == 2) {
                return scientific(num, sep);
            }
        }
        return num;
    }
}
