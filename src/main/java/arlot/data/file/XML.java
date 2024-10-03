package arlot.data.file;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;

// Java Program to Write XML Using DOM Parser
import javax.print.attribute.URISyntax;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XML {
    public static Document read(String filepath) throws IOException, SAXException {
        // Specify the file path as a File object
        File xmlFile = new File(filepath);

        // Parse the XML file
        Document document = Build.BUILD.parse(xmlFile);
        return document;
    }

    // Create a new Document
    private final Document document;
    private DOMSource source;
    private final File file;

    public XML(String filePath, String fileName, boolean isNew) throws IOException {
        String fPath = filePath+fileName;
        this.file = new File(fPath);
        if (isNew || !this.file.exists()) {
            isNew = this.file.createNewFile();
        }

        if (!isNew) {
            Document document1;
            try {
                document1 = Build.BUILD.parse(this.file);
            } catch (SAXException e) {
                document1 = Build.BUILD.newDocument();
            }
            this.document = document1;
        } else {
            this.document = Build.BUILD.newDocument();
        }

        // Write to XML file
        this.source = new DOMSource(this.document);
    }

    /**
     * Applies the data content of this XML document
     * to it's linked file.
     *
     * @return This XML document.
     * @throws TransformerException
     */
    public XML update() throws TransformerException {
        this.source = new DOMSource(this.document);
        // Specify your local file path
        StreamResult result;
        try {
            result = new StreamResult(new FileOutputStream(this.file));
        } catch (FileNotFoundException e) {
            result = new StreamResult(this.file);
        }
        Build.BUILD.transform(this.source, result);
        return this;
    }

    public Element create(String tagName, Node newChild) {
        Element elm = this.document.createElement(tagName);
        elm.appendChild(newChild);
        return elm;
    }

    public Node append(Node parent, Node child) {
        return parent.appendChild(child);
    }
    public Node append(Node child) {
        return append(this.document, child);
    }

    public Node add(Node root, String tagName, String data) {
        Element elm = create(tagName, this.document.createTextNode(data));
        return append(root, elm);
    }
    public Node add(String tagName, String data) {
        return add(this.document, tagName, data);
    }

    public Node remove(Node root, Node oldChild) {
        return root.removeChild(oldChild);
    }
    public Node remove(Node oldChild) {
        return remove(this.document, oldChild);
    }

    public static class Build extends DocumentBuilder {
        // Create a DocumentBuilder
        public static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        public static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

        public static final Build BUILD = new Build();

        private final DocumentBuilder builder;
        private final Transformer transformer;

        public Build() {
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Obtain a new instance of a DOM {@link Document} object
         * to build a DOM tree with.
         *
         * @return A new instance of a DOM Document object.
         */
        @Override
        public Document newDocument() {
            return builder.newDocument();
        }

        /**
         * Obtain an instance of a {@link DOMImplementation} object.
         *
         * @return A new instance of a <code>DOMImplementation</code>.
         */
        @Override
        public DOMImplementation getDOMImplementation() {
            return builder.getDOMImplementation();
        }

        /**
         * Parse the content of the given input source as an XML document
         * and return a new DOM {@link Document} object.
         * An <code>IllegalArgumentException</code> is thrown if the
         * <code>InputSource</code> is <code>null</code>.
         *
         * @param is InputSource containing the content to be parsed.
         *
         * @return A new DOM Document object.
         *
         * @throws IOException If any IO errors occur.
         * @throws SAXException If any parse errors occur.
         * @throws IllegalArgumentException When <code>is</code> is <code>null</code>
         */
        @Override
        public Document parse(InputSource is) throws IOException, SAXException {
            return builder.parse(is);
        }

        /**
         * Indicates whether or not this parser is configured to
         * understand namespaces.
         *
         * @return true if this parser is configured to understand
         * namespaces; false otherwise.
         */
        @Override
        public boolean isNamespaceAware() {
            return builder.isNamespaceAware();
        }

        /**
         * Indicates whether or not this parser is configured to
         * validate XML documents.
         *
         * @return true if this parser is configured to validate
         * XML documents; false otherwise.
         */
        @Override
        public boolean isValidating() {
            return builder.isValidating();
        }

        /**
         * Specify the {@link EntityResolver} to be used to resolve
         * entities present in the XML document to be parsed. Setting
         * this to <code>null</code> will result in the underlying
         * implementation using it's own default implementation and
         * behavior.
         *
         * @param er The <code>EntityResolver</code> to be used to resolve entities
         *           present in the XML document to be parsed.
         */
        @Override
        public void setEntityResolver(EntityResolver er) {
            builder.setEntityResolver(er);
        }

        /**
         * Specify the {@link ErrorHandler} to be used by the parser.
         * Setting this to <code>null</code> will result in the underlying
         * implementation using it's own default implementation and
         * behavior.
         *
         * @param eh The <code>ErrorHandler</code> to be used by the parser.
         */
        @Override
        public void setErrorHandler(ErrorHandler eh) {
            builder.setErrorHandler(eh);
        }


        // transformer
        /**
         * <p>Transform the XML <code>Source</code> to a <code>Result</code>.
         * Specific transformation behavior is determined by the settings of the
         * <code>TransformerFactory</code> in effect when the
         * <code>Transformer</code> was instantiated and any modifications made to
         * the <code>Transformer</code> instance.</p>
         *
         * <p>An empty <code>Source</code> is represented as an empty document
         * as constructed by {@link javax.xml.parsers.DocumentBuilder#newDocument()}.
         * The result of transforming an empty <code>Source</code> depends on
         * the transformation behavior; it is not always an empty
         * <code>Result</code>.</p>
         *
         * @param xmlSource The XML input to transform.
         * @param outputTarget The <code>Result</code> of transforming the
         *   <code>xmlSource</code>.
         *
         * @throws TransformerException If an unrecoverable error occurs
         *   during the course of the transformation.
         */
        public void transform(Source xmlSource, Result outputTarget) throws TransformerException {
            transformer.transform(xmlSource, outputTarget);
        }

        /**
         * Add a parameter for the transformation.
         *
         * <p>Pass a qualified name as a two-part string, the namespace URI
         * enclosed in curly braces ({}), followed by the local name. If the
         * name has a null URL, the String only contain the local name. An
         * application can safely check for a non-null URI by testing to see if the
         * first character of the name is a '{' character.</p>
         * <p>For example, if a URI and local name were obtained from an element
         * defined with &lt;xyz:foo
         * xmlns:xyz="http://xyz.foo.com/yada/baz.html"/&gt;,
         * then the qualified name would be "{http://xyz.foo.com/yada/baz.html}foo".
         * Note that no prefix is used.</p>
         *
         * @param name The name of the parameter, which may begin with a
         * namespace URI in curly braces ({}).
         * @param value The value object.  This can be any valid Java object. It is
         * up to the processor to provide the proper object conversion or to simply
         * pass the object on for use in an extension.
         *
         * @throws NullPointerException If value is null.
         */
        public void setParameter(String name, Object value) {
            transformer.setParameter(name, value);
        }

        /**
         * Get a parameter that was explicitly set with setParameter.
         *
         * <p>This method does not return a default parameter value, which
         * cannot be determined until the node context is evaluated during
         * the transformation process.
         *
         * @param name of <code>Object</code> to get
         *
         * @return A parameter that has been set with setParameter.
         */
        public Object getParameter(String name) {
            return transformer.getParameter(name);
        }

        /**
         * Clear all parameters set with setParameter.
         */
        public void clearParameters() {
            transformer.clearParameters();
        }

        /**
         * Set the error event listener in effect for the transformation.
         *
         * @param listener The new error listener.
         *
         * @throws IllegalArgumentException if listener is null.
         */
        public void setErrorListener(ErrorListener listener)
                throws IllegalArgumentException {
            transformer.setErrorListener(listener);
        }

        /**
         * Get the error event handler in effect for the transformation.
         * Implementations must provide a default error listener.
         *
         * @return The current error handler, which should never be null.
         */
        public ErrorListener getErrorListener() {
            return transformer.getErrorListener();
        }
    }

    static class XMLTransformer extends Transformer {
        private final Properties parameters;
        private final Properties outputProperties;

        private ErrorListener errorListener;
        private URIResolver uriResolver = null;

        public XMLTransformer() {
            parameters = new Properties();
            outputProperties = new Properties();
            errorListener = new DefaultErrorListener();
        }

        private TransformerException toTE(String message, Throwable e) {
            return new TransformerException(message, e);
        }

        /**
         * <p>Transform the XML <code>Source</code> to a <code>Result</code>.
         * Specific transformation behavior is determined by the settings of the
         * <code>TransformerFactory</code> in effect when the
         * <code>Transformer</code> was instantiated and any modifications made to
         * the <code>Transformer</code> instance.</p>
         *
         * <p>An empty <code>Source</code> is represented as an empty document
         * as constructed by {@link DocumentBuilder#newDocument()}.
         * The result of transforming an empty <code>Source</code> depends on
         * the transformation behavior; it is not always an empty
         * <code>Result</code>.</p>
         *
         * @param xmlSource    The XML input to transform.
         * @param outputTarget The <code>Result</code> of transforming the
         *                     <code>xmlSource</code>.
         * @throws TransformerException If an unrecoverable error occurs
         *                              during the course of the transformation.
         */
        @Override
        public void transform(Source xmlSource, Result outputTarget) throws TransformerException {
            boolean empty = false;
            try {
                empty = xmlSource.isEmpty();
            } catch (UnsupportedOperationException e) {
                errorListener.warning(toTE("Cannot check Source emptiness with xmlSource.isEmpty().", e));
            }
            if (!empty) {
                if (xmlSource instanceof StreamSource streamSource) {
                    try {
                        xmlSource = new DOMSource(Build.BUILD.parse(streamSource.getInputStream()));
                    } catch (SAXException | IOException e) {
                        errorListener.fatalError(toTE("Could not parse xmlSource.", e));
                    }
                } else if (xmlSource instanceof SAXSource saxSource) {
                    try {
                        xmlSource = new DOMSource(Build.BUILD.parse(saxSource.getInputSource()));
                    } catch (SAXException | IOException e) {
                        errorListener.fatalError(toTE("Could not parse xmlSource.", e));
                    }
                }
                if (xmlSource instanceof DOMSource domSource) {
                    if (!domSource.isEmpty()) {
                        Node node = domSource.getNode();
                        BufferedOutputStream bos = null;
                        if (outputTarget instanceof StreamResult) {
                            bos = new BufferedOutputStream(((StreamResult)outputTarget).getOutputStream());
                        }
                        if (bos != null) {
                            if (node instanceof Document document) {

                            }
                        }
                    }
                }
            }
        }

        /**
         * Add a parameter for the transformation.
         *
         * <p>Pass a qualified name as a two-part string, the namespace URI
         * enclosed in curly braces ({}), followed by the local name. If the
         * name has a null URL, the String only contain the local name. An
         * application can safely check for a non-null URI by testing to see if the
         * first character of the name is a '{' character.</p>
         * <p>For example, if a URI and local name were obtained from an element
         * defined with &lt;xyz:foo
         * xmlns:xyz="http://xyz.foo.com/yada/baz.html"/&gt;,
         * then the qualified name would be "{http://xyz.foo.com/yada/baz.html}foo".
         * Note that no prefix is used.</p>
         *
         * @param name  The name of the parameter, which may begin with a
         *              namespace URI in curly braces ({}).
         * @param value The value object. This can be any valid Java object. It is
         *              up to the processor to provide the proper object conversion or to simply
         *              pass the object on for use in an extension.
         * @throws NullPointerException If value is null.
         */
        @Override
        public void setParameter(String name, Object value) {
            parameters.put(name, value);
        }

        /**
         * Get a parameter that was explicitly set with setParameter.
         *
         * <p>This method does not return a default parameter value, which
         * cannot be determined until the node context is evaluated during
         * the transformation process.
         *
         * @param name of <code>Object</code> to get
         * @return A parameter that has been set with setParameter.
         */
        @Override
        public Object getParameter(String name) {
            return parameters.get(name);
        }

        /**
         * Clear all parameters set with setParameter.
         */
        @Override
        public void clearParameters() {
            parameters.clear();
        }

        /**
         * Set an object that will be used to resolve URIs used in
         * document().
         *
         * <p>If the resolver argument is null, the URIResolver value will
         * be cleared and the transformer will no longer have a resolver.</p>
         *
         * @param resolver An object that implements the URIResolver interface,
         *                 or null.
         */
        @Override
        public void setURIResolver(URIResolver resolver) {
            uriResolver = resolver;
        }

        /**
         * Get an object that will be used to resolve URIs used in
         * document().
         *
         * @return An object that implements the URIResolver interface,
         * or null.
         */
        @Override
        public URIResolver getURIResolver() {
            return uriResolver;
        }

        /**
         * Set the output properties for the transformation. These
         * properties will override properties set in the Templates
         * with xsl:output.
         *
         * <p>If argument to this function is null, any properties
         * previously set are removed, and the value will revert to the value
         * defined in the templates object.</p>
         *
         * <p>Pass a qualified property key name as a two-part string, the namespace
         * URI enclosed in curly braces ({}), followed by the local name. If the
         * name has a null URL, the String only contain the local name. An
         * application can safely check for a non-null URI by testing to see if the
         * first character of the name is a '{' character.</p>
         * <p>For example, if a URI and local name were obtained from an element
         * defined with &lt;xyz:foo
         * xmlns:xyz="http://xyz.foo.com/yada/baz.html"/&gt;,
         * then the qualified name would be "{http://xyz.foo.com/yada/baz.html}foo".
         * Note that no prefix is used.</p>
         * An <code>IllegalArgumentException</code> is thrown if any of the
         * argument keys are not recognized and are not namespace qualified.
         *
         * @param oformat A set of output properties that will be
         *                used to override any of the same properties in affect
         *                for the transformation.
         * @throws IllegalArgumentException When keys are not recognized and
         *                                  are not namespace qualified.
         * @see OutputKeys
         * @see Properties
         */
        @Override
        public void setOutputProperties(Properties oformat) {
            if (oformat == null) {
                outputProperties.clear();
            } else {
                outputProperties.putAll(oformat);
            }
        }

        /**
         * <p>Get a copy of the output properties for the transformation.</p>
         *
         * <p>The properties returned should contain properties set by the user,
         * and properties set by the stylesheet, and these properties
         * are "defaulted" by default properties specified by
         * <a href="http://www.w3.org/TR/xslt#output">section 16 of the
         * XSL Transformations (XSLT) W3C Recommendation</a>.  The properties that
         * were specifically set by the user or the stylesheet should be in the base
         * Properties list, while the XSLT default properties that were not
         * specifically set should be the default Properties list.  Thus,
         * getOutputProperties().getProperty(String key) will obtain any
         * property in that was set by {@link #setOutputProperty},
         * {@link #setOutputProperties}, in the stylesheet, <em>or</em> the default
         * properties, while
         * getOutputProperties().get(String key) will only retrieve properties
         * that were explicitly set by {@link #setOutputProperty},
         * {@link #setOutputProperties}, or in the stylesheet.</p>
         *
         * <p>Note that mutation of the Properties object returned will not
         * effect the properties that the transformer contains.</p>
         *
         * <p>If any of the argument keys are not recognized and are not
         * namespace qualified, the property will be ignored and not returned.
         * In other words the behaviour is not orthogonal with
         * {@link #setOutputProperties setOutputProperties}.</p>
         *
         * @return A copy of the set of output properties in effect for
         * the next transformation.
         * @see OutputKeys
         * @see Properties
         * @see <a href="http://www.w3.org/TR/xslt#output">
         * XSL Transformations (XSLT) Version 1.0</a>
         */
        @Override
        public Properties getOutputProperties() {
            return (Properties) outputProperties.clone();
        }

        /**
         * Set an output property that will be in effect for the
         * transformation.
         *
         * <p>Pass a qualified property name as a two-part string, the namespace URI
         * enclosed in curly braces ({}), followed by the local name. If the
         * name has a null URL, the String only contain the local name. An
         * application can safely check for a non-null URI by testing to see if the
         * first character of the name is a '{' character.</p>
         * <p>For example, if a URI and local name were obtained from an element
         * defined with &lt;xyz:foo
         * xmlns:xyz="http://xyz.foo.com/yada/baz.html"/&gt;,
         * then the qualified name would be "{http://xyz.foo.com/yada/baz.html}foo".
         * Note that no prefix is used.</p>
         *
         * <p>The Properties object that was passed to {@link #setOutputProperties}
         * won't be effected by calling this method.</p>
         *
         * @param name  A non-null String that specifies an output
         *              property name, which may be namespace qualified.
         * @param value The non-null string value of the output property.
         * @throws IllegalArgumentException If the property is not supported, and is
         *                                  not qualified with a namespace.
         * @see OutputKeys
         */
        @Override
        public void setOutputProperty(String name, String value) throws IllegalArgumentException {
            outputProperties.setProperty(name, value);
        }

        /**
         * <p>Get an output property that is in effect for the transformer.</p>
         *
         * <p>If a property has been set using {@link #setOutputProperty},
         * that value will be returned. Otherwise, if a property is explicitly
         * specified in the stylesheet, that value will be returned. If
         * the value of the property has been defaulted, that is, if no
         * value has been set explicitly either with {@link #setOutputProperty} or
         * in the stylesheet, the result may vary depending on
         * implementation and input stylesheet.</p>
         *
         * @param name A non-null String that specifies an output
         *             property name, which may be namespace qualified.
         * @return The string value of the output property, or null
         * if no property was found.
         * @throws IllegalArgumentException If the property is not supported.
         * @see OutputKeys
         */
        @Override
        public String getOutputProperty(String name) throws IllegalArgumentException {
            return outputProperties.getProperty(name);
        }

        /**
         * Set the error event listener in effect for the transformation.
         *
         * @param listener The new error listener.
         * @throws IllegalArgumentException if listener is null.
         */
        @Override
        public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
            Objects.requireNonNull(listener);
            errorListener = listener;
        }

        /**
         * Get the error event handler in effect for the transformation.
         *
         * @return The current error handler, which should never be null.
         */
        @Override
        public ErrorListener getErrorListener() {
            return errorListener;
        }

        public static class DefaultErrorListener implements ErrorListener {
            private int warnCount = 0;
            private int errorCount = 0;
            private int fatalErrorCount = 0;

            private final int maxWarnCount;
            private final int maxErrorCount;
            private final int maxFatalErrorCount;

            public boolean throwFatalError = true;
            public DefaultErrorListener() {
                this(10, 4);
            }

            public DefaultErrorListener(int maxCount) {
                this.maxWarnCount = (int) Math.floor(maxCount/1.4);
                this.maxErrorCount = maxCount-this.maxWarnCount;
                this.maxFatalErrorCount = this.maxWarnCount/this.maxErrorCount;
            }

            public DefaultErrorListener(int maxWarnCount, int maxErrorCount) {
                if (maxErrorCount >= maxWarnCount) {
                    throw new IllegalArgumentException("`maxErrorCount` must be less than `maxWarnCount`");
                }
                this.maxWarnCount = maxWarnCount;
                this.maxErrorCount = maxErrorCount;
                this.maxFatalErrorCount = maxWarnCount/maxErrorCount;
            }

            private String display(Throwable e, int level, String type, boolean show, boolean thr) {
                String tabs;
                if (level <= 0) {
                    level = 0;
                    tabs = "";
                } else {
                    tabs = "\t".repeat(level);
                }
                TransformerException te;
                if (e instanceof TransformerException) {
                    te = (TransformerException) e;
                } else {
                    te = new TransformerException(e);
                }
                String name = (te.getException()==null?te:te.getException()).getClass().getName();
                String location = Objects.toString(te.getLocationAsString(), "");
                String message = Objects.toString(te.getMessage(), "").replace(name+": ", "");
                StringBuilder sb = new StringBuilder();
                if (show) {
                    sb.append(tabs)
                            .append(type)
                            .append("! ")
                            .append(name)
                            .append(location != null ? " at " + location : "")
                            .append(": ")
                            .append(message)
                            .append("\n");
                    for (StackTraceElement ste : te.getStackTrace()) {
                        sb.append(tabs).append("\tat ").append(ste.toString()).append("\n");
                    }
                    for (Throwable t : te.getSuppressed()) {
                        sb.append(tabs).append("Caused By:").append("\n");
                        sb.append(display(t, level+1, "Caught Error", false, false)).append("\n");
                    }

                    System.err.println(sb);
                }
                if (thr) {
                    throw new Error(type+": "+message, e);
                }
                return sb.toString();
            }
            private String display(Throwable e, String type, boolean thr) {
                return display(e, 0, type, !thr, thr);
            }

            /**
             * Receive notification of a warning.
             *
             * <p>{@link Transformer} can use this method to report
             * conditions that are not errors or fatal errors. The default behaviour
             * is to take no action.</p>
             *
             * <p>After invoking this method, the Transformer must continue with
             * the transformation. It should still be possible for the
             * application to process the document through to the end.</p>
             *
             * @param exception The warning information encapsulated in a
             *                  transformer exception.
             * @throws TransformerException if the application
             *                              chooses to discontinue the transformation.
             * @see TransformerException
             */
            @Override
            public void warning(TransformerException exception) throws TransformerException {
                warnCount++;
                display(exception, "Warning", warnCount > maxWarnCount);
            }

            /**
             * Receive notification of a recoverable error.
             *
             * <p>The transformer must continue to try and provide normal transformation
             * after invoking this method.  It should still be possible for the
             * application to process the document through to the end if no other errors
             * are encountered.</p>
             *
             * @param exception The error information encapsulated in a
             *                  transformer exception.
             * @throws TransformerException if the application
             *                              chooses to discontinue the transformation.
             * @see TransformerException
             */
            @Override
            public void error(TransformerException exception) throws TransformerException {
                errorCount++;
                display(exception, "Error", errorCount > maxErrorCount);
            }

            /**
             * <p>Receive notification of a non-recoverable error.</p>
             *
             * <p>The processor may choose to continue, but will not normally
             * proceed to a successful completion.</p>
             *
             * <p>The method should throw an exception if it is unable to
             * process the error, or if it wishes execution to terminate
             * immediately. The processor will not necessarily honor this
             * request.</p>
             *
             * @param exception The error information encapsulated in a
             *                  <code>TransformerException</code>.
             * @throws TransformerException if the application
             *                              chooses to discontinue the transformation.
             * @see TransformerException
             */
            @Override
            public void fatalError(TransformerException exception) throws TransformerException {
                fatalErrorCount++;
                display(exception, "Fatal Error",
                        throwFatalError || fatalErrorCount > maxFatalErrorCount);
            }
        }
    }
}
