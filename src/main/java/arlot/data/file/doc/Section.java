package arlot.data.file.doc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Section extends Element {
    private final ArrayList<Section> children = new ArrayList<>();
    private Section parameters = null;
    private final String name;
    private final String returns;
    private final String type;
    private final String status;
    private final String level;
    private int aboutIndex = 0;

    public Section(Element parent, String name, String returns, String type, String... options) {
        super("section");
        String level1;
        String status1;
        this.name = name;
        this.returns = (returns==null?"":returns);
        this.type = type;
        try {
            status1 = options[0];
            level1 = options[1];
        } catch (Exception e) {
            status1 = "";
            level1 = "";
        }
        this.level = (level1==null?"":level1);
        this.status = (status1==null?"":status1);
        String tag = switch (type) {
            case "constructors", "methods", "parameters" -> "div";
            case "constructor", "method" -> "article";
            case "parameter" -> "section";
            default -> throw new IllegalArgumentException("Type not allowed.");
        };
        tagName(tag);
        attr("data-name", this.name);
        attr("data-return", this.returns);
        attr("data-type", this.type);
        attr("data-status", this.status);
        attr("data-level", this.level);
        addClass(type);
        Element displayName = getElement(type, tag);
        if (displayName != null) {
            displayName.html(formatted(this.name));
            appendChild(displayName);
            aboutIndex = childNodes().size();
        }

        Element data = null;
        if (type.equals("parameter")) {
            data = new Element("h5");
            data.addClass("paramType");
            data.html(formatted(this.returns));
        } else if (type.equals("method")) {
            data = new Element("p");
            data.addClass("methodData");
            data.appendChild(new Element("span").addClass("methodStatus").html(formatted(this.status)));
            data.appendText(" ");
            data.appendChild(new Element("span").addClass("methodLevel").html(formatted(this.level)));
            data.appendText(" ");
            data.appendChild(new Element("span").addClass("methodReturns").html(formatted(this.returns)));
        }
        if (data != null) {
            appendChild(data);
            aboutIndex = childNodes().size();
        }
        if (parent != null) {
            parent.appendChild(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getLevel() {
        return level;
    }

    public String getReturns() {
        return returns;
    }

    public List<Section> sections() {
        return Collections.unmodifiableList(children);
    }

    public List<Section> parameters() {
        return parameters.sections();
    }

    /**
     *
     * @param about The Element to add as the description of this Section.
     * @return This Section.
     */
    public Section about(Element about) {
        if (about.className().contains("about")) {
            insertChildren(aboutIndex, about);
        }
        return this;
    }

    /**
     *
     * @param text The text to add as the description of this Section.
     * @return This Section.
     */
    public Section about(String text) {
        return about(createAbout(text));
    }

    /**
     *
     * @param section The Section to add to this one.
     * @return This Section.
     */
    public Section addSection(Section section) {
        appendChild(section);
        children.add(section);
        return this;
    }

    /**
     *
     * @param name
     * @param returns
     * @param type
     * @param options
     * @return The newly created section.
     */
    public Section addSection(String name, String returns, String type, String... options) {
        Section ret = new Section(null, name, returns, type, options);
        addSection(ret);
        return ret;
    }

    /**
     *
     * @param name
     * @param type
     * @param about
     * @return This Section.
     */
    public Section parameter(String name, String type, String about) {
        if (this.type.equals("method") || this.type.equals("constructor")) {
            if (parameters == null) {
                parameters = new Section(this, "Parameters", null, "parameters");
            }
            Section param = parameters.addSection(name, type, "parameter");
            param.about(about);
        }
        return this;
    }

    private static Element getElement(String type, String tag) {
        Element displayName = null;
        if (tag.equals("div") && !type.equals("parameters")) {
            displayName = new Element("h2");
        } else if (tag.equals("article")) {
            displayName = new Element("h3");
            if (type.equals("constructor")) {
                displayName.addClass("constructorName");
            } else if (type.equals("method")) {
                displayName.addClass("methodName");
            }
        } else if (tag.equals("section")) {
            displayName = new Element("h4");
            displayName.addClass("paramName");
        }
        return displayName;
    }

    public static Element format(String tag, String text) {
        Element elm = new Element(tag);
        elm.html(formatted(text));
        return elm;
    }

    public static String formatted(String text) {
        text = text.replaceAll("/:/", "&#62;")
                .replaceAll("/;/", "&#60;");
        return text;
    }

    public static Element createAbout(String text) {
        Element section = new Element("section");
        section.addClass("about");
        String[] lines = text.split("\n");
        for (String line : lines) {
            section.appendChild(format("p", line));
        }
        return section;
    }

    public static Section parse(Element parent, Element element) {
        String name = element.attr("data-name");
        String type = element.attr("data-type");

        String returns = element.attr("data-return");
        String status = element.attr("data-status");
        String level = element.attr("data-level");
        //System.out.println(" ------------------ ");
        //element.attributes().forEach(System.out::println);
        //System.out.println(element.className());
        if (name.isBlank()) {
            try {
                if (element.hasClass("constructors")) {
                    name = "Constructors";
                } else if (element.hasClass("methods")) {
                    name = "Methods";
                } else if (element.hasClass("parameters")) {
                    name = "Parameters";
                } else if (element.hasClass("constructor")) {
                    name = Objects.requireNonNull(element.getElementsByTag("h3").first()).text();
                } else if (element.hasClass("method")) {
                    name = Objects.requireNonNull(element.getElementsByClass("methodName").first()).text();
                } else if (element.hasClass("parameter")) {
                    name = Objects.requireNonNull(element.getElementsByClass("paramName").first()).text();
                }
            } catch (Exception e) {
                name = "Section";
            }
        }

        if (type.isBlank()) {
            if (element.hasClass("constructors")) {
                type = "constructors";
            } else if (element.hasClass("methods")) {
                type = "methods";
            } else if (element.hasClass("parameters")) {
                type = "parameters";
            } else if (element.hasClass("constructor")) {
                type = "constructor";
            } else if (element.hasClass("method")) {
                type = "method";
            } else if (element.hasClass("parameter")) {
                type = "parameter";
            } else {
                type = "";
            }
        }

        if (returns.isBlank()) {
            try {
                if (element.hasClass("method")) {
                    returns = Objects.requireNonNull(element.getElementsByClass("methodReturn").first()).text();
                } else if (element.hasClass("parameter")) {
                    returns = Objects.requireNonNull(element.getElementsByClass("paramType").first()).text();
                }
            } catch (Exception e) {
                returns = "";
            }
        }

        if (status.isBlank()) {
            try {
                if (element.hasClass("method")) {
                    status = Objects.requireNonNull(element.getElementsByClass("methodStatus").first()).text();
                }
            } catch (Exception e) {
                status = "";
            }
        }

        if (level.isBlank()) {
            try {
                if (element.hasClass("method")) {
                    level = Objects.requireNonNull(element.getElementsByClass("methodLevel").first()).text();
                }
            } catch (Exception e) {
                level = "";
            }
        }

        //System.out.println(" ------------------ ");
        //System.out.println("name: "+name);
        //System.out.println("type: "+type);
        //System.out.println("returns: "+returns);
        Section main;
        try {
            main = new Section(parent, name, returns, type, status, level);

            if (main.getType().equals("constructors") || main.getType().equals("methods") || main.getType().equals("parameters")) {
                for (Element elm : element.children()) {
                    try {
                        main.addSection(parse(null, elm));
                    } catch (IllegalArgumentException ignore) {}
                }
                return main;
            }
        } catch (Exception e) {
            return null;
        }
        Element about = element.getElementsByClass("about").first();
        if (about != null) {
            main.about(about);
        }
        if (!element.hasClass("parameter") &&
                !element.getElementsByClass("parameters").isEmpty()) {
            main.parameters = parse(main,
                    Objects.requireNonNull(element.getElementsByClass("parameters").first()));
        }
        return main;
    }
}
