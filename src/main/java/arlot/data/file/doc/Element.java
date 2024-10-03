package arlot.data.file.doc;

import org.jsoup.nodes.Attributes;
import org.jsoup.parser.Tag;
import org.jspecify.annotations.Nullable;

public class Element extends org.jsoup.nodes.Element {
    public Element(String tag, String namespace) {
        super(tag, namespace);
    }

    public Element(String tag) {
        super(tag);
    }

    public Element(Tag tag, @Nullable String baseUri, @Nullable Attributes attributes) {
        super(tag, baseUri, attributes);
    }

    public Element(Tag tag, @Nullable String baseUri) {
        super(tag, baseUri);
    }
}
