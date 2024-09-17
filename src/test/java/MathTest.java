import arlot.math.Factorial;
import arlot.math.Number;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class MathTest {
    public static void main(String[] args) {
        /*double i1 = 1202.4;
        double i2 = 4.2;
        Number n1 = new Number(i1);
        Number n2 = new Number(i2);
        n1.divide(n2);
        n1.round(8);
        System.out.println();
        System.out.println(n1);
        System.out.println(i1/i2);*/
        //System.out.println(Basic.reorganize("0.1001E2"));
        //Number min = new Number("-20");
        //Number max = new Number("-1");//"200000000000");
        //System.out.println(Arrays.toString(max.symbols(min)));
        //for (int i=1; i<=100; i++) {
        //    System.out.println("Random run "+i+": "+Basic.random(min, max));
        //}
        //Number n1 = new Number("23456789");
        //Number n2 = new Number("18.5");
        //System.out.println(n1.copy().divide(n2));
        Instant start, end;
        Number f;
        //System.out.println(Arrays.toString(Advanced.getFactFile().toArray(new String[0])));
        /*int i = 300;
        Number nug1 = new Number(i);
        start = Instant.now();
        f = Advanced.factorial(nug1);
        end = Instant.now();
        System.out.println(f);
        System.out.println("Elapsed Time: "+ Duration.between(start, end).toString());
        */
        /*System.out.println();
        int cap = 20000;
        Instant fStart = Instant.now();
        for (int j=0; j<=cap; j++) {
            System.out.println("<-- New factorial ("+j+") -->");
            start = Instant.now();
            f = Advanced.factorial(new Number(j));
            System.out.println(f);
            end = Instant.now();
            System.out.println("Elapsed Time: "+ Duration.between(start, end).toString());
            System.out.println("<-- End factorial ("+j+") -->");
            System.out.println();
        }
        Instant fEnd = Instant.now();
        System.out.println("Total Elapsed Time for run check: "+ Duration.between(fStart, fEnd).toString());
        */
        //System.out.println(Arrays.toString(Advanced.getFactFile().toArray(new String[0])));
        start = Instant.now();
        //System.out.println(Factorial.calculateFactorial(new Number(21)));
        //Factorial factorials = new Factorial(new Number(21));
        //factorials.run();
        //System.out.println(factorials.getFactorial());
        Factorial.fillFactorials(new Number(20000));
        end = Instant.now();
        System.out.println("Total Elapsed Time: "+ Duration.between(start, end).toString());
        System.out.println();
        System.out.println(Arrays.toString(Factorial.getFactorials()));

        //System.out.println(nug1.sqrt());
        //System.out.println(Math.sqrt(i));
        //System.out.println();
        //System.out.println(Advanced.log(new Number(10), new Number(20), 20));
    }
}
