package arlot.user.display;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX Application
 */
public abstract class Application extends javafx.application.Application {

    /**
     * The {@link Scene} that is linked to this {@code Application}.
     */
    private Scene scene;

    /**
     * The {@link Stage} that is set to this {@code Application}.
     */
    private Stage stage;

    /**
     * The title to be given to this Application's window.
     */
    private String title = "Arlot Display Extension Window from JavaFX";

    /**
     * Builds the {@link #scene} and {@link #stage} for this {@code Application}
     * at the time the {@link #start(Stage)} operation is executed.
     * @param stage The {@link Stage} to set to this {@code Application}.
     */
    protected void build(Stage stage) {
        if (scene == null) {
            setScene();
        }
        setStage(stage);
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <br><br>
     * It is recommended not to override this method,
     * instead look at overriding {@link #build(Stage)} for the editing of the {@link #scene}
     * and {@link #stage} of this {@code Application}.
     * <br><br>
     * Any other operations needed to be done after the call to show the {@link #stage},
     * and need to be within the JavaFX Application Thread,
     * then the {@link #execute()} method must be used.
     *
     * @apiNote This method is called on the JavaFX Application Thread.
     * @param stage the primary stage for this application,
     *              onto which the application scene can be set.
     *              The primary stage will be embedded in the browser if the
     *              application was launched as an applet.
     *              Applications may create other stages,
     *              if needed,
     *              but they will not be primary stages and will not be embedded
     *              in the browser.
     */
    @Override
    public void start(Stage stage) {
        build(stage);
        this.stage.show();
        execute();
    }

    /**
     * Is called after the execution of the {@link #start(Stage)} operation.
     * <br>
     * This method is used to assert that objects are being called in the
     * {@code JavaFX Application Thread}.
     */
    public abstract void execute();

    public final Scene getScene() {
        return scene;
    }

    protected final void setScene() {
        setScene(Resources.INTERNAL.FXML.DEFAULT);
    }

    protected final void setScene(Parent parent) {
        scene = new Scene(parent, 600, 480);
    }

    protected final void setScene(Parent parent, double width, double height) {
        scene = new Scene(parent, width, height);
    }

    protected final void setScene(String fxml) {
        try {
            setScene(loadFXML(fxml));
        } catch (IOException e) {
            scene = null;
        }
    }
    protected final void setScene(String fxml, double width, double height) {
        try {
            setScene(loadFXML(fxml), width, height);
        } catch (IOException e) {
            scene = null;
        }
    }

    protected final void setScene(Resources.INTERNAL.FXML fxml) {
        try {
            setScene(loadFXML(fxml));
        } catch (IOException e) {
            scene = null;
        }
    }
    protected final void setScene(Resources.INTERNAL.FXML fxml, double width, double height) {
        try {
            setScene(loadFXML(fxml), width, height);
        } catch (IOException e) {
            scene = null;
        }
    }

    public final Stage getStage() {
        return stage;
    }

    protected final void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setScene(scene);
        this.stage.setTitle(title);
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
        stage.setTitle(this.title);
    }

    protected static void setRoot(Scene scene, String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        if (!fxml.endsWith(".fxml")) {
            fxml += ".fxml";
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Resources.loadResource(fxml).url());
        Parent content = loader.load();
        return content;
    }

    private static Parent loadFXML(Resources.INTERNAL.FXML fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxml.fxml().url());
        Parent content = loader.load();
        return content;
    }

    //public static void main(String[] args) {launch();}

}