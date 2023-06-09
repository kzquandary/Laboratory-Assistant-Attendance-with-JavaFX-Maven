package com.aslabapp.aslabapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import project.Action;
import project.VarTemp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Lock implements Initializable {
    @FXML
    private PasswordField lockpassword;

    @FXML
    private TextField lockusername;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lockusername.setText(VarTemp.username);
    }
    public void mainClose(MouseEvent mouseEvent) {
        Action action = new Action();
        action.handleExit(mouseEvent);
    }

    public void mainMinimize(MouseEvent mouseEvent) {
        Action action = new Action();
        action.handleMinimize(mouseEvent);
    }

    public void unlock() throws IOException {
        String username = lockusername.getText();
        String password = lockpassword.getText();
        if (password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Masukan Password");
            alert.showAndWait();
            return;
        }
        if (username.equals(VarTemp.username) && password.equals(VarTemp.password)) {
            Stage lockstage = (Stage) lockusername.getScene().getWindow();
            lockstage.close();
            Main.homepage();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Berhasil Unlock");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Password salah");
            alert.showAndWait();
        }
    }

}
