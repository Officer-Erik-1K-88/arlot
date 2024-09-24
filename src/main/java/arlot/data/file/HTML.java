package arlot.data.file;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.parser.Tag;
import org.jsoup.select.Evaluator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Objects;

public class HTML {
    private final Document document;
    private final Element head;
    private final Element body;

    public HTML() {
        StringBuilder htmlFile = new StringBuilder();
        htmlFile.append("<html>");
        htmlFile.append("<head>");
        htmlFile.append("<meta charset=").append('"').append("UTF-8").append('"').append('>');
        htmlFile.append("<title>").append("New HTML File").append("</title>");
        htmlFile.append("</head>");
        htmlFile.append("<body>");
        htmlFile.append("<body>");
        htmlFile.append("</html>");
        document = Jsoup.parse(htmlFile.toString());
        head = document.head();
        body = document.body();
    }

    public HTML(String html) {
        document = Jsoup.parse(html);
        head = document.head();
        body = document.body();
    }

    public HTML(File htmlFile) throws IOException {
        if (!htmlFile.exists() || !htmlFile.isFile() ||
                !htmlFile.getName().toLowerCase().endsWith(".html")) {
            throw new IOException("File provided doesn't exist, is a directory, or isn't an html file.");
        }
        Document document1;
        try {
            document1 = Jsoup.parse(htmlFile);
        } catch (IOException e) {
            try(BufferedReader br = new BufferedReader(new FileReader(htmlFile))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                document1 = Jsoup.parse(sb.toString());
            } catch (IOException e2) {
                throw new IOException("File provided couldn't be parsed.");
            }
        }
        document = document1;
        head = document.head();
        body = document.body();
    }

    public HTML(Document document) {
        this.document = document;
        head = this.document.head();
        body = this.document.body();
    }

    public Element head() {
        return head;
    }

    public Element body() {
        return body;
    }

    public Element title() {
        return document.getElementsByTag("title").first();
    }

    public void addStyleSheet(String filePath) {
        if (head.getElementsByAttributeValue("href", filePath).isEmpty()) {
            Element link = new Element("link");
            link.attr("href", filePath);
            link.attr("rel", "stylesheet");
            head.appendChild(link);
        }
    }

    public void addScriptFile(String filePath, String type, boolean body) {
        Element script = new Element("script");
        script.attr("src", filePath);
        script.attr("type", type);
        if (body) {
            if (this.body.getElementsByAttributeValue("src", filePath).isEmpty()) {
                this.body.appendChild(script);
            }
        } else {
            if (head.getElementsByAttributeValue("src", filePath).isEmpty()) {
                head.appendChild(script);
            }
        }
    }

    public void addScript(String text, String type, boolean body) {
        Element script = new Element("script");
        script.attr("type", type);
        script.appendText(text);
        if (body) {
            for (Element scrii : this.body.getElementsByTag("script")) {
                if (scrii.wholeText().equals(text)) {
                    return;
                }
            }
            this.body.appendChild(script);
        } else {
            for (Element scrii : head.getElementsByTag("script")) {
                if (scrii.wholeText().equals(text)) {
                    return;
                }
            }
            head.appendChild(script);
        }
    }

    public void addJavaScriptFile(String filePath, boolean body) {
        addScriptFile(filePath, "text/javascript", body);
    }

    public void addJavaScript(String javascript, boolean body) {
        addScript(javascript, "text/javascript", body);
    }

    public void appendChild(Node child) {
        body.appendChild(child);
    }

    public void appendChildren(Collection<? extends Node> children) {
        body.appendChildren(children);
    }

    public File toFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public String toString() {
        return document.toString();
    }

    /* --------------------------------------------- */
    // static

    public static class Elm extends Element {
        public Elm(Tag tag, @Nullable String baseUri, @Nullable Attributes attributes) {
            super(tag, baseUri, attributes);
        }
    }

    public static class Meta extends Element {
        private String name;
        private String content;
        public Meta() {
            super("meta");
        }

        public Meta(String name, String content) {
            super("meta");
            if (Objects.equals(name, "charset")) {
                charset(content);
            } else {
                this.name(name);
                this.content(content);
            }
        }

        public String name() {
            return name;
        }

        public void name(String name) {
            this.name = name;
            attr("name", this.name);
        }

        public String content() {
            return content;
        }

        public void content(String content) {
            this.content = content;
            attr("content", this.content);
        }

        public Element update(String name, String content) {
            this.name(name);
            this.content(content);
            return this;
        }

        public String charset() {
            return attr("charset");
        }

        public void charset(String charset) {
            this.name = "charset";
            this.content = charset;
            attr("charset", charset);
        }

        private void attributeCheck(@NonNull String attributeKey) {
            String[] allowed = new String[]{
                    "name", "charset", "content", "http-equiv"
            };
            for (String allow : allowed) {
                if (attributeKey.equals(allow)) {
                    return;
                }
            }
            throw new IllegalArgumentException("Meta can't use "+attributeKey+" attribute.");
        }

        @Override
        public Meta attr(@NonNull String attributeKey, @NonNull String attributeValue) {
            attributeCheck(attributeKey);
            if (attributeKey.equals("name")) {
                name = attributeValue;
            } else if (attributeKey.equals("content")) {
                content = attributeValue;
            }
            super.attr(attributeKey, attributeValue);
            return this;
        }

        @Override
        public Meta attr(@NonNull String attributeKey, boolean attributeValue) {
            attributeCheck(attributeKey);
            super.attr(attributeKey, attributeValue);
            return this;
        }
    }
}
