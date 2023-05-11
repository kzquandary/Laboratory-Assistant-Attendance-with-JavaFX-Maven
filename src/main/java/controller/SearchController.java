package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.Absensi;
import model.Mahasiswa;
import org.json.JSONArray;
import org.json.JSONObject;
import project.Route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchController {
    @FXML
    public Text setNIM;
    @FXML
    public Text setNAMA;
    @FXML
    public Text setNOHP;
    @FXML
    public Text textInfo;
    @FXML
    public Pane buttonsearch;
    @FXML
    public Pane infoPane;
    @FXML
    public Pane tabelkonten;
    @FXML
    public TableView<model.Absensi> tabelAbsen;
    @FXML
    public TableColumn<model.Absensi, String> kodeabsen;
    @FXML
    public TableColumn<model.Absensi, String> nimabsen;
    @FXML
    public TableColumn<model.Absensi, String> statusabsen;
    @FXML
    public TableColumn<model.Absensi, String> kodepertemuanabsen;
    private boolean isTabelVisible = false;
    private String searchText;

    public void setSearch(String searchText) {
        this.searchText = searchText;
        performSearch();
    }

    private void performSearch() {
        if (searchText != null && !searchText.isEmpty()) {
            if (searchText.startsWith("62") || searchText.startsWith("08")) {
                searchByNohp();
            } else if (isNumeric(searchText)) {
                searchByNIM();
            } else {
                searchByName();
            }
        }
    }


    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private void searchByNohp() {
        String nohp = searchText;
        if (searchText.startsWith("62")) {
            nohp = "0" + nohp.substring(2);
        }
        String apiUrl = "http://127.0.0.1:8000/api/mahasiswa/nohp/" + nohp;
        extractData(apiUrl);
    }

    private void extractData(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                String nim = jsonObject.getString("nim");
                String nama = jsonObject.getString("nama");
                String noHp = jsonObject.getString("no_hp");
                textInfo.setText("Biodata Mahasiswa");
                setNIM.setText("NIM :" + nim);
                setNAMA.setText("NAMA : " + nama);
                setNOHP.setText("No HP : " + noHp);
                buttonsearch.setVisible(true);
                infoPane.setVisible(true);
            } else if (responseCode == 404) {
                textInfo.setText("Data Mahasiswa Tidak Ditemukan");
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchByNIM() {
        String nim = searchText;
        String link = "http://127.0.0.1:8000/api/mahasiswa/nim/" + nim;
        try {
            URL url = new URL(link);
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
                JSONObject jsonObject = new JSONObject(content.toString());
                String fetchedNama = jsonObject.getString("nama");
                String fetchedNIM = jsonObject.getString("nim");
                String fetchedNo_hp = jsonObject.getString("no_hp");

                textInfo.setText("Biodata Mahasiswa");
                setNIM.setText("NIM :" + fetchedNIM);
                setNAMA.setText("NAMA : " + fetchedNama);
                setNOHP.setText("No HP : " + fetchedNo_hp);
                buttonsearch.setVisible(true);
                infoPane.setVisible(true);
            } else if (status == 404) {
                textInfo.setText("Data Mahasiswa Tidak Ditemukan");
            }
            con.disconnect();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchByName() {
        String name = searchText;
        String param = name.replace(" ", "%20");
        String apiUrl = "http://127.0.0.1:8000/api/mahasiswa/nama/" + param;

        extractData(apiUrl);
    }

    public void absensi() {
        isTabelVisible = !isTabelVisible; // Toggle visibilitas tabel

        tabelkonten.setVisible(isTabelVisible);
        tabelAbsen.setVisible(isTabelVisible);

        if (isTabelVisible) {
            String searchnim = searchText;
            try {
                URL url = new URL(Route.URL + "absensi/nim/" + searchnim);
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
                    ObservableList<Absensi> dataAbsen = FXCollections.observableArrayList();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String kode_absen = jsonObject.getString("kode_absen");
                        String kode_pertemuan = jsonObject.getString("kode_pertemuan");
                        String nim = jsonObject.getString("nim");
                        String statusabsen = jsonObject.getString("status");
                        dataAbsen.add(new Absensi(kode_absen, kode_pertemuan, nim, statusabsen));
                    }
                    kodeabsen.setCellValueFactory(new PropertyValueFactory<>("kodeAbsen"));
                    kodepertemuanabsen.setCellValueFactory(new PropertyValueFactory<>("kodePertemuan"));
                    nimabsen.setCellValueFactory(new PropertyValueFactory<>("nim"));
                    statusabsen.setCellValueFactory(new PropertyValueFactory<>("status"));
                    tabelAbsen.setItems(dataAbsen);
                }
                con.disconnect();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    public void laporan(){

    }
    public void nilai(){

    }
    public void keaktifan(){

    }
}
