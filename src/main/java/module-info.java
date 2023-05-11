module com.aslabapp.aslabapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.aslabapp.aslabapp to javafx.fxml;
    exports com.aslabapp.aslabapp;
    opens controller to javafx.fxml;
    exports controller;
    opens project to javafx.fxml;
    exports project;
    opens model to javafx.base;
    exports model;
}