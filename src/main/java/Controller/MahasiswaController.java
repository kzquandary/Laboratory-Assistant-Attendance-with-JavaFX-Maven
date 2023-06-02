package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Model.Mahasiswa;
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
import java.util.ResourceBundle;

public class MahasiswaController implements Initializable {
    public TextField fieldnim;

    public TextField fieldnama;

    public TextField fieldnohp;
    Alert alert;
    @FXML
    private TableView<Model.Mahasiswa> tabelmahasiswa;
    @FXML
    private TableColumn<Model.Mahasiswa, String> nim;
    @FXML
    private TableColumn<Model.Mahasiswa, String> nama;
    @FXML
    private TableColumn<Model.Mahasiswa, String> nohp;

    public void setTabel() {
        try {
            URL url = new URL(Route.URL + "mahasiswa");
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
                ObservableList<Mahasiswa> dataMhs = FXCollections.observableArrayList();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String nama = jsonObject.getString("nama");
                    String nim = jsonObject.getString("nim");
                    String nohp = jsonObject.getString("no_hp");
                    dataMhs.add(new Mahasiswa(nim, nama, nohp));
                }
                nim.setCellValueFactory(new PropertyValueFactory<>("nim"));
                nama.setCellValueFactory(new PropertyValueFactory<>("nama"));
                nohp.setCellValueFactory(new PropertyValueFactory<>("nohp"));
                tabelmahasiswa.setItems(dataMhs);
            }
            con.disconnect();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        tabelmahasiswa.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fieldnim.setText(newValue.getNim());
                fieldnama.setText(newValue.getNama());
                fieldnohp.setText(newValue.getNohp());
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTabel();
    }


    public void clearmhs() {
        fieldnim.clear();
        fieldnama.clear();
        fieldnohp.clear();
    }

    public void tambah() throws Exception {
        URL url = new URL(Route.URL + "mahasiswa/store");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        String requestBody = String.format("{\"nim\":\"%s\",\"nama\":\"%s\",\"no_hp\":\"%s\"}",
                fieldnim.getText(), fieldnama.getText(), fieldnohp.getText());
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
            alert.setContentText("Mahasiswa Ditambahkan");
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
            URL url = new URL(Route.URL + "mahasiswa/" + fieldnim.getText());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 201) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informasi");
                alert.setHeaderText(null);
                alert.setContentText("Mahasiswa Berhasil Dihapus");
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
            URL url = new URL(Route.URL + "mahasiswa/update/" + fieldnim.getText());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            String requestBody = String.format("{\"nama\":\"%s\",\"no_hp\":\"%s\"}",
                    fieldnama.getText(), fieldnohp.getText());
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
                alert.setContentText("Mahasiswa Berhasil Diupdate");
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
