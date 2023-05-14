package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Pertemuan;
import org.json.JSONArray;
import org.json.JSONObject;
import project.Route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PertemuanController implements Initializable {
    public TextField fieldkodepertemuan;

    public DatePicker fieldtanggalpertemuan;
    Alert alert;
    @FXML
    private TableView<Pertemuan> tabelpertemuan;
    @FXML
    private TableColumn<Pertemuan, String> kodepertemuan;
    @FXML
    private TableColumn<model.Pertemuan, String> tanggalpertemuan;

    public void setTabel() {
        try {
            URL url = new URL(Route.URL + "pertemuan");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                JSONArray jsonArray = new JSONArray(content.toString());
                ObservableList<Pertemuan> dataPertemuan = FXCollections.observableArrayList();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String kode_pertemuan = jsonObject.getString("kode_pertemuan");
                    LocalDate tanggal_pertemuan = LocalDate.parse(jsonObject.getString("tanggal_pertemuan"));
                    dataPertemuan.add(new Pertemuan(kode_pertemuan, tanggal_pertemuan));
                }
                kodepertemuan.setCellValueFactory(new PropertyValueFactory<>("kode_pertemuan"));
                tanggalpertemuan.setCellValueFactory(new PropertyValueFactory<>("tanggal_pertemuan"));
                tabelpertemuan.setItems(dataPertemuan);
            }
            con.disconnect();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        tabelpertemuan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fieldkodepertemuan.setText(newValue.getKode_pertemuan());
                fieldtanggalpertemuan.setValue(newValue.getTanggal_pertemuan());
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTabel();
    }


    public void clear() {
        fieldkodepertemuan.clear();
        fieldtanggalpertemuan.setValue(null);
    }

    public void tambah() throws Exception {
        URL url = new URL(Route.URL + "pertemuan/store");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        LocalDate date = fieldtanggalpertemuan.getValue();
        DateTimeFormatter apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String apiDate = date.format(apiDateFormatter);
        String requestBody = String.format("{\"kode_pertemuan\":\"%s\",\"tanggal_pertemuan\":\"%s\"}",
                fieldkodepertemuan.getText(), apiDate);
        byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
        conn.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBodyBytes, 0, requestBodyBytes.length);
        }
        int httpResponseCode = conn.getResponseCode();
        if (httpResponseCode == 201) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informasi");
            alert.setHeaderText(null);
            alert.setContentText("Pertemuan Ditambahkan");
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal Menambahkan");
        }
        alert.showAndWait();
        setTabel();
    }

    public void hapus() {
        try {
            URL url = new URL(Route.URL + "pertemuan/delete/" + fieldkodepertemuan.getText());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 201) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informasi");
                alert.setHeaderText(null);
                alert.setContentText("Pertemuan Berhasil Dihapus");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Gagal Menghapus");
            }
            alert.showAndWait();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTabel();
    }
    public void update() {
        try {
            URL url = new URL(Route.URL + "pertemuan/" + fieldkodepertemuan.getText());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            LocalDate date = fieldtanggalpertemuan.getValue();
            DateTimeFormatter apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String apiDate = date.format(apiDateFormatter);
            String requestBody = String.format("{\"kode_pertemuan\":\"%s\",\"tanggal_pertemuan\":\"%s\"}",
                    fieldkodepertemuan.getText(), apiDate);
            byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            conn.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBodyBytes, 0, requestBodyBytes.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 201) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informasi");
                alert.setHeaderText(null);
                alert.setContentText("Pertemuan Berhasil Diupdate");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Gagal Mengupdate");
            }
            alert.showAndWait();
            conn.disconnect();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi masalah saat melakukan update data mahasiswa!");
        }
        setTabel();
    }
}
