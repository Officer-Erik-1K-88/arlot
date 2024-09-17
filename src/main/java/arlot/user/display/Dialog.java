package arlot.user.display;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;

import java.util.Optional;

/**
 * A Dialog wraps a JavaFX Dialog object (witch wraps a JavaFX DialogPane)
 * and provides the necessary API to present it to end users.
 * This essentially means that the DialogPane is shown to users inside a Stage.
 * <br><br>
 * The Dialog class has a single generic type, R,
 * which is used to represent the type of the result property
 * (and also, how to convert from ButtonType to R, through the use of the
 * {@link #resultConverterProperty() result converter} {@link Callback}).
 * <br><br>
 * Refer to the <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html">JavaFX Dialog</a>
 * class documentation for more detail.
 *
 * @param <R> The return type of the dialog, via the result property.
 */
public class Dialog<R> implements EventTarget {
    /**
     * The actual JavaFX Dialog object that makes up this {@code Dialog} object.
     * @see #getMaster()
     */
    private final javafx.scene.control.Dialog<R> master;

    /**
     * The base construct where {@link #master} is set to a new instance of a
     * JavaFX Dialog object.
     */
    public Dialog() {
        master = new javafx.scene.control.Dialog<>();
    }

    /**
     * This takes the JavaFX Dialog object of the supplied object and turns it
     * into this dialog's {@link #master} construct.
     * @param master The JavaFX Dialog object to use.
     * @param <M> a class that extends this dialog's {@link #master} construct class.
     */
    public <M extends javafx.scene.control.Dialog<R>> Dialog(M master) {
        this.master = master;
    }

    /**
     * Gets this {@code Dialog's} {@link #master} construct.
     * @return a JavaFX Dialog object
     */
    public javafx.scene.control.Dialog<R> getMaster() {
        return master;
    }

    /**
     * Shows the dialog and waits for the user response
     * (in other words, brings up a blocking dialog, with the returned value the users input).
     * @return An {@link Optional} that contains the result.
     * Refer to the <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html">Dialog</a>
     * class documentation for more detail.
     */
    public final Optional<R> showAndWait() {
        return master.showAndWait();
    }

    /**
     * Shows the dialog but does not wait for a user response
     * (in other words, this brings up a non-blocking dialog).
     */
    public final void show() {
        master.show();
    }

    /**
     * Closes the dialog.
     * @see #hide()
     */
    public final void close() {
        master.close();
    }

    /**
     * Hides the dialog.
     * <br>
     * This method is exactly the same to {@link #close()}.
     */
    public final void hide() {
        master.hide();
    }

    // Showing

    /**
     * Represents whether the dialog is currently showing.
     * @return
     * @see #isShowing()
     */
    public final ReadOnlyBooleanProperty showingProperty() {
        return master.showingProperty();
    }
    /**
     * Returns whether the dialog is showing.
     * @return true if dialog is showing.
     */
    public final boolean isShowing() {
        return showingProperty().get();
    }

    // Resizable
    /**
     * Represents whether the dialog is resizable.
     * @return
     * @see #isResizable()
     * @see #setResizable(boolean)
     */
    public final BooleanProperty resizableProperty() {
        return master.resizableProperty();
    }
    /**
     * Returns whether the dialog is resizable.
     * @return true if dialog is resizable.
     */
    public final boolean isResizable() {
        return this.resizableProperty().get();
    }
    /**
     * Sets whether the dialog can be resized by the user.
     * Resizable dialogs can also be maximized ( maximize button becomes visible).
     * @param resizable true if dialog should be resizable.
     */
    public final void setResizable(boolean resizable) {
        this.resizableProperty().set(resizable);
    }

    // Title
    /**
     * Return the titleProperty of the dialog.
     * @return
     * @see #getTitle()
     * @see #setTitle(String)
     */
    public final StringProperty titleProperty() {
        return master.titleProperty();
    }
    public final String getTitle() {
        return this.titleProperty().get();
    }
    public final void setTitle(String text) {
        this.titleProperty().set(text);
    }

    // Header
    /**
     * A property representing the header text for the dialog pane.
     * The header text is lower precedence than the header node,
     * meaning that if both the header node and the headerText properties are set,
     * the header text will not be displayed in a default DialogPane instance.
     *
     * @return
     * @see #getHeaderText()
     * @see #setHeaderText(String) 
     */
    public final StringProperty headerTextProperty() {
        return this.getDialogPane().headerTextProperty();
    }
    public final String getHeaderText() {
        return this.headerTextProperty().get();
    }
    public final void setHeaderText(String text) {
        this.headerTextProperty().set(text);
    }

