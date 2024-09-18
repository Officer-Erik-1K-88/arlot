import arlot.protect.Encryption;

public class ProtectionTest {
    public static void main(String[] args) throws Exception {
        Encryption e1 = new Encryption("Testing1".toCharArray());
        String encoded = e1.encode(new String[]{"This is some test data. OOOOOOOhhh, yeah!!!!!"})[0];
        System.out.println(encoded);
        System.out.println(e1.decode("Testing1", new String[]{encoded})[0]);
    }
}
