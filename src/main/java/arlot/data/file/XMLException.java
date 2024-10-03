package arlot.data.file;

import org.xml.sax.SAXParseException;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

public class XMLException extends TransformerException {
    private XMLLocator locator;
    /**
     * Create a new TransformerException.
     *
     * @param message The error or warning message.
     */
    public XMLException(String message) {
        super(message);
    }

    /**
     * Create a new TransformerException wrapping an existing exception.
     *
     * @param e The exception to be wrapped.
     */
    public XMLException(Throwable e) {
        super(e);
    }

    /**
     * Wrap an existing exception in a TransformerException.
     *
     * <p>This is used for throwing processor exceptions before
     * the processing has started.</p>
     *
     * @param message The error or warning message, or null to
     *                use the message from the embedded exception.
     * @param e       Any exception
     */
    public XMLException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * Create a new TransformerException from a message and a Locator.
     *
     * <p>This constructor is especially useful when an application is
     * creating its own exception from within a DocumentHandler
     * callback.</p>
     *
     * @param message The error or warning message.
     * @param locator The locator object for the error or warning.
     */
    public XMLException(String message, SourceLocator locator) {
        super(message, locator);
    }

    /**
     * Wrap an existing exception in a TransformerException.
     *
     * @param message The error or warning message, or null to
     *                use the message from the embedded exception.
     * @param locator The locator object for the error or warning.
     * @param e       Any exception
     */
    public XMLException(String message, SourceLocator locator, Throwable e) {
        super(message, locator, e);
    }

    public SAXParseException asSAXParse() {
        return new SAXParseException(getMessage(), this.locator);
    }

    /**
     * Method getLocator retrieves an instance of a SourceLocator
     * object that specifies where an error occurred.
     *
     * @return A SourceLocator object, or null if none was specified.
     */
    @Override
    public SourceLocator getLocator() {
        return this.locator;
    }

    /**
     * Method setLocator sets an instance of a SourceLocator
     * object that specifies where an error occurred.
     *
     * @param location A SourceLocator object, or null to clear the location.
     */
    @Override
    public void setLocator(SourceLocator location) {
        this.locator = new XMLLocator(location);
    }
}
