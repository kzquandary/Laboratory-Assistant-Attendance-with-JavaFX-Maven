package Controller;

import Model.Absensi;
import Model.Keaktifan;
import Model.Laporan;
import Model.Nilai;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import Project.Action;
import Project.ApiRoute;
import Project.StringVariable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class SearchController {
    private final Absensi absensi = new Absensi();
    private final Laporan laporan = new Laporan();
    private final Nilai nilai = new Nilai();
    private final Keaktifan keaktifan = new Keaktifan();
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
    public TableView<Model.Absensi> tabelAbsen;
    @FXML
    public TableColumn<Model.Absensi, String> kodeabsen;
    @FXML
    public TableColumn<Model.Absensi, String> nimabsen;
    @FXML
    public TableColumn<Model.Absensi, String> statusabsen;
    @FXML
    public TableColumn<Model.Absensi, String> kodepertemuanabsen;
    @FXML
    public TableView<Model.Laporan> tabelLaporan;
    @FXML
    public TableColumn<Model.Laporan, String> kodelaporan;
    @FXML
    public TableColumn<Model.Laporan, String> nimlaporan;
    @FXML
    public TableColumn<Model.Laporan, String> statuslaporan;
    @FXML
    public TableColumn<Model.Laporan, String> kodepertemuanlaporan;
    @FXML
    public TableView<Model.Nilai> tabelNilai;
    @FXML
    public TableColumn<Model.Nilai, String> kodenilai;
    @FXML
    public TableColumn<Model.Nilai, String> kodelaporannilai;
    @FXML
    public TableColumn<Model.Nilai, String> kodepertemuannilai;
    @FXML
    public TableColumn<Model.Nilai, String> nimnilai;
    @FXML
    public TableColumn<Model.Nilai, String> nilainilai;
    @FXML
    public TableView<Model.Keaktifan> tabelKeaktifan;
    @FXML
    public TableColumn<Model.Keaktifan, String> kodekeaktifan;
    @FXML
    public TableColumn<Model.Keaktifan, String> kodepertemuankeaktifan;
    @FXML
    public TableColumn<Model.Keaktifan, String> nimkeaktifan;
    @FXML
    public TableColumn<Model.Keaktifan, String> keterangankeaktifan;
    private String searchText;
    private boolean isTabelAbsenVisible = false;
    private boolean isTabelLaporanVisible = false;
    private boolean isTabelNilaiVisible = false;
    private boolean isTabelKeaktifanVisible = false;

    public void setSearch(String searchText) {
        this.searchText = searchText;
        performSearch();
    }

    private void performSearch() {
        if (searchText != null && !searchText.isEmpty()) {
            if (Pattern.matches(StringVariable.RegNoHP, searchText)) {
                searchByNohp();
            } else if (Pattern.matches(StringVariable.RegNIM, searchText)) {
                searchByNIM();
            } else {
                searchByName();
            }
        }
    }

    private void searchByNohp() {
        String nohp = searchText;
        if (searchText.startsWith("62")) {
            nohp = "0" + nohp.substring(2);
        }
        String apiUrl = ApiRoute.setGetMahasiswaByNohp(nohp);
        extractData(apiUrl);
    }

    private void extractData(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.GET);

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
                setAbsensi(jsonObject, nim, nama);
            } else if (responseCode == 404) {
                textInfo.setText("Data Mahasiswa Tidak Ditemukan");
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException e) {
            Action.alerterror(StringVariable.ApiError);
        }
    }

    private void setAbsensi(JSONObject jsonObject, String nim, String nama) {
        String noHp = jsonObject.getString("no_hp");
        textInfo.setText("Biodata Mahasiswa");
        setNIM.setText("NIM :" + nim);
        setNAMA.setText("NAMA : " + nama);
        setNOHP.setText("No HP : " + noHp);
        buttonsearch.setVisible(true);
        infoPane.setVisible(true);
        absensi.setNim(nim);
        laporan.setNim(nim);
        nilai.setNim(nim);
        keaktifan.setNim(nim);
    }

    private void searchByNIM() {
        String nim = searchText;
        String link = ApiRoute.setGetMahasiswaByNim(nim);
        try {
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(StringVariable.GET);
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
                setAbsensi(jsonObject, fetchedNIM, fetchedNama);
            } else if (status == 404) {
                textInfo.setText("Data Mahasiswa Tidak Ditemukan");
            }
            con.disconnect();
        } catch (Exception e) {
            Action.alerterror(StringVariable.ExceptionE(String.valueOf(e)));
        }
    }

    private void searchByName() {
        String name = searchText;
        String param = name.replace(" ", "%20");
        String apiUrl = ApiRoute.setGetMahasiswaByNama(param);

        extractData(apiUrl);
    }

    public void absensi() {
        if (isTabelLaporanVisible) {
            tabelLaporan.setVisible(false);
            isTabelLaporanVisible = false;
        }
        if (isTabelNilaiVisible) {
            tabelNilai.setVisible(false);
            isTabelNilaiVisible = false;
        }
        if (isTabelKeaktifanVisible) {
            tabelKeaktifan.setVisible(false);
            isTabelKeaktifanVisible = false;
        }

        if (!isTabelAbsenVisible) {
            tabelkonten.setVisible(true);
            tabelAbsen.setVisible(true);
            isTabelAbsenVisible = true;
            String searchnim = absensi.getNim();
            try {
                URL url = new URL(ApiRoute.setGetAbsensiByNim(searchnim));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(StringVariable.GET);
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
                Action.alerterror(StringVariable.ApiError);
            }
        }
    }

    public void laporan() {
        if (isTabelAbsenVisible) {
            tabelAbsen.setVisible(false);
            isTabelAbsenVisible = false;
        }
        if (isTabelNilaiVisible) {
            tabelNilai.setVisible(false);
            isTabelNilaiVisible = false;
        }
        if (isTabelKeaktifanVisible) {
            tabelKeaktifan.setVisible(false);
            isTabelKeaktifanVisible = false;
        }

        if (!isTabelLaporanVisible) {
            tabelkonten.setVisible(true);
            tabelLaporan.setVisible(true);
            isTabelLaporanVisible = true;
            String searchnim = laporan.getNim();
            try {
                URL url = new URL(ApiRoute.setGetLaporanByNim(searchnim));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(StringVariable.GET);
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
                    ObservableList<Laporan> dataLaporan = FXCollections.observableArrayList();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String kode_laporan = jsonObject.getString("kode_laporan");
                        String kode_pertemuan = jsonObject.getString("kode_pertemuan");
                        String nim = jsonObject.getString("nim");
                        String statusabsen = jsonObject.getString("status");
                        dataLaporan.add(new Laporan(kode_laporan, kode_pertemuan, nim, statusabsen));
                    }
                    kodelaporan.setCellValueFactory(new PropertyValueFactory<>("kodeLaporan"));
                    kodepertemuanlaporan.setCellValueFactory(new PropertyValueFactory<>("kodePertemuan"));
                    nimlaporan.setCellValueFactory(new PropertyValueFactory<>("nim"));
                    statuslaporan.setCellValueFactory(new PropertyValueFactory<>("status"));
                    tabelLaporan.setItems(dataLaporan);
                }
                con.disconnect();
            } catch (Exception e) {
                Action.alerterror(StringVariable.ApiError);
            }
        }
    }

    public void nilai() {
        if (isTabelAbsenVisible) {
            tabelAbsen.setVisible(false);
            isTabelAbsenVisible = false;
        }
        if (isTabelLaporanVisible) {
            tabelLaporan.setVisible(false);
            isTabelLaporanVisible = false;
        }
        if (isTabelKeaktifanVisible) {
            tabelKeaktifan.setVisible(false);
            isTabelKeaktifanVisible = false;
        }

        if (!isTabelNilaiVisible) {
            tabelkonten.setVisible(true);
            tabelNilai.setVisible(true);
            isTabelNilaiVisible = true;
            String searchnim = nilai.getNim();
            try {
                URL url = new URL(ApiRoute.setGetNilaiByNim(searchnim));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(StringVariable.GET);
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
                    ObservableList<Nilai> dataNilai = FXCollections.observableArrayList();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String kode_nilai = jsonObject.getString("kode_nilai");
                        String kode_laporan = jsonObject.getString("kode_laporan");
                        String kode_pertemuan = jsonObject.getString("kode_pertemuan");
                        String nim = jsonObject.getString("nim");
                        String nilai = jsonObject.getString("nilai");
                        dataNilai.add(new Nilai(kode_nilai, kode_laporan, kode_pertemuan, nim, nilai));
                    }
                    kodenilai.setCellValueFactory(new PropertyValueFactory<>("kodeNilai"));
                    kodelaporannilai.setCellValueFactory(new PropertyValueFactory<>("kodeLaporan"));
                    kodepertemuannilai.setCellValueFactory(new PropertyValueFactory<>("kodePertemuan"));
                    nimnilai.setCellValueFactory(new PropertyValueFactory<>("nim"));
                    nilainilai.setCellValueFactory(new PropertyValueFactory<>("nilai"));
                    tabelNilai.setItems(dataNilai);
                }
                con.disconnect();
            } catch (Exception e) {
                Action.alerterror(StringVariable.ApiError);
            }
        }
    }

    public void keaktifan() {
        if (isTabelAbsenVisible) {
            tabelAbsen.setVisible(false);
            isTabelAbsenVisible = false;
        }
        if (isTabelLaporanVisible) {
            tabelLaporan.setVisible(false);
            isTabelLaporanVisible = false;
        }
        if (isTabelNilaiVisible) {
            tabelNilai.setVisible(false);
            isTabelNilaiVisible = false;
        }

        if (!isTabelKeaktifanVisible) {
            tabelkonten.setVisible(true);
            tabelKeaktifan.setVisible(true);
            isTabelKeaktifanVisible = true;
            String searchnim = keaktifan.getNim();
            try {
                URL url = new URL(ApiRoute.setGetKeaktifanByNim(searchnim));
                KeaktifanController.ExtractData(url, kodekeaktifan, kodepertemuankeaktifan, nimkeaktifan, keterangankeaktifan, tabelKeaktifan);
            } catch (Exception e) {
                Action.alerterror(StringVariable.ApiError);
            }

        }
    }
}
