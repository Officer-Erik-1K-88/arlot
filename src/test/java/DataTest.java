import arlot.data.Data;
import arlot.data.file.JSON;

public class DataTest {
    public static void main(String[] args) {
        JSON test = new JSON("test");
        //test.put("subTest", new Data<JSON>(new JSON("subTest")));
        test.put("test.subTest.subSubTest", new Data<String>("This is a nested string."));
        test.put("test2.subTest", new Data<Integer>(553676));
        test.put("test3", new Data<Double>(564.3344));
        System.out.println(test);
        System.out.println(JSON.stringToJSON(test.toString(), "test", true).get("test.subTest.subSubTest"));
    }
}
