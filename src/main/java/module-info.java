module com.example.muicapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires jaudiotagger; // JAudiotagger library

    requires com.azure.storage.blob;
    requires reactor.netty.core;
    requires org.apache.tika.core;
    requires java.prefs;


    // Open packages that JavaFX needs to access via reflection
    opens com.example.musicapp to javafx.fxml;
    opens datab to javafx.fxml;
    opens login to javafx.fxml;
    opens model to javafx.fxml;

    // Export packages as needed
    exports com.example.musicapp;
    exports datab;
    exports login;
    exports model;
}

