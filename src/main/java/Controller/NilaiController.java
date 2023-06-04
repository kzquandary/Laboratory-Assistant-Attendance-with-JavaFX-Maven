package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import Model.Pertemuan;
import org.json.JSONArray;
import org.json.JSONObject;
import project.Route;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static Controller.LaporanController.getObjects;

public class NilaiController implements Initializable {
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
    private final List<TextField> nilaiFields = new ArrayList<>();
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
        textkodepertemuan.setText("Kode Pertemuan : " + selectedPertemuan.getKode_pertemuan());
        double layoutY = 10;
        double textLayout = 10;
        double formLayoutX = 180;

        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        JSONArray dataMahasiswa = getAbsensiData(kodePertemuan);

        anchorabsen.getChildren().clear();
        nilaiFields.clear();

        for (int i = 0; i < dataMahasiswa.length(); i++) {
            JSONObject mahasiswaObj = dataMahasiswa.getJSONObject(i);
            String namaMahasiswa = mahasiswaObj.getString("nama");
            int nilaiMahasiswa = mahasiswaObj.getInt("nilai");
            String kodeNilai = mahasiswaObj.getString("kode_nilai");

            Text namaText = new Text(namaMahasiswa);
            namaText.setFont(new Font("Comic Sans MS", 12));
            namaText.setTextAlignment(TextAlignment.RIGHT);
            namaText.setLayoutY(layoutY + 12);
            namaText.setLayoutX(textLayout);

            TextField nilaiField = new TextField(Integer.toString(nilaiMahasiswa));
            nilaiField.setPrefWidth(250);
            nilaiField.setPrefHeight(20);
            nilaiField.setLayoutX(formLayoutX);
            nilaiField.setLayoutY(layoutY);
            nilaiField.setId(kodeNilai);

            anchorabsen.getChildren().addAll(namaText, nilaiField);
            nilaiFields.add(nilaiField);

            layoutY += 30;
        }

        scrollpane.setContent(anchorabsen);
    }

    private JSONArray getAbsensiData(String kodePertemuan) {
        String apiUrl = Route.URL + "nilai/" + kodePertemuan;

        return getObjects(apiUrl);
    }

    public void initPertemuan() {
        getKodePertemuan(kodepertemuan);
    }

    public static void getKodePertemuan(ChoiceBox<Pertemuan> kodepertemuan) {
        GetKodePertemuan(kodepertemuan);
    }

    static void GetKodePertemuan(ChoiceBox<Pertemuan> kodepertemuan) {
        String apiUrl = Route.URL + "pertemuan";

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
                        if (pertemuan != null) {
                            return pertemuan.getKode_pertemuan();
                        } else {
                            return "Pertemuan kosong";
                        }
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

    public void submit() {
        Map<String, String> dataMap = new HashMap<>();

        for (TextField nilaiField : nilaiFields) {
            String kodeNilai = nilaiField.getId();
            String nilai = nilaiField.getText();

            dataMap.put(kodeNilai, nilai);
        }

        try {
            // Create the HTTP connection
            URL url = new URL(Route.URL + "nilai/update");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Build the request body
            StringBuilder requestBody = new StringBuilder();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                String kodeNilai = entry.getKey();
                String nilai = entry.getValue();

                requestBody.append(kodeNilai).append("=").append(nilai).append("&");
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
                alert.setContentText("Nilai Diupdate");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Gagal Mengupdate Nilai");
            }
            alert.showAndWait();

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
