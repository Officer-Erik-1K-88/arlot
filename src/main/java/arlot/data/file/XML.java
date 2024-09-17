package arlot.data.file;

import java.io.IOException;

import org.w3c.dom.Node;
import java.io.File;

// Java Program to Write XML Using DOM Parser
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

class Build {
    // Create a DocumentBuilder
    public static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    public static final DocumentBuilder builder;

    static {
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    public static final Transformer transformer;

    static {
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document newDocument() {
        return builder.newDocument();
    }
}

class ReadXML {
    private Document document;
    private File file;
    public ReadXML(String filepath) throws IOException, SAXException {
        // Specify the file path as a File object
        this.file = new File(filepath);

        // Parse the XML file
        this.document = Build.builder.parse(this.file);
    }
}

public class XML {
    public static Document read(String filepath) throws IOException, SAXException {
        // Specify the file path as a File object
        File xmlFile = new File(filepath);

        // Parse the XML file
        Document document = Build.builder.parse(xmlFile);
        return document;
    }

    // Create a new Document
    private final Document document = Build.newDocument();
    private DOMSource source;
    private File file;
    public XML(String filename) throws IOException {
        // Write to XML file
        this.source = new DOMSource(this.document);

        this.file = new File(filename+".xml");
        this.file.createNewFile();
    }

    public void update() throws TransformerException {
        this.source = new DOMSource(this.document);
        // Specify your local file path
        StreamResult result = new StreamResult(this.file);
        Build.transformer.transform(this.source, result);
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
        return this.document.appendChild(child);
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
        return this.document.removeChild(oldChild);
    }
}
