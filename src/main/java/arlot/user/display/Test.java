package arlot.user.display;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Test extends Application {
    @Override
    protected void build(Stage stage) {
        setScene();
        setStage(stage);
    }

    @Override
    public void execute() {
        Dialog<ButtonType> dialog = new Dialog<>();
        //dialog.getDialogPane()
        /*Alert alert = new Alert(Alert.AlertType.ERROR,
                "Testing",
                "This is a test\nYes a good test in deed. Will it expand or will it just stay at the same placement?",
                "What a good test this is.\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- Is good.");
        alert.show();*/
    }

    public static void main(String[] args) {
        //System.out.println(Test.class.getClassLoader().resources("").toList());
        launch();
    }
}
