package arlot.user.display;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class Alert extends Dialog<ButtonType> {
    /**
     * How much of the width of this Alert's
     * {@link javafx.scene.control.DialogPane DialogPane}
     * will be divided by before being attached
     * as the body's {@code WrappingWidth} for the height
     * calculations of the {@code ContentText}.
     */
    public static final double BODYDIV = 1.27686112;
    public static final double PADDING = 10;
    public static final Font FONT = new Font(12);

    private final ObjectProperty<AlertType> alertType = new SimpleObjectProperty<>();
    private TextField input = null;

    public Alert() {
        super(new javafx.scene.control.Alert(AlertType.NONE.convert()));
        setAlertType(AlertType.NONE);
        init("NULL Action Declared!",
                "The action of `NULL` was issued!",
                "An undefined action was executed.");
    }

    public Alert(AlertType alertType) {
        super(new javafx.scene.control.Alert(alertType.convert()));
        setAlertType(alertType);
        init(alertType.name()+" Action Declared!",
                "An action of "+alertType.name()+" was declared.",
                "An empty construct of the `AlertType` \""+alertType.name()+"\" was called.");
    }

    public Alert(AlertType alertType, String contentText, ButtonType... buttonTypes) {
        super(new javafx.scene.control.Alert(alertType.convert(), contentText, buttonTypes));
        setAlertType(alertType);
        init(getTitle(), getHeaderText(), getContentText());
    }

    public Alert(AlertType alertType, String title, String header, String body) {
        super(new javafx.scene.control.Alert(alertType.convert()));
        setAlertType(alertType);
        init(title, header, body);
    }

    public Alert(AlertType alertType, String title, String header) {
        super(new javafx.scene.control.Alert(alertType.convert()));
        setAlertType(alertType);
        init(title, header, null);
    }

    /**
     * Initializes this {@code Alert} container.
     * @param title The title of this {@code Alert}.
     * @param header The header of this {@code Alert}.
     * @param body The body content of this {@code Alert}.
     */
    protected void init(String title, String header, String body) {
        if (getAlertType() != AlertType.NONE) {
            ImageView imageView = new ImageView(getAlertType().getImage());
            setGraphic(imageView);
        }
        if (title == null) {
            title = "NULL";
        }
        if (header == null) {
            header = "";
        }
        if (body == null) {
            body = "";
        }
        if (!getTitle().equals(title)) {
            this.setTitle(title);
        }
        if (!getHeaderText().equals(header)) {
            this.setHeaderText(header);
        }
        if (!getContentText().equals(body)) {
            this.setContentText(body);
        }
        //this.setResizable(true);

        if (getAlertType() == AlertType.PROMPT) {
            if (input == null) {
                input = new TextField();
                input.setMinHeight(26);
                input.setPrefHeight(26);
                input.setMaxHeight(26);
                input.setMinWidth(200);
                input.setPrefWidth(200);
                //input.setMaxWidth(200);
                input.setFont(FONT);
            }
        }

        AtomicInteger placementIndex = new AtomicInteger();
        AtomicReference<Double> placementY = new AtomicReference<>((double) 0);
        initNodes(this.getDialogPane(), ((labelPair, pane) -> {
            Label label = labelPair.getValue();
            placementIndex.set(labelPair.getKey());
            double requiredHeight;
            if (pane.equals(this.getDialogPane()) && input != null) {
                requiredHeight = setRequiredHeight(label, pane, input.getPrefHeight()+20);
            } else {
                requiredHeight = setRequiredHeight(label, pane);
            }
            placementY.set(placementY.get()+requiredHeight);
            return requiredHeight;
        }));
        if (input != null) {
            input.autosize();
            input.setLayoutX(PADDING);
            input.setLayoutY(placementY.get()+input.getHeight()+PADDING);

            this.getDialogPane().getChildren().add(placementIndex.get()+1, input);
        }
    }

    private void initNodes(Pane parent, BiFunction<Pair<Integer, Label>, Pane, Double> labelFunction) {
        ObservableList<Node> children = parent.getChildren();
        for(int i=0; i<children.size(); i++) {
            Node node = children.get(i);
            if (node instanceof Pane pane) {
                initNodes(pane, labelFunction);
            } else if (node instanceof Label label) {
                label.setFont(FONT);
                double requiredHeight = labelFunction.apply(new Pair<>(i, label), parent);
                label.setMinHeight(requiredHeight);
                label.setPrefHeight(requiredHeight);
            }
        }
    }

    /**
     * The same as {@link #showAndWait()}, but handles the factor of a
     * {@link AlertType#PROMPT prompted} alert and returns the text of the {@link #input}.
     * <br>
     * Returns null if any exceptions occur after this {@code Alert} is closed.
     *
     * @return The text provided to a {@link AlertType#PROMPT} on confirmation.
     */
    public final String showAndCollect() {
        Optional<ButtonType> buttonType = this.showAndWait();
        try {
            boolean good = buttonType.isPresent() && buttonType.get().equals(ButtonType.OK);
            if (good) {
                return input.getText();
            } else {
                return "";
            }
        } catch (Exception e) {
            return null;
        }
    }

    public final boolean showAndCollect(Runnable action) {
        boolean good;
        Optional<ButtonType> buttonType = this.showAndWait();
        try {
            good = buttonType.isPresent() && buttonType.get().equals(ButtonType.OK);
            if (good) {
                Platform.runLater(action);
            }
        } catch (Exception e) {
            good = false;
        }
        return good;
    }

    public void clear() {
        init("NULL", null, null);
        if (isShowing()) {
            close();
        }
    }

    @Override
    public javafx.scene.control.Alert getMaster() {
        return (javafx.scene.control.Alert) super.getMaster();
    }

    // AlertType
    public final ObjectProperty<AlertType> alertTypeProperty() {
        return this.alertType;
    }
    public final AlertType getAlertType() {
        return this.alertType.get();
    }
    public final void setAlertType(AlertType alertType) {
        this.alertType.setValue(alertType);
        if (getMaster() != null) {
            getMaster().setAlertType(alertType.convert());
        }
    }

    // ButtonType
    public final ObservableList<ButtonType> getButtonTypes() {
        return this.getDialogPane().getButtonTypes();
    }

    // Input
    public final TextField getInput() {
        return input;
    }

    public static boolean validateAction(String actionName, Runnable action) {
        Alert confirm = new Alert(AlertType.VALIDATE,
                "Validate Action: "+actionName,
                "Are you sure you want to `"+actionName+"`?");
        return confirm.showAndCollect(action);
    }

    private static String textAlert(String title, String header, String body) {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(header);
        confirm.setContentText(body);
        TextField input = new TextField();
        input.setMinHeight(26);
        input.setPrefHeight(26);
        input.setMaxHeight(26);
        input.setMinWidth(200);
        input.setPrefWidth(200);
        //input.setMaxWidth(200);

        ObservableList<Node> children = confirm.getDialogPane().getChildren();
        int placementIndex = 0;
        double placementY = 0;
        for(int i=0; i<children.size(); i++) {
            Node node = children.get(i);
            //System.out.println(node.getClass().getName());
            //System.out.println(node.getTypeSelector());
            if (node instanceof Label label) {
                placementIndex = i;
                double requiredHeight = setRequiredHeight(label, confirm.getDialogPane(), input.getPrefHeight()+20);
                label.setMinHeight(requiredHeight);
                label.setPrefHeight(requiredHeight);
                placementY += requiredHeight;
            }
        }

        input.autosize();
        input.setLayoutX(PADDING);
        input.setLayoutY(placementY+input.getHeight()+PADDING);

        confirm.getDialogPane().getChildren().add(placementIndex+1, input);

        Optional<ButtonType> buttonType = confirm.showAndWait();
        boolean good = buttonType.isPresent() && buttonType.get().equals(ButtonType.OK);
        if (good) {
            return input.getText();
        } else {
            return "";
        }
    }

    private static void showAlert(AlertType type, String title, String header, String body) {
        new Alert(type, title, header, body).show();
    }
    private static void showAlert(AlertType type, String title, String header) {
        new Alert(type, title, header).show();
    }

    public static double setRequiredHeight(Label label, Pane parent, double increase) {
        label.setWrapText(true);
        Text text = new Text(label.getText());
        text.setFont(label.getFont());
        double wrappingWidth;
        if (parent.getWidth() == 0) {
            wrappingWidth = 200;
        } else {
            wrappingWidth = parent.getWidth();
        }
        wrappingWidth /= 2;//BODYDIV;//2.816;
        //increase += PADDING*2;
        text.setWrappingWidth(wrappingWidth);
        text.setLineSpacing(label.getLineSpacing());
        double requiredHeight = text.getLayoutBounds().getHeight()+increase;
        label.setMinHeight(requiredHeight);
        label.setPrefHeight(requiredHeight);
        //System.out.println(label.getParent() == parent);
        System.out.println(label.getText());
        System.out.println("Font Size: "+label.getFont().getSize());
        System.out.println("Font Family: "+label.getFont().getFamily());
        System.out.println("Font Name: "+label.getFont().getName());
        System.out.println("Font Style: "+label.getFont().getStyle());
        System.out.println(parent.getWidth());
        System.out.println(parent.getHeight());
        System.out.println(wrappingWidth);
        System.out.println(text.getWrappingWidth());
        System.out.println(requiredHeight);
        //System.out.println(parent.getWidth()/BODYDIV);
        //System.out.println(parent.getWidth());
        return requiredHeight;
    }
    public static double setRequiredHeight(Label label, Pane parent) {
        return setRequiredHeight(label, parent, 0);
    }

    /**
     * @see javafx.scene.control.Alert.AlertType
     */
    public enum AlertType {
        NONE,
        INFORMATION("images/Alert.Information.png"),
        WARNING("images/Alert.Warning.png"),
        CONFIRMATION("images/Alert.Confirmation.png"),
        PROMPT(CONFIRMATION.equivalent, "images/Alert.Prompt.png"),
        VALIDATE(CONFIRMATION.equivalent, "images/Alert.Validate.png"),
        ERROR("images/Alert.Error.png");

        private javafx.scene.control.Alert.AlertType equivalent;
        private final Image image;
        private final double iHeight = 60;
        private final double iWidth = 60;
        private boolean original;

        AlertType() {
            image = null;
            try {
                equivalent = javafx.scene.control.Alert.AlertType.valueOf(this.name());
                original = true;
            } catch (IllegalArgumentException e) {
                equivalent = javafx.scene.control.Alert.AlertType.NONE;
                original = false;
            }
        }

        AlertType(String imgPath) {
            image = new Image(
                    Resources.loadResource(imgPath).url().toExternalForm(),
                    iWidth,
                    iHeight,
                    true,
                    true);
            try {
                equivalent = javafx.scene.control.Alert.AlertType.valueOf(this.name());
                original = true;
            } catch (IllegalArgumentException e) {
                equivalent = javafx.scene.control.Alert.AlertType.NONE;
                original = false;
            }
        }

        AlertType(javafx.scene.control.Alert.AlertType equivalent) {
            image = null;
            this.equivalent = equivalent;
            original = false;
        }

        AlertType(javafx.scene.control.Alert.AlertType equivalent, String imgPath) {
            image = new Image(
                    Resources.loadResource(imgPath).url().toExternalForm(),
                    iWidth,
                    iHeight,
                    true,
                    true);
            this.equivalent = equivalent;
            original = false;
        }

        /**
         * Converts this {@code AlertType} enum into it's equivalent
         * {@code javafx.scene.control.Alert.AlertType} enum.
         * <br>
         * if this {@code AlertType} enum doesn't have a counterpart
         * {@code javafx.scene.control.Alert.AlertType} enum,
         * then {@link javafx.scene.control.Alert.AlertType#NONE} is returned.
         *
         * @return A {@code javafx.scene.control.Alert.AlertType} enum that is equivalent to
         * this {@code AlertType} enum.
         */
        public javafx.scene.control.Alert.AlertType convert() {
            return equivalent;
        }

        public Image getImage() {
            return image;
        }

        /**
         * Gets whether this {@code AlertType} is also in
         * {@code javafx.scene.control.Alert.AlertType}.
         * @return true if this {@code AlertType} is in
         * {@code javafx.scene.control.Alert.AlertType}, otherwise false.
         */
        public boolean isOriginal() {
            return original;
        }
    }
}
