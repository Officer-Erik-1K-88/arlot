package arlot.data.file.doc;

import arlot.data.collect.Options;
import arlot.data.collect.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class Documentation {
    public final static Options<String> options = new Options<>(Arrays.asList(
            new Pair<>("sheetPath", "resources/"),
            new Pair<>("stylesheets", Arrays.asList(
                    "base_reset.css", "base_style.css", "file_style.css"
            )),
            new Pair<>("javascript files", Arrays.asList(
                    "file_script.js"
            )),
            new Pair<>("path", "Documentation/"),
            new Pair<>("index", "index.html"),
            new Pair<>("nav selector", "header>nav>ul"),
            new Pair<>("link target", "content")
    ));

    private final HTML html = new HTML();
    private Section constructors = null;
    private Section methods = null;

    private final String className;
    private final String type;
    private final String fileName;
    private final int aboutIndex;

    public Documentation(String className, String status, String extend, String[] implement) {
        this(className, status, extend, implement, null);
    }

    public Documentation(String className, String status, String extend, String[] implement, String fileName) {
        this.className = className;
        if (fileName == null || fileName.isBlank()) {
            fileName = this.className;
        }
        this.fileName = fileName;
        if (status == null) {
            status = "";
        }
        if (extend == null) {
            extend = "";
        }
        if (implement == null) {
            implement = new String[0];
        }
        if (status.isBlank()) {
            this.type = "null";
        } else {
            this.type = status;
        }
        HTML.Meta viewport = new HTML.Meta();
        viewport.name("viewport");
        viewport.content("width=device-width, initial-scale=1");
        html.head().appendChild(viewport);
        html.head().appendChild(new HTML.Meta("className", className));
        html.head().appendChild(new HTML.Meta("classStatus", status));
        html.head().appendChild(new HTML.Meta("classExtends", extend));
        html.head().appendChild(new HTML.Meta("fileName", fileName));

        StringBuilder imp = new StringBuilder();
        for (int i=0; i<implement.length; i++) {
            String s = implement[i];
            imp.append(s);
            if (i != implement.length-1) {
                imp.append(", ");
            }
        }

        html.head().appendChild(new HTML.Meta().update("classImplements", imp.toString()));

        html.title().html("Arlot Documentation: "+className);
        html.body().appendChild(new Element("h1").html(className));
        if (!status.isBlank() || !extend.isBlank() || !imp.isEmpty()) {
            Element classData = new Element("p");
            classData.addClass("classData");
            if (!status.isBlank()) {
                classData.appendChild(new Element("span").addClass("classStatus").html(Section.formatted(status)));
                classData.appendText(" ");
            }
            if (!extend.isBlank()) {
                classData.appendChild(new Element("span").addClass("classExtends").html(Section.formatted(extend)));
                classData.appendText(" ");
            }
            if (!imp.isEmpty()) {
                classData.appendChild(new Element("span").addClass("classImplements").html(Section.formatted(imp.toString())));
            }
            html.body().appendChild(classData);
        }
        aboutIndex = html.body().childNodes().size();
    }

    public void about(Element about) {
        if (about.hasClass("about")) {
            html.body().insertChildren(aboutIndex, about);
        }
    }

    public void about(String text) {
        about(Section.createAbout(text));
    }

    public void addSection(Section section) {
        html.body().appendChild(section);
        if (section.getType().equals("constructors") && constructors == null) {
            constructors = section;
        } else if (section.getType().equals("methods") && methods == null) {
            methods = section;
        }
    }

    public Section constructor(String name) {
        if (constructors == null) {
            constructors = new Section(html.body(), "Constructors", null, "constructors");
        }
        return constructors.addSection(name, null, "constructor");
    }

    public Section method(String name, String status, String level, String returns) {
        if (methods == null) {
            methods = new Section(html.body(), "Methods", null, "methods");
        }
        return methods.addSection(name, returns, "method", status, level);
    }

    public File toFile(String path, boolean def, boolean addToNav) {
        String sheetPath = options.get("sheetPath").getSingle();
        if (path != null && !path.isBlank()) {
            path = path.replaceAll("\\\\", "/");
            if (path.endsWith("/")) {
                path = path.substring(0, path.length()-1);
            }
            int count = path.split("/").length;
            sheetPath = "../".repeat(count) +
                    options.get("sheetPath").getSingle();
            path = path+"/";
        } else {
            path = "";
        }
        if (!path.toLowerCase().endsWith(".html")) {
            path += fileName+".html";
        }
        if (def) {
            for (String file : options.get("stylesheets")) {
                html.addStyleSheet(sheetPath+file);
            }

            for (String file : options.get("javascript files")) {
                html.addJavaScriptFile(sheetPath+file, true);
            }

            if (addToNav) {
                try {
                    HTML index = new HTML(new File(options.get("path").getSingle()+
                            options.get("index").getSingle()));
                    Element nav = index.body().selectFirst(options.get("nav selector").getSingle());
                    if (nav == null) {
                        throw new NullPointerException();
                    }
                    String np = path.replace(options.get("path").getSingle(), "");
                    int lastSlash = np.lastIndexOf("/");
                    if (lastSlash != -1) {
                        np = np.substring(0, lastSlash);
                    }
                    if (path.equals(fileName + ".html")) {
                        np = "";
                    }
                    Element location = null;
                    Iterator<Element> selected;
                    Element elm = null;
                    if (!np.isBlank()) {
                        String[] navPath = np.split("/");
                        int lev = 0;
                        selected = nav.children().iterator();
                        StringBuilder docPath = new StringBuilder();
                        while (selected.hasNext()) {
                            elm = selected.next();
                            if (Objects.requireNonNull(elm.selectFirst("a"))
                                    .text().equalsIgnoreCase(navPath[lev])) {
                                docPath.append(navPath[lev]).append('/');
                                lev++;
                                if (elm.hasClass("dropdown")) {
                                    selected = Objects.requireNonNull(elm.selectFirst("ul"))
                                            .children().iterator();
                                } else {
                                    break;
                                }
                            }
                            if (lev == navPath.length) {
                                break;
                            }
                        }
                        if (elm != null) {
                            if (lev != navPath.length) {
                                while (lev != navPath.length) {
                                    Element nameCheck = elm.selectFirst("a");
                                    if (nameCheck != null &&
                                            nameCheck.text().equalsIgnoreCase(navPath[lev]) &&
                                            !nameCheck.text().equalsIgnoreCase(navPath[lev-1])) {
                                        lev++;
                                        continue;
                                    }
                                    docPath.append(navPath[lev]).append('/');
                                    String name = navPath[lev].substring(0, 1).toUpperCase() +
                                            navPath[lev].substring(1);
                                    Element alink = new Element("a")
                                            .attr("href",
                                                    docPath + name + ".html")
                                            .attr("target",
                                                    options.get("link target").getSingle())
                                            .attr("data-type", "placeholder")
                                            .html(name);
                                    if (nameCheck == null) {
                                        elm.appendChild(alink);
                                    } else {
                                        if (!elm.hasClass("dropdown")) {
                                            elm.addClass("dropdown");
                                            Element ul = new Element("ul")
                                                    .addClass("dropdown-content");
                                            elm.appendChild(ul);

                                            elm = new Element("li").appendChild(alink);
                                            ul.appendChild(elm);
                                        }
                                    }
                                    lev++;
                                }
                                if (!elm.hasClass("dropdown")) {
                                    elm.addClass("dropdown");
                                    elm.appendChild(new Element("ul")
                                            .addClass("dropdown-content"));
                                }
                            }
                            location = elm.selectFirst("ul");
                        }
                    } else {
                        location = nav;
                    }
                    if (location == null) {
                        throw new NullPointerException();
                    }
                    boolean added = false;
                    if (!location.children().isEmpty()) {
                        selected = location.children().iterator();
                        while (selected.hasNext()) {
                            elm = selected.next();
                            Element alink = elm.selectFirst("a");
                            if (alink==null) continue;
                            if (alink.attr("href").equals(path)) {
                                added = true;
                                alink.html(className);
                                break;
                            } else if (alink.text().equals(className) && alink.attr("data-type").equals("placeholder")) {
                                alink.attr("href", path);
                                alink.attr("data-type", this.type);
                                added = true;
                                break;
                            }
                        }
                    }
                    if (!added) {
                        location.appendChild(new Element("li")
                                .appendChild(new Element("a")
                                        .attr("href", path)
                                        .attr("target",
                                                options.get("link target").getSingle())
                                        .attr("data-type", this.type)
                                        .html(className)));
                    }
                    index.toFile(options.get("path").getSingle() +
                            options.get("index").getSingle());
                } catch (Exception ignored) {}
            }
            if (!path.startsWith(options.get("path").getSingle())) {
                path = options.get("path").getSingle()+path;
            }
        }
        System.out.println(path);
        return html.toFile(path);
    }

    public File toFile(String path, boolean def) {
        return toFile(path, def, false);
    }

    public File toFile(String path) {
        if (path != null && !path.isBlank()) {
            path = path.replaceAll("\\\\", "/");
            if (!path.endsWith("/")) {
                path = path+"/";
            }
        } else {
            path = "";
        }
        if (path.toLowerCase().endsWith(".html")) {
            return html.toFile(path);
        }
        return html.toFile(path+fileName+".html");
    }

    @Override
    public String toString() {
        return html.toString();
    }

    public static Documentation parse(Document doc) {
        String className = null;
        String status = null;
        String extend = null;
        String[] implement = null;
        String fileName = null;
        Elements meta = doc.getElementsByTag("meta");
        for (Element data : meta) {
            if (data.attr("name").equals("className")) {
                className = data.attr("content");
            } else if (data.attr("name").equals("classStatus")) {
                status = data.attr("content");
            } else if (data.attr("name").equals("classExtends")) {
                extend = data.attr("content");
            } else if (data.attr("name").equals("classImplements")) {
                implement = data.attr("content").split(", ");
            } else if (data.attr("name").equals("fileName")) {
                fileName = data.attr("content");
            }
        }
        if (className == null) {
            try{
                className = Objects.requireNonNull(doc.body().getElementsByTag("h1").first()).text();
            } catch (Exception ignored) {}
        }
        if (status == null) {
            try {
                status = Objects.requireNonNull(doc.body().getElementsByClass("classStatus").first()).text();
            } catch (Exception ignored) {}
        }
        if (extend == null) {
            try {
                extend = Objects.requireNonNull(doc.body().getElementsByClass("classExtends").first()).text();
            } catch (Exception ignored) {}
        }
        if (implement == null) {
            try {
                implement = Objects.requireNonNull(doc.body().getElementsByClass("classImplements").first()).text().split(", ");
            } catch (Exception ignored) {}
        }
        if (className == null || status == null || extend == null || implement == null) {
            throw new IllegalArgumentException("The string html must be a previous Documentation Object.");
        }
        Documentation document = new Documentation(className, status, extend, implement, fileName);

        for (Element elm : doc.body().children()) {
            try {
                //System.out.println("< -- Element -- >");
                //System.out.println(elm.outerHtml());
                //System.out.println("< -- Element End -- >");
                document.addSection(Section.parse(null, elm));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        Element about = doc.getElementsByClass("about").first();
        if (about != null) {
            document.about(about);
        }

        return document;
    }

    public static Documentation parse(String html) {
        return parse(Jsoup.parse(html));
    }

    public static Documentation parse(File htmlFile) {
        if (!htmlFile.exists() || !htmlFile.isFile() ||
                !htmlFile.getName().toLowerCase().endsWith(".html")) {
            return null;
        }
        try {
            return parse(Jsoup.parse(htmlFile));
        } catch (IOException e) {
            try(BufferedReader br = new BufferedReader(new FileReader(htmlFile))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return parse(sb.toString());
            } catch (IOException ignored) {}
        }
        return null;
    }
}
