import arlot.user.display.Alert;
import arlot.user.display.Application;

import java.io.IOException;

public class UserDisplayTest extends Application {
    @Override
    public void execute() {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "Testing",
                "This is a test",
                "What a good test this is.");
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
