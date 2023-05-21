package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import model.Keaktifan;
import model.Mahasiswa;
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
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class KeaktifanController implements Initializable {
    Alert alert;
    @FXML
    public TableView<Keaktifan> tabelkeaktifan;
    @FXML
    public TableColumn<model.Keaktifan, String> kodekeaktifan;
    @FXML
    public TableColumn<model.Keaktifan, String> tabelnim;
    @FXML
    public TableColumn<model.Keaktifan, String> tablenama;
    @FXML
    public TableColumn<model.Keaktifan, String> kodepertemuan;
    @FXML
    public TableColumn<model.Keaktifan, String> keterangan;
    @FXML
    public TextField fieldkodekeaktifan;
    @FXML
    public TextField fieldketerangan;
    @FXML
    public ChoiceBox<Mahasiswa> fieldnim;
    @FXML
    public ChoiceBox<Pertemuan> fieldpertemuan;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initFieldNim();
        initFieldPertemuan();
        setTabel();
    }
    public void initFieldNim() {
        String apiUrl = Route.URL + "mahasiswa";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.lines().collect(Collectors.joining());

                JSONArray jsonArray = new JSONArray(response);
                ObservableList<Mahasiswa> mahasiswaList = FXCollections.observableArrayList();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String nim = jsonObject.getString("nim");
                    String nama = jsonObject.getString("nama");
                    String noHp = jsonObject.getString("no_hp");

                    Mahasiswa mahasiswa = new Mahasiswa(nim, nama, noHp);
                    mahasiswaList.add(mahasiswa);
                }

                fieldnim.setItems(mahasiswaList);

                fieldnim.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Mahasiswa mahasiswa) {
                        return (mahasiswa != null) ? mahasiswa.getNama() : "";
                    }

                    @Override
                    public Mahasiswa fromString(String s) {
                        return null;
                    }
                });
                if (!mahasiswaList.isEmpty()) {
                    fieldnim.setValue(mahasiswaList.get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void initFieldPertemuan() {
        AbsensiController.getKodePertemuan(fieldpertemuan);
    }

    public void setTabel(){
        try {
            URL url = new URL(Route.URL + "keaktifan");
            ExtractData(url, kodekeaktifan, kodepertemuan, tabelnim, keterangan, tabelkeaktifan);
            tablenama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        tabelkeaktifan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fieldkodekeaktifan.setText(newValue.getKodeKeaktifan());
                fieldketerangan.setText(newValue.getKeterangan());
                Mahasiswa selectedMahasiswa = fieldnim.getItems().stream()
                        .filter(mahasiswa -> mahasiswa.getNim().equals(newValue.getNim()))
                        .findFirst()
                        .orElse(null);
                Pertemuan selectedPertemuan = fieldpertemuan.getItems().stream()
                        .filter(pertemuan -> pertemuan.getKode_pertemuan().equals(newValue.getKodePertemuan()))
                        .findFirst()
                        .orElse(null);
                fieldnim.setValue(selectedMahasiswa);
                fieldpertemuan.setValue(selectedPertemuan);
            }
        });
    }

    public static void ExtractData(URL url, TableColumn<Keaktifan, String> kodekeaktifan, TableColumn<Keaktifan, String> kodepertemuan, TableColumn<Keaktifan, String> tabelnim, TableColumn<Keaktifan, String> keterangan2, TableView<Keaktifan> tabelkeaktifan) throws IOException {
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
            ObservableList<Keaktifan> dataKeaktifan = FXCollections.observableArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String nama = jsonObject.getString("nama");
                String kode_keaktifan = jsonObject.getString("kode_keaktifan");
                String kode_pertemuan = jsonObject.getString("kode_pertemuan");
                String nim = jsonObject.getString("nim");
                String keterangan = jsonObject.getString("keterangan");
                dataKeaktifan.add(new Keaktifan(kode_keaktifan, kode_pertemuan, nim, keterangan, nama));
            }
            kodekeaktifan.setCellValueFactory(new PropertyValueFactory<>("kodeKeaktifan"));
            kodepertemuan.setCellValueFactory(new PropertyValueFactory<>("kodePertemuan"));
            tabelnim.setCellValueFactory(new PropertyValueFactory<>("nim"));
            keterangan2.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
            tabelkeaktifan.setItems(dataKeaktifan);
        }
        con.disconnect();
    }


    public void tambah() throws Exception {
        Mahasiswa selectedMahasiswa = fieldnim.getValue();
        Pertemuan selectedPertemuan = fieldpertemuan.getValue();
        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        String nimMahasiswa = selectedMahasiswa.getNim();
        String keterangan = fieldketerangan.getText();

        URL url = new URL(Route.URL + "keaktifan/store");

        int httpResponseCode = fetchApi(kodePertemuan, nimMahasiswa, keterangan, url);

        if (httpResponseCode == 201) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informasi");
            alert.setHeaderText(null);
            alert.setContentText("Keaktifan Ditambahkan");
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal Menambahkan Keaktifan");
        }

        alert.showAndWait();
        setTabel();
    }

    public int fetchApi(String kodePertemuan, String nimMahasiswa, String keterangan, URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"kode_pertemuan\":\"%s\",\"nim\":\"%s\",\"keterangan\":\"%s\"}",
                kodePertemuan, nimMahasiswa, keterangan);

        byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
        conn.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBodyBytes, 0, requestBodyBytes.length);
        }

        return conn.getResponseCode();
    }

    public void update() throws Exception{
        Mahasiswa selectedMahasiswa = fieldnim.getValue();
        Pertemuan selectedPertemuan = fieldpertemuan.getValue();
        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        String nimMahasiswa = selectedMahasiswa.getNim();
        String keterangan = fieldketerangan.getText();

        URL url = new URL(Route.URL + "keaktifan/update/" + fieldkodekeaktifan.getText());
        int httpResponseCode = fetchApi(kodePertemuan, nimMahasiswa, keterangan, url);

        if (httpResponseCode == 201) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informasi");
            alert.setHeaderText(null);
            alert.setContentText("Keaktifan Berhasil di Update");
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal Mengupdate Keaktifan");
        }

        alert.showAndWait();
        setTabel();
    }
    public void hapus(){
        try {
            URL url = new URL(Route.URL + "keaktifan/" + fieldkodekeaktifan.getText());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 201) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informasi");
                alert.setHeaderText(null);
                alert.setContentText("Keaktifan Berhasil Dihapus");
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
    public void clear(){
        fieldkodekeaktifan.clear();
        fieldketerangan.clear();
    }


}