    // Body
    /**
     * A property representing the content text for the dialog pane.
     * The content text is lower precedence than the content node,
     * meaning that if both the content node and the contentText properties are set,
     * the content text will not be displayed in a default DialogPane instance.
     *
     * @return
     * @see #getContentText() 
     * @see #setContentText(String) 
     */
    public final StringProperty contentTextProperty() {
        return this.getDialogPane().contentTextProperty();
    }
    public final String getContentText() {
        return this.contentTextProperty().get();
    }
    public final void setContentText(String text) {
        this.contentTextProperty().set(text);
    }

    // DialogPane

    /**
     * The root node of the dialog,
     * the DialogPane contains all visual elements shown in the dialog.
     * As such, it is possible to completely adjust the display of the
     * dialog by modifying the existing dialog pane or creating a new one.
     *
     * @return
     * @see #getDialogPane()
     * @see #setDialogPane(DialogPane)
     */
    public final ObjectProperty<DialogPane> dialogPaneProperty() {
        return master.dialogPaneProperty();
    }
    public final DialogPane getDialogPane() {
        return this.dialogPaneProperty().get();
    }
    public final void setDialogPane(DialogPane dialogPane) {
        this.dialogPaneProperty().set(dialogPane);
    }

    // Graphic

    /**
     * The dialog graphic, presented either in the header,
     * if one is showing, or to the left of the content.
     * @return
     * @see #getGraphic()
     * @see #setGraphic(Node)
     */
    public final ObjectProperty<Node> graphicProperty() {
        return this.getDialogPane().graphicProperty();
    }
    public final Node getGraphic() {
        return this.graphicProperty().get();
    }
    public final void setGraphic(Node node) {
        this.graphicProperty().set(node);
    }

    // Result
    /**
     * A property representing what has been returned from the dialog.
     * A result is generated through the result converter,
     * which is intended to convert from the ButtonType that the user
     * clicked on into a value of type R.
     * Refer to the Dialog class JavaDoc for more details.
     *
     * @return
     * @see #getResult()
     * @see #setResult(R)
     */
    public final ObjectProperty<R> resultProperty() {
        return master.resultProperty();
    }
    public final R getResult() {
        return this.resultProperty().get();
    }
    public final void setResult(R result) {
        this.resultProperty().set(result);
    }

    // Result Converter
    /**
     * API to convert the ButtonType that the user clicked on
     * into a result that can be returned via the result property.
     * This is necessary as ButtonType represents the visual button within the dialog,
     * and do not know how to map themselves to a valid result
     * - that is a requirement of the dialog implementation by
     * making use of the result converter.
     * In some cases, the result type of a Dialog subclass is ButtonType
     * (which means that the result converter can be null),
     * but in some cases (where the result type, R, is not ButtonType or Void),
     * this callback must be specified.
     *
     * @return
     * @see #getResultConverter()
     * @see #setResultConverter(Callback)
     */
    public final ObjectProperty<Callback<ButtonType, R>> resultConverterProperty() {
        return master.resultConverterProperty();
    }
    public final Callback<ButtonType, R> getResultConverter() {
        return this.resultConverterProperty().get();
    }
    public final void setResultConverter(Callback<ButtonType, R> resultConverter) {
        this.resultConverterProperty().set(resultConverter);
    }

    // Modality
    public final void initModality(Modality modality) {
        master.initModality(modality);
    }
    public final Modality getModality() {
        return master.getModality();
    }

    // Style
    public final void initStyle(StageStyle stageStyle) {
        master.initStyle(stageStyle);
    }

    // Owner
    public final void initOwner(Window window) {
        master.initOwner(window);
    }
    public final Window getOwner() {
        return master.getOwner();
    }

    // Width
    /**
     * Property representing the width of the dialog.
     * @return
     * @see #getWidth()
     * @see #setWidth(double)
     */
    public final ReadOnlyDoubleProperty widthProperty() {
        return master.widthProperty();
    }
    public final double getWidth() {
        return this.widthProperty().get();
    }
    public final void setWidth(double width) {
        master.setWidth(width);
    }

    // Height
    /**
     * Property representing the height of the dialog.
     * @return
     * @see #getHeight()
     * @see #setHeight(double)
     */
    public final ReadOnlyDoubleProperty heightProperty() {
        return master.heightProperty();
    }
    public final double getHeight() {
        return this.heightProperty().get();
    }
    public final void setHeight(double height) {
        master.setHeight(height);
    }

