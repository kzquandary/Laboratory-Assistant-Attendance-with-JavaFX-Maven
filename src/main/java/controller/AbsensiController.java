package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import model.Pertemuan;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AbsensiController implements Initializable {
    @FXML
    public ChoiceBox<Pertemuan> kodepertemuan;
    @FXML
    public Pane absensipane;
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
        double layoutY = 10;
        double radioButtonLayoutX = 200;
        double textLayout = 80;

        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        JSONArray dataMahasiswa = getAbsensiData(kodePertemuan);

        // Clear previous state
        anchorabsen.getChildren().clear();
        toggleGroups.clear();

        for (int i = 0; i < dataMahasiswa.length(); i++) {
            JSONObject mahasiswaObj = dataMahasiswa.getJSONObject(i);
            String namaMahasiswa = mahasiswaObj.getString("nama");
            String statusKehadiran = mahasiswaObj.getString("status");
            String kodeabsensi = mahasiswaObj.getString("kode_absen");

            Text namaText = new Text(namaMahasiswa);
            namaText.setFont(new Font("Comic Sans MS", 12));
            namaText.setTextAlignment(TextAlignment.RIGHT);
            namaText.setLayoutY(layoutY + 12);
            namaText.setLayoutX(textLayout);

            ToggleGroup toggleGroup = new ToggleGroup(); // Membuat ToggleGroup baru untuk setiap mahasiswa
            toggleGroups.add(toggleGroup); // Add the toggleGroup to the class member

            RadioButton hadirRadioButton = new RadioButton("Hadir");
            RadioButton izinRadioButton = new RadioButton("Izin");
            RadioButton alphaRadioButton = new RadioButton("Alpha");

            hadirRadioButton.setToggleGroup(toggleGroup);
            izinRadioButton.setToggleGroup(toggleGroup);
            alphaRadioButton.setToggleGroup(toggleGroup);
            hadirRadioButton.setId(kodeabsensi);
            izinRadioButton.setId(kodeabsensi);
            alphaRadioButton.setId(kodeabsensi);

            if (statusKehadiran.equalsIgnoreCase("Hadir")) {
                toggleGroup.selectToggle(hadirRadioButton);
            } else if (statusKehadiran.equalsIgnoreCase("Izin")) {
                toggleGroup.selectToggle(izinRadioButton);
            } else if (statusKehadiran.equalsIgnoreCase("Alpha")) {
                toggleGroup.selectToggle(alphaRadioButton);
            }

            hadirRadioButton.setLayoutX(radioButtonLayoutX);
            izinRadioButton.setLayoutX(radioButtonLayoutX + 60);
            alphaRadioButton.setLayoutX(radioButtonLayoutX + 120);
            hadirRadioButton.setLayoutY(layoutY);
            izinRadioButton.setLayoutY(layoutY);
            alphaRadioButton.setLayoutY(layoutY);

            anchorabsen.getChildren().addAll(namaText, hadirRadioButton, izinRadioButton, alphaRadioButton);

            layoutY += 30;
        }

        scrollpane.setContent(anchorabsen);
    }

    private JSONArray getAbsensiData(String kodePertemuan) {
        String apiUrl = "http://127.0.0.1:8000/api/absensi/" + kodePertemuan;

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
        String apiUrl = "http://127.0.0.1:8000/api/pertemuan";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.lines().collect(Collectors.joining());

                JSONArray jsonArray = new JSONArray(response);
                ObservableList<Pertemuan> pertemuanList = FXCollections.observableArrayList();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String kodePertemuan = jsonObject.getString("kode_pertemuan");
                    LocalDate tanggalPertemuan = LocalDate.parse(jsonObject.getString("tanggal_pertemuan"));

                    Pertemuan pertemuan = new Pertemuan(kodePertemuan, tanggalPertemuan);
                    pertemuanList.add(pertemuan);
                }
                kodepertemuan.setItems(pertemuanList);
                kodepertemuan.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Pertemuan pertemuan) {
                        return pertemuan.getKode_pertemuan();
                    }

                    @Override
                    public Pertemuan fromString(String s) {
                        return null;
                    }
                });
                if (!pertemuanList.isEmpty()) {
                    kodepertemuan.setValue(pertemuanList.get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setAllHadir() {
        for (Node node : anchorabsen.getChildren()) {
            if (node instanceof RadioButton radioButton) {
                radioButton.setSelected(radioButton.getText().equals("Hadir"));
            }
        }
    }
    public void submit() {
        Map<String, String> dataMap = new HashMap<>();

        for (ToggleGroup toggleGroup : toggleGroups) {
            Toggle selectedToggle = toggleGroup.getSelectedToggle();
            if (selectedToggle instanceof RadioButton radioButton) {
                String kodeabsensi = radioButton.getId();
                String toggleValue = radioButton.getText();

                dataMap.put(kodeabsensi, toggleValue);
            }
        }

        try {
            // Create the HTTP connection
            URL url = new URL("http://127.0.0.1:8000/api/absensi/update");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Build the request body
            StringBuilder requestBody = new StringBuilder();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                String kodeabsensi = entry.getKey();
                String toggleValue = entry.getValue();

                requestBody.append(kodeabsensi).append("=").append(toggleValue).append("&");
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
                alert.setContentText("Absensi Diupdate");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Gagal Mengupdate Absensi");
            }
            alert.showAndWait();

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
