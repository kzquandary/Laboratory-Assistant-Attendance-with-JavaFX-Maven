package com.aslabapp.aslabapp;

import controller.SearchController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.Action;
import project.Route;
import controller.InfoHomeController;
import java.io.IOException;
import java.util.Objects;
public class Main extends Application {

    public Pane content;
    public Text jmlmahasiswa;
    public Text jmlpertemuan;
    public TextField search_form;
    public Text jmllaporan;
    @FXML
    public static Rectangle background;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Route.Home));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Asisten Laboratorium");
        Action act = new Action();
        root.setOnMouseDragged(act::handleMouseDragged);
        root.setOnMousePressed(act::handleMousePressed);
        InfoHomeController ihc = new InfoHomeController();
        ihc.initTextFields(loader);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void changebg(LinearGradient color) {
        background.setFill(color);
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
    public void search_form() {
        String searchText = search_form.getText();
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
    }



}