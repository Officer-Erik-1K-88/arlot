package arlot.data.file;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMLocator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class XMLLocator implements Locator, SourceLocator, DOMLocator {
    private String publicID;
    private String systemID;
    private int lineNumber = -1;
    private int columnNumber = -1;

    private Node location = null;
    private URI uri;

    public XMLLocator(SourceLocator locator) {
        publicID = locator.getPublicId();
        systemID = locator.getSystemId();
        lineNumber = locator.getLineNumber();
        columnNumber = locator.getColumnNumber();
        if (locator instanceof DOMLocator) {
            location = ((DOMLocator) locator).getOriginatingNode();
            updateURI();
        }
    }

    public XMLLocator(Locator locator) {
        publicID = locator.getPublicId();
        systemID = locator.getSystemId();
        lineNumber = locator.getLineNumber();
        columnNumber = locator.getColumnNumber();
    }

    public XMLLocator(DOMLocator locator) {
        publicID = locator.getPublicId();
        systemID = locator.getSystemId();
        lineNumber = locator.getLineNumber();
        columnNumber = locator.getColumnNumber();
        location = locator.getOriginatingNode();
        updateURI();
    }

    public XMLLocator(String publicID, String systemID,
                      int lineNumber, int columnNumber, Node location) {
        this.publicID = publicID;
        this.systemID = systemID;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.location = location;
        updateURI();
    }

    public XMLLocator(String publicID, String systemID, Node location) {
        this.publicID = publicID;
        this.systemID = systemID;
        this.location = location;
        updateURI();
    }

    public XMLLocator(String publicID, String systemID) {
        this.publicID = publicID;
        this.systemID = systemID;
    }

    void updateURI() {
        if (location != null) {
            try {
                uri = new URI(location.getBaseURI());
            } catch (URISyntaxException e) {
                uri = null;
            }
        } else {
            uri = null;
        }
    }


    /**
     * Return the public identifier for the current document event.
     *
     * <p>The return value is the public identifier of the document
     * entity or of the external parsed entity in which the markup
     * triggering the event appears.</p>
     *
     * @return A string containing the public identifier, or
     * null if none is available.
     * @see #getSystemId
     */
    @Override
    public String getPublicId() {
        return publicID;
    }

    /**
     * Set the public identifier for this locator.
     *
     * @param publicID The new public identifier, or null
     *        if none is available.
     * @see #getPublicId
     */
    public void setPublicId(String publicID) {
        this.publicID = publicID;
    }

    /**
     * Return the system identifier for the current document event.
     *
     * <p>The return value is the system identifier of the document
     * entity or of the external parsed entity in which the markup
     * triggering the event appears.</p>
     *
     * <p>If the system identifier is a URL, the parser must resolve it
     * fully before passing it to the application.  For example, a file
     * name must always be provided as a <em>file:...</em> URL, and other
     * kinds of relative URI are also resolved against their bases.</p>
     *
     * @return A string containing the system identifier, or null
     * if none is available.
     * @see #getPublicId
     */
    @Override
    public String getSystemId() {
        return systemID;
    }

    /**
     * Set the system identifier for this locator.
     *
     * @param systemID The new system identifier, or null
     *        if none is available.
     * @see #getSystemId
     */
    public void setSystemId(String systemID) {
        this.systemID = systemID;
    }

    /**
     * Return the line number where the current document event ends.
     * Lines are delimited by line ends, which are defined in
     * the XML specification.
     *
     * <p><strong>Warning:</strong> The return value from the method
     * is intended only as an approximation for the sake of diagnostics;
     * it is not intended to provide sufficient information
     * to edit the character content of the original XML document.
     * In some cases, these "line" numbers match what would be displayed
     * as columns, and in others they may not match the source text
     * due to internal entity expansion.  </p>
     *
     * <p>The return value is an approximation of the line number
     * in the document entity or external parsed entity where the
     * markup triggering the event appears.</p>
     *
     * <p>If possible, the SAX driver should provide the line position
     * of the first character after the text associated with the document
     * event.  The first line is line 1.</p>
     *
     * @return The line number, or -1 if none is available.
     * @see #getColumnNumber
     */
    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Set the line number for this locator (1-based).
     *
     * @param lineNumber The line number, or -1 if none is available.
     * @see #getLineNumber
     */
    public void setLineNumber (int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Return the column number where the current document event ends.
     * This is one-based number of Java <code>char</code> values since
     * the last line end.
     *
     * <p><strong>Warning:</strong> The return value from the method
     * is intended only as an approximation for the sake of diagnostics;
     * it is not intended to provide sufficient information
     * to edit the character content of the original XML document.
     * For example, when lines contain combining character sequences, wide
     * characters, surrogate pairs, or bi-directional text, the value may
     * not correspond to the column in a text editor's display. </p>
     *
     * <p>The return value is an approximation of the column number
     * in the document entity or external parsed entity where the
     * markup triggering the event appears.</p>
     *
     * <p>If possible, the SAX driver should provide the line position
     * of the first character after the text associated with the document
     * event.  The first column in each line is column 1.</p>
     *
     * @return The column number, or -1 if none is available.
     * @see #getLineNumber
     */
    @Override
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Set the column number for this locator (1-based).
     *
     * @param columnNumber The column number, or -1 if none is available.
     * @see #getColumnNumber
     */
    public void setColumnNumber (int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * Return the node where the event occurred.
     *
     * @return The node that is the location for the event.
     * Or <code>null</code> if the node is unidentified.
     */
    @Override
    public Node getOriginatingNode() {
        return location;
    }

    /**
     * The node this locator is pointing to, or <code>null</code> if no node
     * is available.
     */
    public Node getRelatedNode() {
        return getOriginatingNode();
    }

    /**
     * Set the event occurrence node for this locator.
     *
     * <br><br>
     *
     * Changing this will change the {@link #getUri() uri}.
     * @param location The new node where the event occurred.
     * @see #getOriginatingNode
     */
    public void setOriginatingNode(Node location) {
        this.location = location;
        updateURI();
    }

    /**
     * The URI this locator is pointing to, or <code>null</code> if no URI is
     * available.
     * <br>
     * If the stored URI value is <code>null</code> then the
     * {@link Node#getBaseURI() base URI} of the node will be used.
     */
    public String getUri() {
        if (uri != null) {
            return uri.toString();
        }
        if (location!=null) {
            return location.getBaseURI();
        }
        return null;
    }

    /**
     * Set the URI this locator is pointing to. The URI of this locator
     * will be changed and updated based on the {@link #getOriginatingNode() location}.
     * <br><br>
     * Changing this will change {@link #getOriginatingNode() location}.
     * @param uri The URI to set this locator with.
     * @see #getUri()
     */
    public void setUri(URI uri) {
        this.uri = uri;
        try {
            this.location = XML.Build.BUILD.parse(this.uri.toString());
        } catch (IOException | SAXException e) {
            this.location = null;
        }
    }

    /**
     * Get an InputSource that refers to the URI of this locator.
     *
     * @return The InputSource that was constructed using the held URI.
     * Or <code>null</code> if not possible.
     */
    public InputSource getSource() {
        try {
            return new InputSource(uri.toURL().openStream());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the name of the character encoding for the entity.
     * If the encoding was declared externally (for example, in a MIME
     * Content-Type header), that will be the name returned.  Else if there
     * was an <em>&lt;?xml&nbsp;...encoding='...'?&gt;</em> declaration at
     * the start of the document, that encoding name will be returned.
     * Otherwise the encoding will been inferred (normally to be UTF-8, or
     * some UTF-16 variant), and that inferred name will be returned.
     *
     * <p>When an {@link org.xml.sax.InputSource InputSource} is used
     * to provide an entity's character stream, this method returns the
     * encoding provided in that input stream.
     *
     * <p> Note that some recent W3C specifications require that text
     * in some encodings be normalized, using Unicode Normalization
     * Form C, before processing.  Such normalization must be performed
     * by applications, and would normally be triggered based on the
     * value returned by this method.
     *
     * <p> Encoding names may be those used by the underlying JVM,
     * and comparisons should be case-insensitive.
     *
     * @return Name of the character encoding being used to interpret
     * * the entity's text, or null if this was not provided for a *
     * character stream passed through an InputSource or is otherwise
     * not yet available in the current parsing state.
     */
    public String getEncoding() {
        try {
            return getSource().getEncoding();
        } catch (Exception e) {
            return null;
        }
    }
}
