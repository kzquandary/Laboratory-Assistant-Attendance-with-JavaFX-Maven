package com.aslabapp.aslabapp;

import com.swardana.materialiconfx.control.MaterialIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

public class GameKata implements Initializable {
    @FXML
    private MaterialIcon Volume_MUTE;
    @FXML
    private MaterialIcon Volume_UP;
    @FXML
    private Button Button_Nomor_1;
    @FXML
    private Button Button_Nomor_2;
    @FXML
    private Button Button_Nomor_3;
    @FXML
    private Button Button_Nomor_4;
    @FXML
    private Button Button_Nomor_5;
    @FXML
    private Button Button_Nomor_6;
    @FXML
    private Button Button_Nomor_7;
    @FXML
    private Button Button_Nomor_8;
    @FXML
    private Button Button_Nomor_9;
    @FXML
    private Button Button_Nomor_10;
    @FXML
    private Button Button_Nomor_11;
    @FXML
    private Button Button_Nomor_12;
    @FXML
    private Button Button_Nomor_13;
    @FXML
    private Button Button_Nomor_14;
    @FXML
    private Button Button_Nomor_15;
    @FXML
    private Button Button_Nomor_16;
    @FXML
    private TextField Field_Kata;
    @FXML
    private Button Button_Hapus;
    @FXML
    private Button Button_New_Game;
    @FXML
    private Pane Panel_Jawaban;

    private Map<Button, Boolean> buttonStatus;
    private Set<Button> tombolDiklikSebelumnya;
    private List<Button> buttons;
    //    private List<String> kunciJawaban;
//    private List<Character> hurufAcak;
    private Button buttonTerakhirKlik;
    private Integer jumlahKlik = 0;
    @FXML
    private Button Button_Kembali_Ke_Home;

    @FXML
    private Button Button_Kembali_Ke_level;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tombolDiklikSebelumnya = new HashSet<>();
        buttonStatus = new HashMap<>();

