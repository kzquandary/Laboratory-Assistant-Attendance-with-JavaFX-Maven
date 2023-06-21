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
    requires org.controlsfx.controls;
    requires javafx.media;


    opens com.aslabapp.aslabapp to javafx.fxml;
    exports com.aslabapp.aslabapp;
    opens Controller to javafx.fxml;
    exports Controller;
    opens Project to javafx.fxml;
    exports Project;
    opens Model to javafx.base;
    exports Model;
}