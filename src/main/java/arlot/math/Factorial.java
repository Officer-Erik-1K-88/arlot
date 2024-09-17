package arlot.math;

import arlot.error.NotImplementedException;
import arlot.error.OutOfRangeException;
import arlot.error.UpdateDeniedException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Factorial implements Runnable {
    public static Number calculateFactorial(Number from) {
        if (FACTORIALS.containsKey(from.toString())) {
            return new Number(FACTORIALS.get(from.toString()));
        }

        Factorial factorial = new Factorial(from);
        factorial.run();
        try {
            addFactorial(factorial.getFrom(), factorial.getFactorial());
        } catch (UpdateDeniedException ignore) {}

        return factorial.getFactorial();
    }

    public static void fillFactorials(Number max) {
        Number factorial = new Number(1);
        Number ret = new Number();
        Instant start, end;
        for (Number from = new Number(1); from.symbols("le", max); from.add(1)) {
            System.out.println("<-- New factorial ("+from+") -->");
            start = Instant.now();
            if (FACTORIALS.containsKey(from.toString())) {
                factorial.update(FACTORIALS.get(from.toString()));
            } else {
                factorial.multiply(from);
                try {
                    addFactorial(from, factorial);
                } catch (UpdateDeniedException ignore) {}
            }
            System.out.println(factorial);
            end = Instant.now();
            System.out.println("Elapsed Time: "+ Duration.between(start, end).toString());
            System.out.println("<-- End factorial ("+from+") -->");
            System.out.println();
        }
    }

    public static Number fillFactorials(Number max, Number get) {
        if (get.symbols("gt", max)) {
            throw new OutOfRangeException("The provided factorial calculation is out of bounds of max.");
        }
        fillFactorials(max);
        return new Number(FACTORIALS.get(get.toString()));
    }

    private static final Map<String, String> FACTORIALS = new HashMap<>();
    static {
        FACTORIALS.put("0","1");
        FACTORIALS.put("1","1");
        FACTORIALS.put("2","2");
        FACTORIALS.put("3","6");
        FACTORIALS.put("4","24");
        FACTORIALS.put("5","120");
        FACTORIALS.put("6","720");
        FACTORIALS.put("7","5040");
        FACTORIALS.put("8","40320");
        FACTORIALS.put("9","362880");
        FACTORIALS.put("10","3628800");
        FACTORIALS.put("11","39916800");
        FACTORIALS.put("12","479001600");
        FACTORIALS.put("13","6227020800");
        FACTORIALS.put("14","87178291200");
        FACTORIALS.put("15","1307674368000");
        FACTORIALS.put("16","20922789888000");
        FACTORIALS.put("17","355687428096000");
        FACTORIALS.put("18","6402373705728000");
        FACTORIALS.put("19","121645100408832000");
        FACTORIALS.put("20","2432902008176640000");
    }
    public static void addFactorial(Number from, Number factorial) {
        if (FACTORIALS.containsKey(from.toString())) {
            throw new UpdateDeniedException("Cannot edit already existing factorials.");
        }
        FACTORIALS.put(from.toString(), factorial.toString());
    }
    public static String[] getFactorials() {
        String[] factorials = new String[FACTORIALS.size()];
        AtomicInteger i = new AtomicInteger(0);
        FACTORIALS.forEach((from, factorial) -> {
            factorials[i.get()] = from+":"+factorial;
            i.set(i.get()+1);
        });
        return factorials;
    }

    private final Number factorial;
    private final Number from;
    private final Number start = new Number(2);
    private boolean ran;

    public Factorial(Number from) {
        this.from = from.copy();
        if (this.from.isDecimal()) {
            throw new NotImplementedException("This calculation isn't implemented, make sure the provided Number isn't a decimal.");
        }
        factorial = new Number(1);
        ran = false;
    }
    public Factorial(Number start, Number from) {
        this.start.update(start);
        this.from = from.copy();
        if (this.from.isDecimal()) {
            throw new NotImplementedException("This calculation isn't implemented, make sure the provided Number isn't a decimal.");
        }
        factorial = new Number(1);
        ran = false;
    }


    @Override
    public void run() {
        if (!ran) {
            ran = true;
            if (from.symbols("eq", "0") || from.symbols("eq", "1")) {
                factorial.update(new Number(1));
            } else if (from.symbols("eq", "2")) {
                factorial.update(from);
            } else {
                for (Number i = start.copy(); i.symbols("le", from); i.add(1)) {
                    factorial.multiply(i);
                }
                /*Number n = from.copy();
                Number i = start.copy();
                for (;i.symbols("ne",n) && i.symbols("ne", n.copy().subtract(1)); i.add(1)) {
                    factorial.multiply(i).multiply(n);
                    n.subtract(1);
                }
                if (!i.symbols("ne",n)) {
                    factorial.multiply(i);
                }*/
            }
        }
    }

    public Number getFactorial() {
        return factorial.copy();
    }
    public Number getFrom() {
        return from.copy();
    }
    public Number getStart() {
        return start.copy();
    }

    public boolean hasRan() {
        return ran;
    }

    // not usable
    private final static Path factorialFilePath = new File(Advanced.class.getClassLoader().getResource("math/factorials.txt").getFile()).toPath();//"src/main/java/arlot/math/factorials.txt";

    private static List<String> getFactFile() {
        try (Stream<String> lines = Files.lines(factorialFilePath)) {
            return lines.collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Number getFactorialFromFile(String num) {
        List<String> lines = getFactFile();
        if (lines.size() > Integer.parseInt(num)) {
            String[] ls;
            for (String line : lines) {
                ls = line.split(":");
                if (num.equals(ls[0])) {
                    return new Number(ls[1]);
                }
            }
        }
        return null;
    }

    private static Number[] getClosestFactorial() {
        List<String> lines = getFactFile();
        String[] cf = lines.get(lines.size()-2).split(":");
        return new Number[] {
                new Number(cf[0]),
                new Number(cf[1])
        };
    }

    private static void addFactFile(Number num, Number factorial) {
        List<String> data = getFactFile();
        String line = num.toString()+":"+factorial.toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(factorialFilePath.toFile(), true))) {
            if (!data.contains(line)) {
                writer.newLine(); // To ensure each new line starts on a new line
                writer.write(line);
                data.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
