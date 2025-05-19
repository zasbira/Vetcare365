module org.vetjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens org.vetjavafx to javafx.fxml;
    exports org.vetjavafx;
    exports org.vetjavafx.Controller;
    opens org.vetjavafx.Controller to javafx.fxml;
}

