package com.aslabapp.aslabapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import Project.Action;
import Project.TempVariable;

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
        lockusername.setText(TempVariable.username);
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
            Action.alerterror("Masukan Password");
            return;
        }
        if (username.equals(TempVariable.username) && password.equals(TempVariable.password)) {
            Stage lockstage = (Stage) lockusername.getScene().getWindow();
            lockstage.close();
            Main.homepage();
            Action.toastinfo("Berhasil Unlock");
        } else {
            Action.toasterror("Password salah");
        }
    }

}
