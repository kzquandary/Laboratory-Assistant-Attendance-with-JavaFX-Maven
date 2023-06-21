package com.aslabapp.aslabapp;

import com.swardana.materialiconfx.control.MaterialIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
public class HomeGame implements Initializable {
    @FXML
    public AnchorPane content;
    @FXML
    private MaterialIcon Volume_MUTE;
    @FXML
    private MaterialIcon Volume_UP;
    private static final int BUTTON_WIDTH = 170;
    private static final int BUTTON_HEIGHT = 54;
    private static final int BUTTON_MARGIN = 20;
    private static final int BUTTONS_PER_ROW = 4;
    private static final int BUTTON_VERTICAL_OFFSET = 100;
    private int rowCount = 0;
    private int columnCount = 0;
    public static String level_pilihan;
    public static List<String> kunciJawaban;
    public static List<Character> hurufAcak;
    public Button Home;
    private static MediaPlayer backgroundMediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLevelButtons();
        Home.setOnAction(e -> {
            level_pilihan = null;
            kunciJawaban = null;
            hurufAcak = null;
            Stage gameStage = (Stage) Home.getScene().getWindow();
            gameStage.close();
            try {
                PlayMusic(false);
                Main.homepage();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Volume_UP.setOnMouseClicked(mouseEvent -> {
            Volume_UP.setVisible(false);
            Volume_MUTE.setVisible(true);
            PlayMusic(false);
        });
        Volume_MUTE.setOnMouseClicked(mouseEvent -> {
            Volume_MUTE.setVisible(false);
            Volume_UP.setVisible(true);
            PlayMusic(true);
        });
    }

    private void loadLevelButtons() {
        try (InputStream inputStream = Main.class.getResourceAsStream("data.txt")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                StringBuilder levelInfo = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Level")) {
                        if (levelInfo.length() > 0) {
                            createLevelButton(levelInfo.toString());
                            levelInfo.setLength(0);
                        }
                    }
                    levelInfo.append(line).append("\n");
                }
                if (levelInfo.length() > 0) {
                    createLevelButton(levelInfo.toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + e.getMessage());
        }
    }


    private void createLevelButton(String levelData) {
        String[] lines = levelData.split("\n");
        if (lines.length >= 4) {
            String levelName = lines[0].trim().replace("Level ", "");
            String hurufAcakStr = lines[1].replace("Huruf Acak: ", "").trim();
            String jawabanStr = lines[2].replace("Jawaban: ", "").trim();
            boolean isCompleted = Boolean.parseBoolean(lines[3].replace("Terselesaikan: ", "").trim());

            Button button = new Button();
            if (isCompleted) {
                button.setStyle("-fx-background-color: green;");
            }
            button.setText("Level " + levelName);
            button.setPrefWidth(BUTTON_WIDTH);
            button.setPrefHeight(BUTTON_HEIGHT);
            button.setLayoutX(getButtonX());
            button.setLayoutY(getButtonY());
            button.setOnAction(event -> {
                ButtonClicked();
                level_pilihan = levelName;
                kunciJawaban = Arrays.asList(jawabanStr.split(", "));
                hurufAcak = new ArrayList<>();
                for (char c : hurufAcakStr.toCharArray()) {
                    hurufAcak.add(c);
                }
                try {
                    GamePage(level_pilihan);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            content.getChildren().add(button);

            columnCount++;
            if (columnCount >= BUTTONS_PER_ROW) {
                rowCount++;
                columnCount = 0;
            }
        }
    }

    private double getButtonX() {
        return BUTTON_MARGIN + (BUTTON_WIDTH + BUTTON_MARGIN) * columnCount;
    }

    private double getButtonY() {
        return BUTTON_MARGIN + (BUTTON_HEIGHT + BUTTON_MARGIN) * rowCount + BUTTON_VERTICAL_OFFSET;
    }

    private void GamePage(String level) throws IOException {
        Stage HomeGame = (Stage) content.getScene().getWindow();
        HomeGame.close();

        Stage gameStage = new Stage();
        FXMLLoader gameloader = new FXMLLoader(getClass().getResource("GameKata.fxml"));
        AnchorPane gameroot = gameloader.load();
        Scene gamescene = new Scene(gameroot);
        gameStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        gameStage.setTitle("Game Kata Level " + level);
        gameStage.setResizable(false);
        gameStage.setScene(gamescene);
        gameStage.setScene(gamescene);
        gameStage.show();
    }
    public static void PlayMusic(Boolean status) {
        if (status) {
            Media media = new Media(Objects.requireNonNull(Main.class.getResource("BackgroundMusic.mp3")).toExternalForm());
            backgroundMediaPlayer = new MediaPlayer(media);
            backgroundMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMediaPlayer.setVolume(60.0);
            backgroundMediaPlayer.play();
        } else {
            if (backgroundMediaPlayer != null) {
                backgroundMediaPlayer.stop();
            }
        }
    }
    public static void ButtonClicked(){
        Media media = new Media(Objects.requireNonNull(Main.class.getResource("ButtonClick.mp3")).toExternalForm());
        MediaPlayer buttonMediaPlayer = new MediaPlayer(media);
        buttonMediaPlayer.play();
    }
    public static void JawabanBenar(){
        Media media = new Media(Objects.requireNonNull(Main.class.getResource("JawabanBenar.wav")).toExternalForm());
        MediaPlayer benarMediaPlayer = new MediaPlayer(media);
        benarMediaPlayer.play();
    }

    public static void LevelComplete() {
        Media media = new Media(Objects.requireNonNull(Main.class.getResource("LevelWin.mp3")).toExternalForm());
        MediaPlayer winMediaPlayer = new MediaPlayer(media);
        winMediaPlayer.setVolume(100.0);
        winMediaPlayer.play();
    }
}
