package controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class InfoHomeController implements Initializable {
    public Text jmlmahasiswa;
    public Text jmlpertemuan;
    public Text jmllaporan;
    public Alert alert;
    public void initTextFields(FXMLLoader loader) {
        jmlmahasiswa = (Text) loader.getNamespace().get("jmlmahasiswa");
        jmlpertemuan = (Text) loader.getNamespace().get("jmlpertemuan");
        jmllaporan = (Text) loader.getNamespace().get("jmllaporan");
        setMahassiwa();
        setPeretmuan();
        setLaporan();
    }

    public void setMahassiwa() {
        try {
            URL url = new URL("http://127.0.0.1:8000/api/mahasiswa");
            getApi(url, jmlmahasiswa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getApi(URL url, Text text) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray mahasiswas = new JSONArray(response.toString());
            int totalMahasiswa = mahasiswas.length();
            text .setText(String.valueOf(totalMahasiswa));
        } else {
            System.out.println("GET request not worked");
            alert =  new Alert(Alert.AlertType.ERROR);
            alert.setTitle("API Belum Aktif");
            alert.setContentText("Silahkan Aktifkan API Terlebih Dahulu Untuk Mengakses Informasi");
        }
    }

    public void setPeretmuan() {
        try {
            URL url = new URL("http://127.0.0.1:8000/api/pertemuan");
            getApi(url, jmlpertemuan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLaporan() {
        try {
            URL url = new URL("http://127.0.0.1:8000/api/laporan");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            int count = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("status").equals("Mengumpulkan")) {
                    count++;
                }
            }
            jmllaporan.setText(Integer.toString(count));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMahassiwa();
        setPeretmuan();
        setLaporan();
    }
}