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
import javafx.scene.control.Alert;
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
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Login));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Asisten Laboratorium");
        Action act = new Action();
        root.setOnMouseDragged(act::handleMouseDragged);
        root.setOnMousePressed(act::handleMousePressed);
        primaryStage.setScene(scene);

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

                primaryStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            primaryStage.show();
        }
    }

    @FXML
    private void login() throws IOException {
        String username = fieldusername.getText();
        String password = fieldpassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Username dan Password harus diisi");
            alert.showAndWait();
            return;
        }

        if (rememberme.isSelected()) {
            String token = Action.loginWithToken(username, password);

            if (token != null) {
                Stage loginStage = (Stage) fieldusername.getScene().getWindow();
                loginStage.close();
                homepage();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info");
                alert.setHeaderText(null);
                alert.setContentText("Berhasil Login");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User dan Password salah");
                alert.showAndWait();
            }
        } else {
            boolean loginResult = Action.loginWithCredentials(username, password);

            if (loginResult) {
                VarTemp.username = username;
                VarTemp.password = password;

                Stage loginStage = (Stage) fieldusername.getScene().getWindow();
                loginStage.close();
                homepage();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info");
                alert.setHeaderText(null);
                alert.setContentText("Berhasil Login");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User dan Password salah");
                alert.showAndWait();
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
        Stage homeStage = (Stage) content.getScene().getWindow();
        homeStage.close();
        Action.deleteTokenFile();
        Action.logoutFromAPI(VarTemp.username);
        loginpage();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText("Berhasil logout");
        alert.showAndWait();
    }




    public void lock() throws IOException {
        Stage homeStage = (Stage) content.getScene().getWindow();
        homeStage.close();
        lockpage();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText("Akun telah dikunci");
        alert.showAndWait();
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
                        Thread.sleep(1000); // Simulasi pencarian selama 1 detik
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Input tidak valid. Harap masukkan input yang sesuai dengan format.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Silahkan isi form terlebih dahulu");
            alert.showAndWait();
        }
    }

}