import arlot.data.collect.Pair;
import arlot.data.file.doc.Documentation;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

public class DataTest {
    public static class SimpleMessageException extends Exception {
        private final String message;

        public SimpleMessageException(String message) {
            this.message = message;
            //System.err.println(message);

        }

        @Override
        public String toString() {
            return message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public void printStackTrace() {
            // Override to only print the message without the stack trace
            System.err.println(message);
            System.exit(1);
        }
    }
    public static void main(String[] args) throws Exception {
        //try {
            throw new SimpleMessageException("test");
        //} catch (Exception e) {
        //    System.out.println(e);
        //}
        //TransformerException test = new TransformerException(new Exception("This is just a test"));
        //System.out.println(test.getLocationAsString());
        //Documentation Overview = new Documentation("Overview", null, null, null);
        //Overview.about("The arlot package is a constructed holder over many smaller packages that are in the scope of giving useful and easy to use tools in programming.");
        //Overview.toFile(null, true, true);
        /*
        Documentation test = new Documentation("[class name]",
                "class | abstract | interface | annotation",
                "Test",
                new String[] {"Test", "Test", "Test"});
        test.constructor("Constructor Definition Name")
                .about("This is the description of a constructor.")
                .parameter("constructorParameter",
                        "constructorParameterDataType",
                        "This is the description of a constructor parameter.");
        test.method("method",
                "public | protected | private",
                "non-static | static | abstract | final",
                "void")
                .about("This is the description of a method.")
                .parameter("methodParameter",
                        "methodParameterDataType",
                        "This is the description of a method parameter.");
        */
        //Documentation test = new Documentation("Pair2", "class", null, new String[]{"Cloneable", "Serializable",  "Comparable/;/Pair/;/?, ?/://:/", "Map.Entry/;/K, V/:/"});
        //test.toFile("data/collect", true, true);
        //Documentation test = new Documentation("Overview", null, null, new String[0]);
        //test.about("The arlot package is a constructed holder over many smaller packages that are in the scope of giving useful and easy to use tools in programming.");
        //System.out.println("< ---------------------- Built Template ---------------------- >");
        //System.out.println(test);
        //test.toFile(null, true);
        //Documentation test2 = Documentation.parse(new File("Documentation/resources/templete.html"));
        //System.out.println("< ---------------------- Actual Template ---------------------- >");
        //System.out.println(test2);
        /*
        JSON test = new JSON("test");
        //test.put("subTest", new Data<JSON>(new JSON("subTest")));
        test.put("test.subTest.subSubTest", new Data<String>("This is a nested string."));
        test.put("test2.subTest", new Data<Integer>(553676));
        test.put("test3", new Data<Double>(564.3344));
        System.out.println(test);
        System.out.println(JSON.stringToJSON(test.toString(), "test", true).get("test.subTest.subSubTest"));
        */
    }
}