        buttons = Arrays.asList(
                Button_Nomor_1, Button_Nomor_2, Button_Nomor_3, Button_Nomor_4,
                Button_Nomor_5, Button_Nomor_6, Button_Nomor_7, Button_Nomor_8,
                Button_Nomor_9, Button_Nomor_10, Button_Nomor_11, Button_Nomor_12,
                Button_Nomor_13, Button_Nomor_14, Button_Nomor_15, Button_Nomor_16
        );

//        kunciJawaban = Arrays.asList(
//                "TISU", "APEL", "OJEK", "SAPU", "PELUIT",
//                "SULIT", "TULI", "SUIT", "ISU", "REM"
//        );
//
//        hurufAcak = new ArrayList<>(Arrays.asList(
//                'G', 'A', 'R', 'T', 'P', 'Q', 'O', 'J', 'L', 'N',
//                'K', 'U', 'S', 'I', 'E', 'M'
//        ));
        Collections.shuffle(HomeGame.hurufAcak);

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            button.setText(String.valueOf(HomeGame.hurufAcak.get(i)));
            buttonStatus.put(button, true);
            button.setFocusTraversable(false);
            button.setOnAction(event -> {
                HomeGame.ButtonClicked();
                if (buttonStatus.get(button)) {
                    if (buttonTerakhirKlik != null && buttonTerakhirKlik != button) {
                        buttonTerakhirKlik.setDisable(true);
                        buttonStatus.put(buttonTerakhirKlik, true);
                    }
                    buttonTerakhirKlik = button;
                    Field_Kata.setText(Field_Kata.getText() + button.getText());
                    button.setDisable(true);
                    buttonStatus.put(button, false);
                    jumlahKlik++;
                    checkJawaban();
                }
            });
        }

        Button_Hapus.setOnAction(e -> handleHapus());
        Button_New_Game.setOnAction(e -> newGame());
        Button_Kembali_Ke_Home.setOnAction(e -> {
            Stage gameStage = (Stage) Panel_Jawaban.getScene().getWindow();
            gameStage.close();
            HomeGame.PlayMusic(false);
            try {
                Main.homepage();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Button_Kembali_Ke_Home.setOnAction(e -> {
            Stage gameStage = (Stage) Panel_Jawaban.getScene().getWindow();
            HomeGame.PlayMusic(false);
            gameStage.close();
            try {
                Main.homepage();
            } catch (IOException ex) {

                throw new RuntimeException(ex);
            }
        });
        Button_Kembali_Ke_level.setOnAction(e -> {
            HomeGame.level_pilihan = null;
            Stage gameStage = (Stage) Panel_Jawaban.getScene().getWindow();
            gameStage.close();
            try {
                Main.GameHome();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Volume_UP.setOnMouseClicked(mouseEvent -> {
            Volume_UP.setVisible(false);
            Volume_MUTE.setVisible(true);
            HomeGame.PlayMusic(false);
        });
        Volume_MUTE.setOnMouseClicked(mouseEvent -> {
            Volume_MUTE.setVisible(false);
            Volume_UP.setVisible(true);
            HomeGame.PlayMusic(true);
        });
    }

    private void handleHapus() {
        String currentText = Field_Kata.getText();
        int currentLength = currentText.length();

        if (currentLength > 0) {
            String deletedCharacter = currentText.substring(currentLength - 1);
            Field_Kata.setText(currentText.substring(0, currentLength - 1));
            if (deletedCharacter.length() == 1 && jumlahKlik > 0) {
                jumlahKlik--;
                if (buttonTerakhirKlik != null) {
                    buttonTerakhirKlik.setDisable(false);
                    buttonStatus.put(buttonTerakhirKlik, true);
                    tombolDiklikSebelumnya.remove(buttonTerakhirKlik);
                    buttonTerakhirKlik = null;
                }
                Iterator<Button> iterator = tombolDiklikSebelumnya.iterator();
                while (iterator.hasNext()) {
                    Button button = iterator.next();
                    button.setDisable(false);
                    buttonStatus.put(button, true);
                    iterator.remove();
                }
            }
            for (Button button : buttons) {
                if (button.getText().equals(deletedCharacter) && button.isDisable()) {
                    button.setDisable(false);
                    buttonStatus.put(button, true);
                    break;
                }
            }
        }
    }

    private void checkJawaban() {
        String jawaban = Field_Kata.getText().toUpperCase();

        // Cek apakah jawaban sudah ada dalam Panel_Jawaban
        boolean jawabanSudahDijawab = false;
        for (Node node : Panel_Jawaban.getChildren()) {
            if (node instanceof Text text) {
                if (jawaban.equals(text.getText())) {
                    jawabanSudahDijawab = true;
                    break;
                }
            }
        }

        // Cek apakah jawaban termasuk dalam kunci jawaban
        boolean jawabanBenar = false;
        if (!jawabanSudahDijawab) {
            for (String kunci : HomeGame.kunciJawaban) {
                if (jawaban.equals(kunci)) {
                    jawabanBenar = true;
                    break;
                }
            }
        }

        if (jawabanBenar) {
            Text text = new Text(jawaban);
            text.setLayoutX(14.0);
            text.setLayoutY(65.0 + Panel_Jawaban.getChildren().size() * 30);
            text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
            text.setFill(Color.BLACK);
            HomeGame.JawabanBenar();
            Panel_Jawaban.getChildren().add(text);
            resetTextFieldAndButtons();
            if (Panel_Jawaban.getChildren().size() == 10) {
                updateTerselesaikan("Level " + HomeGame.level_pilihan);
                HomeGame.LevelComplete();
                showCongratulationsAlert();
            }
        } else if (jawabanSudahDijawab) {
            System.out.println("Jawaban Sudah Dijawab Sebelumnya");
            resetTextFieldAndButtons();
        }
    }

    private void showCongratulationsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Selamat!");
        alert.setHeaderText(null);
        alert.setContentText("Anda berhasil menyelesaikan permainan!");
        alert.showAndWait();
    }

    public static void updateTerselesaikan(String level) {
        String fileName = "data.txt";
        try {
            // Membaca file dari resource menggunakan InputStream
            InputStream inputStream = Main.class.getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            StringBuilder content = new StringBuilder();

            String line;
            boolean foundLevel = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Level " + level)) {
                    foundLevel = true;
                }

                // Ubah status terselesaikan jika level ditemukan
                if (foundLevel && line.trim().startsWith("Terselesaikan:")) {
                    line = line.replace("false", "true");
                    foundLevel = false; // Setelah level ditemukan dan diubah, berhenti dari loop
                }

                content.append(line).append(System.lineSeparator());
            }

            reader.close();

            // Tulis konten yang telah diperbarui ke file asli
            OutputStream outputStream = new FileOutputStream(fileName);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(content.toString());
            writer.close();

            if (foundLevel) {
                System.out.println("Level " + level + " tidak ditemukan dalam file.");
            } else {
                System.out.println("Status terselesaikan level " + level + " telah diperbarui.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + fileName);
        } catch (IOException e) {
            System.out.println("Terjadi kesalahan saat membaca atau menulis file.");
            e.printStackTrace();
        }
    }


    private void resetTextFieldAndButtons() {
        Field_Kata.setText("");
        jumlahKlik = 0;
        buttonTerakhirKlik = null;
        tombolDiklikSebelumnya.clear();
        buttonStatus.replaceAll((button, aBoolean) -> true);
        buttons.forEach(button -> button.setDisable(false));
    }

    private void newGame() {
        Field_Kata.setText("");
        jumlahKlik = 0;
        tombolDiklikSebelumnya.clear();
        buttonStatus.replaceAll((button, aBoolean) -> true);
        buttons.forEach(button -> button.setDisable(false));
        Panel_Jawaban.getChildren().clear();
        Collections.shuffle(HomeGame.hurufAcak);
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            button.setText(String.valueOf(HomeGame.hurufAcak.get(i)));
        }
    }

}