    // X-Axis
    /**
     * The horizontal location of this Dialog.
     * Changing this attribute will move the Dialog horizontally.
     * @return
     * @see #getY()
     * @see #setY(double)
     */
    public final ReadOnlyDoubleProperty xProperty() {
        return master.xProperty();
    }
     public final double getX() {
        return master.getX();
    }
    public final void setX(double x) {
        master.setX(x);
    }

    // Y-Axis
    /**
     * The vertical location of this Dialog.
     * Changing this attribute will move the Dialog vertically.
     * @return
     * @see #getY()
     * @see #setY(double)
     */
    public final ReadOnlyDoubleProperty yProperty() {
        return master.yProperty();
    }
    public final double getY() {
        return master.getY();
    }
    public final void setY(double y) {
        master.setY(y);
    }

    // Event
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain dispatchChain) {
        return master.buildEventDispatchChain(dispatchChain);
    }
    public final <E extends Event> void addEventHandler(EventType<E> eventType, EventHandler<? super E> eventHandler) {
        master.addEventHandler(eventType, eventHandler);
    }
    public final <E extends Event> void removeEventHandler(EventType<E> eventType, EventHandler<? super E> eventHandler) {
        master.removeEventHandler(eventType, eventHandler);
    }
    public final <E extends Event> void addEventFilter(EventType<E> eventType, EventHandler<? super E> eventHandler) {
        master.addEventFilter(eventType, eventHandler);
    }
    public final <E extends Event> void removeEventFilter(EventType<E> eventType, EventHandler<? super E> eventHandler) {
        master.removeEventFilter(eventType, eventHandler);
    }

    // On Showing
    /**
     * Called just prior to the Dialog being shown.
     * @return
     * @see #getOnShowing()
     * @see #setOnShowing(EventHandler)
     */
    public final ObjectProperty<EventHandler<DialogEvent>> onShowingProperty() {
        return master.onShowingProperty();
    }
    public final void setOnShowing(EventHandler<DialogEvent> eventHandler) {
        this.onShowingProperty().set(eventHandler);
    }
    public final EventHandler<DialogEvent> getOnShowing() {
        return master.getOnShowing();
    }

    // On Shown
    /**
     * Called just after the Dialog is shown.
     * @return
     * @see #getOnShown()
     * @see #setOnShown(EventHandler)
     */
    public final ObjectProperty<EventHandler<DialogEvent>> onShownProperty() {
        return master.onShownProperty();
    }
    public final void setOnShown(EventHandler<DialogEvent> eventHandler) {
        this.onShownProperty().set(eventHandler);
    }
    public final EventHandler<DialogEvent> getOnShown() {
        return master.getOnShown();
    }

    // On Hiding
    /**
     * Called just prior to the Dialog being hidden.
     * @return
     * @see #getOnHiding()
     * @see #setOnHiding(EventHandler)
     */
    public final ObjectProperty<EventHandler<DialogEvent>> onHidingProperty() {
        return master.onHidingProperty();
    }
    public final void setOnHiding(EventHandler<DialogEvent> eventHandler) {
        this.onHidingProperty().set(eventHandler);
    }
    public final EventHandler<DialogEvent> getOnHiding() {
        return master.getOnHiding();
    }

    // On Hidden
    /**
     * Called just after the Dialog has been hidden.
     * When the {@code Dialog} is hidden,
     * this event handler is invoked allowing the developer to clean up resources or
     * perform other tasks when the Alert is closed.
     * @return
     * @see #getOnHidden()
     * @see #setOnHidden(EventHandler)
     */
    public final ObjectProperty<EventHandler<DialogEvent>> onHiddenProperty() {
        return master.onHiddenProperty();
    }
    public final void setOnHidden(EventHandler<DialogEvent> eventHandler) {
        this.onHiddenProperty().set(eventHandler);
    }
    public final EventHandler<DialogEvent> getOnHidden() {
        return master.getOnHidden();
    }

    // On Close Request
    /**
     * Called when there is an external request to close this Dialog.
     * The installed event handler can prevent dialog closing by consuming
     * the received event.
     * @return
     * @see #getOnCloseRequest()
     * @see #setOnCloseRequest(EventHandler)
     */
    public final ObjectProperty<EventHandler<DialogEvent>> onCloseRequestProperty() {
        return master.onCloseRequestProperty();
    }
    public final void setOnCloseRequest(EventHandler<DialogEvent> eventHandler) {
        this.onCloseRequestProperty().set(eventHandler);
    }
    public final EventHandler<DialogEvent> getOnCloseRequest() {
        return master.getOnCloseRequest();
    }
}
