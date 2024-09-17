package arlot.math;

import arlot.error.NotImplementedException;
import arlot.error.OutOfRangeException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Advanced {
    // constants
    /**
     * Gets {@code Euler's Number} as a {@link Number}.
     * @return A {@link Number} representation of {@code #E Euler's Number}.
     */
    public static Number e() {
        return Constants.E.asNumber();
    }

    /**
     * Gets {@code #PI} as a {@link Number}.
     * @return A {@link Number} representation of {@code #PI}.
     */
    public static Number pi() {
        return Constants.PI.asNumber();
    }

    /**
     * Gets {@code #TAU} as a {@link Number}.
     * @return A {@link Number} representation of {@code #TAU}.
     */
    public static Number tau() {
        return Constants.TAU.asNumber();//pi().multiply(2);
    }

    /**
     * Gets {@code #PHI} as a {@link Number}.
     * @return A {@link Number} representation of {@code #PHI}.
     */
    public static Number phi() {
        return Constants.PHI.asNumber();
    }

    /**
     * Gets {@code #SOL Speed of Light} as a {@link Number}.
     * @return A {@link Number} representation of {@code #SOL Speed of Light}.
     */
    public static Number speedOfLight() {
        return Constants.SOL.asNumber();
    }

    /**
     * Converts an angle measured in degrees to an approximately
     * equivalent angle measured in radians.
     * The conversion from degrees to radians is generally inexact.
     *
     * @param angdeg an angle, in degrees
     * @return  the measurement of the angle {@code angdeg} in radians.
     */
    public static Number toRadians(Number angdeg) {
        return angdeg.copy().multiply(pi().divide(180));
    }

    /**
     * Converts an angle measured in radians to an approximately
     * equivalent angle measured in degrees.
     * The conversion from radians to degrees is generally inexact.
     *
     * @param angrad an angle, in radians
     * @return the measurement of the angle {@code angrad} in degrees.
     */
    public static Number toDegrees(Number angrad) {
        return angrad.copy().multiply(new Number(180).divide(pi()));
    }

    /**
     * Returns Euler's number <i>e</i> raised to the power of a number.
     *
     * @param to the exponent to raise <i>e</i> to.
     * @return the value <i>e</i><sup>{@code to}</sup>,
     * where <i>e</i> is the base of the natural logarithms.
     */
    public static Number exp(Number to) {
        return e().power(to);
    }

    public static Number[][] primeFactor(Number num) {
        Number cnum = num.copy();
        Map<String, Number> factors = new HashMap<>();
        Number factor = new Number(2);
        while (cnum.symbols("ne", "1")) {
            if (cnum.mod(factor).symbols("eq", "0")) {
                if (factors.containsKey(factor.toString())) {
                    factors.get(factor.toString()).add(1);
                } else {
                    factors.put(factor.toString(), new Number(1));
                }
            } else {
                factor.add(1);
            }
        }
        List<Number[]> ret = new ArrayList<>();
        for (String key : factors.keySet()) {
            ret.add(new Number[]{new Number(key), factors.get(key)});
        }
        return ret.toArray(new Number[0][2]);
    }

    public static Number euclidAlgorithm(Number val1, Number val2) {
        Number a, b, remainder;
        if (val1.symbols("ge", val2)) {
            a = val1.copy();
            b = val2.copy();
        } else {
            a = val2.copy();
            b = val1.copy();
        }
        do {
            remainder = a.mod(b);
            a = b;
            b = remainder;
        } while (remainder.symbols("ne", "0"));
        return a;
    }

    /**
     *
     * @param numbers The {@link Number}s to use.
     * @return The Greatest Common Factor.
     */
    public static Number gcf(Number... numbers) {
        if (numbers.length < 2) {
            throw new OutOfRangeException("There must be at least two provided numbers.");
        } else if (numbers.length == 2) {
            return euclidAlgorithm(numbers[0], numbers[1]);
        }
        List<Number[]> pairs = new ArrayList<>();
        int count = 0;
        int index = 0;
        for (Number num : numbers) {
            if (count==0) {
                pairs.add(new Number[2]);
                index = pairs.size()-1;
            }
            pairs.get(index)[count] = num;
            count = (count==1?0:1);
        }
        if (count==1) {
            pairs.set(pairs.size()-2, new Number[] {euclidAlgorithm(
                    pairs.get(pairs.size()-2)[0],
                    pairs.get(pairs.size()-2)[1]),
                    pairs.getLast()[0]});
            pairs.removeLast();
        }
        List<Number> newNumbers = new ArrayList<>();
        for (Number[] nums : pairs) {
            newNumbers.add(euclidAlgorithm(nums[0], nums[1]));
        }
        return gcf(newNumbers.toArray(new Number[0]));
    }

    /**
     *
     * @param numbers The {@link Number}s to use.
     * @return The Least Common Multiple.
     */
    public static Number lcm(Number... numbers) {
        Number gcf = gcf(numbers);
        Number ret = new Number(1);
        for (Number num : numbers) {
            ret.multiply(num);
        }
        ret.divide(gcf);
        return ret;
    }

    /**
     * This method gets the logarithm base of a value.
     * <br><br>
     * This is the visual representation of this method:
     * <br>
     * <code>log<sub>base</sub>value = {@code return}</code>
     * <br><br>
     * If the given {@code tolerance} is less than 20
     * then 20 will be added to the {@code tolerance}.
     * @param base Some base of this logarithm.
     * @param value The value to pass to the base.
     * @param tolerance How precise to calculate to, the bigger this number is the more precise the returned number will be.
     * @return The base logarithm of the provided value.
     */
    public static Number log(Number base, Number value, int tolerance) {
        if (tolerance < 20) {
            tolerance += 20;
        }
        base = base.copy();
        value = value.copy();
        Number lower = new Number(value);
        Number upper = new Number(value);
        if (value.isNegative()) {
            upper.multiply(-1);
        } else {
            lower.multiply(-1);
        }
        Number m = lower.copy().add(upper).divide(2);
        Number poweredBase = Basic.pow(base, m);
        int count = 0;
        while (poweredBase.symbols("ne", value)) {
            if (count <= tolerance) {
                if (poweredBase.symbols("lt", value)) {
                    lower.update(m);
                } else {
                    upper.update(m);
                }
                m.update(lower.copy().add(upper).divide(2));
                poweredBase.update(Basic.pow(base, m));
                count++;
            } else {
                break;
            }
        }
        return m;
    }

    public static Number factorial(Number num) {
        return Factorial.calculateFactorial(num);
    }
}
