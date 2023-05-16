package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class KeaktifanController implements Initializable {
    @FXML
    public TableView<Keaktifan> tabelkeaktifan;
    @FXML
    public TableColumn<model.Keaktifan, String> kodekeaktifan;
    @FXML
    public TableColumn<model.Keaktifan, String> tabelnim;
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
                fieldpertemuan.setItems(pertemuanList);
                fieldpertemuan.setConverter(new StringConverter<>() {
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
                    fieldpertemuan.setValue(pertemuanList.get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTabel(){
        try {
            URL url = new URL(Route.URL + "keaktifan");
            ExtractData(url, kodekeaktifan, kodepertemuan, tabelnim, keterangan, tabelkeaktifan);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        tabelkeaktifan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fieldkodekeaktifan.setText(newValue.getKodeKeaktifan());
                fieldketerangan.setText(newValue.getKeterangan());
                // Mencari objek Mahasiswa dengan nim yang sesuai
                Mahasiswa selectedMahasiswa = fieldnim.getItems().stream()
                        .filter(mahasiswa -> mahasiswa.getNim().equals(newValue.getNim()))
                        .findFirst()
                        .orElse(null);
                Pertemuan selectedPertemuan = fieldpertemuan.getItems().stream()
                        .filter(pertemuan -> pertemuan.getKode_pertemuan().equals(newValue.getKodePertemuan()))
                        .findFirst()
                        .orElse(null);
                // Mengatur nilai ChoiceBox dengan objek Mahasiswa yang sesuai
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
                String kode_keaktifan = jsonObject.getString("kode_keaktifan");
                String kode_pertemuan = jsonObject.getString("kode_pertemuan");
                String nim = jsonObject.getString("nim");
                String keterangan = jsonObject.getString("keterangan");
                dataKeaktifan.add(new Keaktifan(kode_keaktifan, kode_pertemuan, nim, keterangan));
            }
            kodekeaktifan.setCellValueFactory(new PropertyValueFactory<>("kodeKeaktifan"));
            kodepertemuan.setCellValueFactory(new PropertyValueFactory<>("kodePertemuan"));
            tabelnim.setCellValueFactory(new PropertyValueFactory<>("nim"));
            keterangan2.setCellValueFactory(new PropertyValueFactory<>("keterangan"));
            tabelkeaktifan.setItems(dataKeaktifan);
        }
        con.disconnect();
    }


    public void tambah(){

    }
    public void update(){

    }
    public void hapus(){

    }
    public void clear(){

    }


}
