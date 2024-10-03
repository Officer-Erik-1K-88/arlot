module arlot {
    exports arlot;
    exports arlot.user;
    exports arlot.user.display;
    exports arlot.user.display.controllers;
    exports arlot.math;
    exports arlot.protect;
    exports arlot.roleplay;
    exports arlot.roleplay.info;
    exports arlot.data;
    exports arlot.data.file;
    exports arlot.data.tag;
    exports arlot.data.collect;
    exports arlot.error;
    exports arlot.time;

    requires java.management;
    requires java.desktop;
    requires org.json;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.jsoup;
    requires org.jspecify;
    //requires java.compiler;
    //requires org.objectweb.asm;

    opens arlot.user.display to javafx.fxml;
    opens arlot.user.display.controllers to javafx.fxml;
    opens arlot to javafx.fxml;
    opens arlot.user to javafx.fxml;
    exports arlot.data.file.doc;
}