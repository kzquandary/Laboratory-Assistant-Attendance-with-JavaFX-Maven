package com.aslabapp.aslabapp;

import Controller.InfoHomeController;
import Controller.SearchController;
import Project.Action;
import Project.Route;
import Project.StringVariable;
import Project.TempVariable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    public Rectangle Background_Rect;
    public Rectangle Sidebar_Rect;
    public Rectangle Header_Rect;
    public Pane Tab_Tema;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Path file = Paths.get(TempVariable.filePath);
        if (Files.exists(file)) {
            try {
                String token = Files.readString(file);

                boolean isValid = Action.validateToken(token);

                if (isValid) {
                    homepage();
                    Action.toastinfo("Berhasil Login dengan Akses Token");
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
                Action.toastinfo("Berhasil Login");
            } else {
                Action.alerterror("User dan Password salah");
            }
        } else {
            boolean loginResult = Action.loginWithCredentials(username, password);

            if (loginResult) {
                TempVariable.username = username;
                TempVariable.password = password;

                Stage loginStage = (Stage) fieldusername.getScene().getWindow();
                loginStage.close();
                homepage();
                Action.toastinfo("Berhasil Login");
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
            boolean konfirmasi = Action.alertkonfir("Apakah Anda yakin ingin logout?");

            if (konfirmasi) {
                Stage homeStage = (Stage) content.getScene().getWindow();
                homeStage.close();

                Action.deleteTokenFile();
                Action.logoutFromAPI(TempVariable.username);

                loginpage();
                Action.toastinfo("Berhasil logout");
            }
        } else {
            Action.alerterror(StringVariable.ApiError);
        }
    }

    public void lock() throws IOException {
        boolean konfirmasi = Action.alertkonfir("Apakah Anda yakin ingin mengunci akun?");

        if (konfirmasi) {
            Stage homeStage = (Stage) content.getScene().getWindow();
            homeStage.close();
            lockpage();
            Action.toastinfo("Akun telah dikunci");
        }
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

    public void carimahasiswa() throws IOException {
        if (!search_form.getText().isEmpty()) {
            String searchText = search_form.getText();
            if (searchText.equalsIgnoreCase("Game")) {
                Stage homeStage = (Stage) content.getScene().getWindow();
                homeStage.close();
                HomeGame.PlayMusic(true);
                GameHome();
            }
            if (searchText.matches(StringVariable.RegNama)) {

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

    public static void GameHome() throws IOException {
        Stage gameStage = new Stage();
        FXMLLoader gameloader = new FXMLLoader(Main.class.getResource("HomeGame.fxml"));
        ScrollPane gameroot = gameloader.load();
        Scene gamescene = new Scene(gameroot);
        gameStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        gameStage.setTitle("Game Kata");
        gameStage.setResizable(false);
        gameStage.setScene(gamescene);
        gameStage.setScene(gamescene);
        gameStage.show();
    }

    public void Buka_Tema() {
        Tab_Tema.setVisible(!Tab_Tema.isVisible());
    }

    public void CloseTema() {
        Tab_Tema.setVisible(false);
    }

    @FXML
    void BG_BIRU() {
        Background_Rect.setFill(Paint.valueOf("#161d99"));
    }

    @FXML
    void BG_Cyan() {
        Background_Rect.setFill(Paint.valueOf("#3288c9"));
    }

    @FXML
    void BG_HIJAU() {
        Background_Rect.setFill(Paint.valueOf("#28a100"));
    }

    @FXML
    void BG_KUNING() {
        Background_Rect.setFill(Paint.valueOf("#bff20a"));
    }

    @FXML
    void BG_LIME() {
        Background_Rect.setFill(Paint.valueOf("#32c768"));
    }

    @FXML
    void BG_MERAH() {
        Background_Rect.setFill(Paint.valueOf("#c73256"));
    }

    @FXML
    void BG_ORANGE() {
        Background_Rect.setFill(Paint.valueOf("#c76b32"));
    }

    @FXML
    void BG_UNGU() {
        Background_Rect.setFill(Paint.valueOf("#380257"));
    }

    @FXML
    void BGLG_1() {
        Background_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0a226b 0.6729%, #b06ab3 51.0094%, #3288c9 100.0%)"));
    }

    @FXML
    void BGLG_2() {
        Background_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #7eb26b 17.6312%, #7eb26b 29.6576%, #0a226b 66.891%, #32c7c1 100.0%)"));
    }

    @FXML
    void BGLG_3() {
        Background_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0c6b0b 0.6729%, #736bb2 51.0094%, #aac732 100.0%)"));
    }

    @FXML
    void BGLG_4() {
        Background_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #6b0b5e 0.6729%, #abb06a 31.7654%, #806ab0 68.2369%, #c74b32 100.0%)"));
    }

    @FXML
    void BGLG_5() {
        Background_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0b206b 0.6729%, #601963 51.0094%, #1807b1 100.0%)"));
    }

    @FXML
    void BGLG_6() {
        Background_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0a226b 0.6729%, #5e2c61 51.0094%, #b18b0d 100.0%)"));
    }

    @FXML
    void BGLG_7() {
        Background_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #001451 0.6729%, #7b0f81 51.0094%, #840724 100.0%)"));
    }

    @FXML
    void BGLG_8() {
        Background_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #3e6b0b 0.6729%, #b0b26b 51.0094%, #32c741 100.0%)"));
    }

    @FXML
    void SB_BIRU() {
        Sidebar_Rect.setFill(Paint.valueOf("#161d99"));
    }

    @FXML
    void SB_Cyan() {
        Sidebar_Rect.setFill(Paint.valueOf("#3289c74e"));
    }

    @FXML
    void SB_HIJAU() {
        Sidebar_Rect.setFill(Paint.valueOf("#28a100"));
    }

    @FXML
    void SB_KUNING() {
        Sidebar_Rect.setFill(Paint.valueOf("#bef00c99"));
    }

    @FXML
    void SB_LIME() {
        Sidebar_Rect.setFill(Paint.valueOf("#34c767b3"));
    }

    @FXML
    void SB_MERAH() {
        Sidebar_Rect.setFill(Paint.valueOf("#c73256"));
    }

    @FXML
    void SB_ORANGE() {
        Sidebar_Rect.setFill(Paint.valueOf("#c76b32"));
    }

    @FXML
    void SB_UNGU() {
        Sidebar_Rect.setFill(Paint.valueOf("#380257b2"));
    }

    @FXML
    void SBLG_1() {
        Sidebar_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0a226b 0.6729%, #b06ab3 51.0094%, #3288c9 100.0%)"));
    }

    @FXML
    void SBLG_2() {
        Sidebar_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #7eb26b 17.6312%, #7eb26b 29.6576%, #0a226b 66.891%, #32c7c1 100.0%)"));
    }

    @FXML
    void SBLG_3() {
        Sidebar_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0c6b0b 0.6729%, #736bb2 51.0094%, #aac732 100.0%)"));
    }

    @FXML
    void SBLG_4() {
        Sidebar_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #6b0b5e 0.6729%, #abb06a 31.7654%, #806ab0 68.2369%, #c74b32 100.0%)"));
    }

    @FXML
    void SBLG_5() {
        Sidebar_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0b206b 0.6729%, #601963 51.0094%, #1807b1 100.0%)"));
    }

    @FXML
    void SBLG_6() {
        Sidebar_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0a226b 0.6729%, #5e2c61 51.0094%, #b18b0d 100.0%)"));
    }

    @FXML
    void SBLG_7() {
        Sidebar_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #001451 0.6729%, #7b0f81 51.0094%, #840724 100.0%)"));
    }

    @FXML
    void SBLG_8() {
        Sidebar_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #3e6b0b 0.6729%, #b0b26b 51.0094%, #32c741 100.0%)"));
    }

    @FXML
    void HD_BIRU() {
        Header_Rect.setFill(Paint.valueOf("#161d99"));
    }

    @FXML
    void HD_Cyan() {
        Header_Rect.setFill(Paint.valueOf("#3288c9"));
    }

    @FXML
    void HD_HIJAU() {
        Header_Rect.setFill(Paint.valueOf("#28a100"));
    }

    @FXML
    void HD_KUNING() {
        Header_Rect.setFill(Paint.valueOf("#bef00c99"));
    }

    @FXML
    void HD_LIME() {
        Header_Rect.setFill(Paint.valueOf("#32c768"));
    }

    @FXML
    void HD_MERAH() {
        Header_Rect.setFill(Paint.valueOf("#c73256"));
    }

    @FXML
    void HD_ORANGE() {
        Header_Rect.setFill(Paint.valueOf("#c76b32"));
    }

    @FXML
    void HD_UNGU() {
        Header_Rect.setFill(Paint.valueOf("#380257b2"));
    }

    @FXML
    void HDLG_1() {
        Header_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0a226b 0.6729%, #b06ab3 51.0094%, #3288c9 100.0%)"));
    }

    @FXML
    void HDLG_2() {
        Header_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #7eb26b 17.6312%, #7eb26b 29.6576%, #0a226b 66.891%, #32c7c1 100.0%)"));
    }

    @FXML
    void HDLG_3() {
        Header_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0c6b0b 0.6729%, #736bb2 51.0094%, #aac732 100.0%)"));
    }

    @FXML
    void HDLG_4() {
        Header_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #6b0b5e 0.6729%, #abb06a 31.7654%, #806ab0 68.2369%, #c74b32 100.0%)"));
    }

    @FXML
    void HDLG_5() {
        Header_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0b206b 0.6729%, #601963 51.0094%, #1807b1 100.0%)"));
    }

    @FXML
    void HDLG_6() {
        Header_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #0a226b 0.6729%, #5e2c61 51.0094%, #b18b0d 100.0%)"));
    }

    @FXML
    void HDLG_7() {
        Header_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #001451 0.6729%, #7b0f81 51.0094%, #840724 100.0%)"));
    }

    @FXML
    void HDLG_8() {
        Header_Rect.setFill(Paint.valueOf("linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, #223d96 0.0%, #3e6b0b 0.6729%, #b0b26b 51.0094%, #32c741 100.0%)"));
    }


}