package com.aslabapp.aslabapp;

import Controller.InfoHomeController;
import Controller.SearchController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.Action;
import project.Route;
import project.StringVariable;
import project.VarTemp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Main extends Application {
    public Pane content;
    public Text jmlmahasiswa;
    public Text jmlpertemuan;
    public TextField search_form;
    public Text jmllaporan;
    @FXML
    private PasswordField fieldpassword;
    @FXML
    private TextField fieldusername;
    @FXML
    private CheckBox rememberme;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Path file = Paths.get(VarTemp.filePath);
        if (Files.exists(file)) {
            try {
                String token = Files.readString(file);

                boolean isValid = Action.validateToken(token);

                if (isValid) {
                    homepage();
                } else {
                    loginpage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
           loginpage();
        }
    }

    @FXML
    private void login() throws IOException {
        String username = fieldusername.getText();
        String password = fieldpassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
           Action.alerterror("Username dan Password harus diisi");
            return;
        }

        if (rememberme.isSelected()) {
            String token = Action.loginWithToken(username, password);

            if (token != null) {
                Stage loginStage = (Stage) fieldusername.getScene().getWindow();
                loginStage.close();
                homepage();
                Action.alertinfo("Berhasil Login");
            } else {
                Action.alerterror("User dan Password salah");
            }
        } else {
            boolean loginResult = Action.loginWithCredentials(username, password);

            if (loginResult) {
                VarTemp.username = username;
                VarTemp.password = password;

                Stage loginStage = (Stage) fieldusername.getScene().getWindow();
                loginStage.close();
                homepage();
                Action.alertinfo("Berhasil Login");
            } else {
                Action.alerterror("User dan Password salah");
            }
        }
    }
    public static void homepage() throws IOException {
        Stage homeStage = new Stage();
        FXMLLoader homeLoader = new FXMLLoader(Main.class.getResource(Route.Home));
        AnchorPane homeRoot = homeLoader.load();
        Scene homeScene = new Scene(homeRoot);
        homeStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        homeStage.initStyle(StageStyle.TRANSPARENT);
        homeScene.setFill(Color.TRANSPARENT);
        homeStage.setTitle("Asisten Laboratorium");
        Action act = new Action();
        homeRoot.setOnMouseDragged(act::handleMouseDragged);
        homeRoot.setOnMousePressed(act::handleMousePressed);
        InfoHomeController ihc = new InfoHomeController();
        ihc.initTextFields(homeLoader);
        homeStage.setScene(homeScene);
        homeStage.show();
    }
    public void loginpage() throws IOException {
        Stage loginStage = new Stage();
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource(Route.Login));
        AnchorPane loginRoot = loginLoader.load();
        Scene loginScene = new Scene(loginRoot);
        loginScene.setFill(Color.TRANSPARENT);
        loginStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        loginStage.initStyle(StageStyle.TRANSPARENT);
        loginStage.setTitle("Login");
        loginStage.setScene(loginScene);
        Action act = new Action();
        loginRoot.setOnMouseDragged(act::handleMouseDragged);
        loginRoot.setOnMousePressed(act::handleMousePressed);
        loginStage.setScene(loginScene);
        loginStage.show();
    }
    public void lockpage() throws IOException {
        Stage lockStage = new Stage();
        FXMLLoader lockLoader = new FXMLLoader(getClass().getResource(Route.Lock));
        AnchorPane lockroot = lockLoader.load();
        Scene lockScene = new Scene(lockroot);
        lockScene.setFill(Color.TRANSPARENT);
        lockStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        lockStage.initStyle(StageStyle.TRANSPARENT);
        lockStage.setTitle("Login");
        lockStage.setScene(lockScene);
        Action act = new Action();
        lockroot.setOnMouseDragged(act::handleMouseDragged);
        lockroot.setOnMousePressed(act::handleMousePressed);
        lockStage.setScene(lockScene);
        lockStage.show();
    }
    public void logout() throws IOException {
        boolean apiActive = Action.checkAPIStatus();
        if (apiActive) {
            Stage homeStage = (Stage) content.getScene().getWindow();
            homeStage.close();

            Action.deleteTokenFile();
            Action.logoutFromAPI(VarTemp.username);
        } else {
            Action.alerterror(StringVariable.ApiError);
            return;
        }

        loginpage();
        Action.alertinfo("Berhasil logout");
    }

    public void lock() throws IOException {
        Stage homeStage = (Stage) content.getScene().getWindow();
        homeStage.close();
        lockpage();
        Action.alertinfo("Akun telah dikunci");
    }

    public void mainClose(MouseEvent mouseEvent) {
        Action action = new Action();
        action.handleExit(mouseEvent);
    }

    public void mainMinimize(MouseEvent mouseEvent) {
        Action action = new Action();
        action.handleMinimize(mouseEvent);
    }


    public static void main(String[] args) {
        launch();
    }

    public void home() {
        content.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.InfoHome));
        Action.Move(loader, content);
    }

    public void mahasiswa() throws IOException {
        content.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Mahasiswa));
        Action.Move(loader, content);
    }

    public void pertemuan() throws IOException {
        content.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Pertemuan));
        Action.Move(loader, content);
    }

    public void absensi() {
        content.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Absensi));
        Action.Move(loader, content);
    }

    public void laporan() throws IOException {
        content.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Laporan));
        Action.Move(loader, content);
    }

    public void nilai() {
        content.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Nilai));
        Action.Move(loader, content);
    }

    public void keaktifan() {
        content.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Keaktifan));
        Action.Move(loader, content);
    }

    public void setting() {
        content.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Setting));
        Action.Move(loader, content);
    }

    public void carimahasiswa() {
        if (!search_form.getText().isEmpty()) {
            String searchText = search_form.getText();
            if (searchText.matches(Route.RegNama)) {

                content.getChildren().clear();

                ImageView loadingImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Loading.gif"))));
                Action.LoadingAnimation(content, loadingImage);

                Task<Void> searchTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(1000);
                        Platform.runLater(() -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Search));
                                Parent searchRoot = loader.load();
                                SearchController searchController = loader.getController();
                                searchController.setSearch(searchText);
                                content.getChildren().clear();
                                content.getChildren().add(searchRoot);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        return null;
                    }
                };

                new Thread(searchTask).start();
            } else {
                Action.alerterror(StringVariable.FormatError);
            }
        } else {
            Action.alerterror(StringVariable.EmptyForm);
        }
    }

}