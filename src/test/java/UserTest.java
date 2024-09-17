import arlot.user.Colors;
import arlot.user.display.Resources;

import java.io.File;

public class UserTest {
    public static void main(String[] args) {
        //System.out.println(Resources.INTERNAL.FXML.DEFAULT.fxml().getPath());
        //System.out.println(Resources.INTERNAL.FXML.DEFAULT.controller().getPath());
        //File f = new File("src/main/resources/arlot/user/display/default.fxml");
        //System.out.println(f.exists());
        System.out.println(Colors.WHITE.color().compareTo(Colors.BLACK.color()));
    }
}
