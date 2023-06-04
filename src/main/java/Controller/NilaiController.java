package Controller;

import Model.Pertemuan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
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
    public Text textkodepertemuan;
    public Pagination pagination;
    public Pane contentPane = new Pane();
    private final Map<Integer, Map<String, String>> nilaiFieldMap = new HashMap<>();

    public Map<String, String> tempMhs = new HashMap<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initPertemuan();
        kodepertemuan.getItems().add(0, new Pertemuan("Pilih Pertemuan", null));
        kodepertemuan.getSelectionModel().selectFirst();
        kodepertemuan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.getKode_pertemuan().equals("Pilih Pertemuan")) {
                    pagination.setVisible(false);
                    textkodepertemuan.setVisible(false);
                } else {
                    initializeDataMahasiswa(newValue);
                }
            }
        });
    }
    private void initializeDataMahasiswa(Pertemuan selectedPertemuan) {
        pagination.setVisible(true);
        textkodepertemuan.setText("Kode Pertemuan : " + selectedPertemuan.getKode_pertemuan());
        final double[] layoutY = {10};
        double textLayout = 10;
        double formLayoutX = 180;

        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        JSONArray dataMahasiswa = getAbsensiData(kodePertemuan);
        for (int i = 0; i < dataMahasiswa.length(); i++) {
            JSONObject dataMhs = dataMahasiswa.getJSONObject(i);
            tempMhs.put(dataMhs.getString("kode_nilai"), String.valueOf(dataMhs.getInt("nilai")));
        }
        int mahasiswaPerHalaman = 12;
        int jumlahHalaman = (int) Math.ceil((double) dataMahasiswa.length() / mahasiswaPerHalaman);
        pagination.setPageCount(jumlahHalaman);

        pagination.setPageFactory(pageIndex -> {
            if (pageIndex >= 0 && pageIndex < pagination.getPageCount()) {
                int halaman = pageIndex;
                int awal = halaman * mahasiswaPerHalaman;
                int akhir = Math.min(awal + mahasiswaPerHalaman, dataMahasiswa.length());

                contentPane.getChildren().clear();
                layoutY[0] = 10;

                Map<String, String> nilaiMap = nilaiFieldMap.getOrDefault(pageIndex, new HashMap<>());

                for (int i = awal; i < akhir; i++) {
                    JSONObject mahasiswaObj = dataMahasiswa.getJSONObject(i);
                    String namaMahasiswa = mahasiswaObj.getString("nama");
                    String kodenilai = mahasiswaObj.getString("kode_nilai");

                    Text namaText = new Text(namaMahasiswa);
                    namaText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
                    namaText.setTextAlignment(TextAlignment.RIGHT);
                    namaText.setLayoutY(layoutY[0] + 24);
                    namaText.setLayoutX(textLayout);
                    namaText.setFill(Color.WHITE);

                    TextField nilaiField = new TextField(nilaiMap.getOrDefault(kodenilai, String.valueOf(mahasiswaObj.getInt("nilai"))));
                    nilaiField.setPrefWidth(250);
                    nilaiField.setPrefHeight(20);
                    nilaiField.setLayoutX(formLayoutX);
                    nilaiField.setLayoutY(layoutY[0]);
                    nilaiField.setId(kodenilai);
                    nilaiField.setOnKeyReleased(keyEvent -> {
                        nilaiMap.put(kodenilai, nilaiField.getText());
                        tempMhs.put(kodenilai, nilaiField.getText());
                        }
                    );

                    contentPane.getChildren().addAll(namaText, nilaiField);

                    layoutY[0] += 30;
                }

                nilaiFieldMap.put(pageIndex, nilaiMap);

                return contentPane;
            } else {
                return null;
            }
        });
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
        try {
            // Create the HTTP connection
            URL url = new URL(Route.URL + "nilai/update");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Build the request body
            StringBuilder requestBody = new StringBuilder();
            for (Map.Entry<String, String> entry : tempMhs.entrySet()) {
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
