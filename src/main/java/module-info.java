module com.aslabapp.aslabapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires commons.math3;
    requires SparseBitSet;
    requires com.swardana.materialiconfx;
    requires java.net.http;
    requires java.desktop;


    opens com.aslabapp.aslabapp to javafx.fxml;
    exports com.aslabapp.aslabapp;
    opens Controller to javafx.fxml;
    exports Controller;
    opens project to javafx.fxml;
    exports project;
    opens Model to javafx.base;
    exports Model;
}