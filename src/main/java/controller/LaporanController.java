package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import model.Pertemuan;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class LaporanController implements Initializable {
    @FXML
    public ChoiceBox<Pertemuan> kodepertemuan;
    @FXML
    public Pane laporanpane;
    @FXML
    public ScrollPane scrollpane;
    @FXML
    public AnchorPane anchorabsen;
    @FXML
    public Text textkodepertemuan;
    private final List<ToggleGroup> toggleGroups = new ArrayList<>(); // Declare toggleGroups as a class member

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initPertemuan();
        kodepertemuan.getItems().add(0, new Pertemuan("Pilih Pertemuan", null));
        kodepertemuan.getSelectionModel().selectFirst();
        kodepertemuan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                initializeDataMahasiswa(newValue);
            }
        });

    }
    private void initializeDataMahasiswa(Pertemuan selectedPertemuan) {
        textkodepertemuan.setText(selectedPertemuan.getKode_pertemuan());
        double layoutY = 5;
        double radioButtonLayoutX = 130;
        double textLayout = 5;

        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        JSONArray dataMahasiswa = getLaporanData(kodePertemuan);

        // Clear previous state
        anchorabsen.getChildren().clear();
        toggleGroups.clear();

        for (int i = 0; i < dataMahasiswa.length(); i++) {
            JSONObject mahasiswaObj = dataMahasiswa.getJSONObject(i);
            String namaMahasiswa = mahasiswaObj.getString("nama");
            String statuslaporan = mahasiswaObj.getString("status");
            String kodeLaporan = mahasiswaObj.getString("kode_laporan");

            Text namaText = new Text(namaMahasiswa);
            namaText.setFont(new Font("Comic Sans MS", 12));
            namaText.setTextAlignment(TextAlignment.RIGHT);
            namaText.setLayoutY(layoutY + 12);
            namaText.setLayoutX(textLayout);

            ToggleGroup toggleGroup = new ToggleGroup(); // Membuat ToggleGroup baru untuk setiap mahasiswa
            toggleGroups.add(toggleGroup); // Add the toggleGroup to the class member

            RadioButton mengumpulkanRadioButton = new RadioButton("Mengumpulkan");
            RadioButton telatRadioButton = new RadioButton("Telat");
            RadioButton tidakMengumpulkanRadioButton = new RadioButton("Tidak Mengumpulkan");

            mengumpulkanRadioButton.setToggleGroup(toggleGroup);
            telatRadioButton.setToggleGroup(toggleGroup);
            tidakMengumpulkanRadioButton.setToggleGroup(toggleGroup);
            mengumpulkanRadioButton.setId(kodeLaporan);
            telatRadioButton.setId(kodeLaporan);
            tidakMengumpulkanRadioButton.setId(kodeLaporan);

            if (statuslaporan.equalsIgnoreCase("Mengumpulkan")) {
                toggleGroup.selectToggle(mengumpulkanRadioButton);
            } else if (statuslaporan.equalsIgnoreCase("Telat")) {
                toggleGroup.selectToggle(telatRadioButton);
            } else if (statuslaporan.equalsIgnoreCase("Tidak Mengumpulkan")) {
                toggleGroup.selectToggle(tidakMengumpulkanRadioButton);
            }

            mengumpulkanRadioButton.setLayoutX(radioButtonLayoutX);
            telatRadioButton.setLayoutX(radioButtonLayoutX + 120);
            tidakMengumpulkanRadioButton.setLayoutX(radioButtonLayoutX + 180);
            mengumpulkanRadioButton.setLayoutY(layoutY);
            telatRadioButton.setLayoutY(layoutY);
            tidakMengumpulkanRadioButton.setLayoutY(layoutY);

            anchorabsen.getChildren().addAll(namaText, mengumpulkanRadioButton, telatRadioButton, tidakMengumpulkanRadioButton);

            layoutY += 30;
        }

        scrollpane.setContent(anchorabsen);
    }

    private JSONArray getLaporanData(String kodePertemuan) {
        String apiUrl = "http://127.0.0.1:8000/api/laporan/" + kodePertemuan;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.lines().collect(Collectors.joining());

                return new JSONArray(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    public void initPertemuan() {
        getKodePertemuan(kodepertemuan);
    }

    public static void getKodePertemuan(ChoiceBox<Pertemuan> kodepertemuan) {
        NilaiController.GetKodePertemuan(kodepertemuan);
    }

    @FXML
    public void setAllHadir() {
        for (Node node : anchorabsen.getChildren()) {
            if (node instanceof RadioButton radioButton) {
                radioButton.setSelected(radioButton.getText().equals("Mengumpulkan"));
            }
        }
    }
    public void submit() {
        Map<String, String> dataMap = new HashMap<>();

        for (ToggleGroup toggleGroup : toggleGroups) {
            Toggle selectedToggle = toggleGroup.getSelectedToggle();
            if (selectedToggle instanceof RadioButton radioButton) {
                String kodelaporan = radioButton.getId();
                String toggleValue = radioButton.getText();

                dataMap.put(kodelaporan, toggleValue);
            }
        }

        try {
            // Create the HTTP connection
            URL url = new URL("http://127.0.0.1:8000/api/laporan/update");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Build the request body
            StringBuilder requestBody = new StringBuilder();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                String kodelaporan = entry.getKey();
                String toggleValue = entry.getValue();

                requestBody.append(kodelaporan).append("=").append(toggleValue).append("&");
            }

            // Send the request body
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody.toString());
            outputStream.flush();
            outputStream.close();

            // Get the response
            int responseCode = connection.getResponseCode();

            Alert alert;
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informasi");
                alert.setHeaderText(null);
                alert.setContentText("Laporan Berhasil Diupdate");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Gagal Mengupdate Laporan");
            }
            alert.showAndWait();

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
