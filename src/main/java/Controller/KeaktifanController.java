package Controller;

import Model.Keaktifan;
import Model.Mahasiswa;
import Model.Pertemuan;
import Project.Action;
import Project.ApiRoute;
import Project.StringVariable;
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
import org.json.JSONArray;
import org.json.JSONObject;

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
    @FXML
    public TableView<Keaktifan> tabelkeaktifan;
    @FXML
    public TableColumn<Model.Keaktifan, String> kodekeaktifan;
    @FXML
    public TableColumn<Model.Keaktifan, String> tabelnim;
    @FXML
    public TableColumn<Model.Keaktifan, String> tablenama;
    @FXML
    public TableColumn<Model.Keaktifan, String> kodepertemuan;
    @FXML
    public TableColumn<Model.Keaktifan, String> keterangan;
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
        InisiasiFieldNIM();
        InisiasiFieldPertemuan();
        setTabel();
    }

    public void InisiasiFieldNIM() {
        String apiUrl = ApiRoute.GetMahasiswa;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.GET);

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
            Action.alerterror(StringVariable.ExceptionE(String.valueOf(e)));
        }
    }

    public void InisiasiFieldPertemuan() {
        AbsensiController.getKodePertemuan(fieldpertemuan);
    }

    public void setTabel() {
        try {
            URL url = new URL(ApiRoute.GetKeaktifan);
            ExtractData(url, kodekeaktifan, kodepertemuan, tabelnim, keterangan, tabelkeaktifan);
            tablenama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        } catch (Exception e) {
            Action.alerterror(StringVariable.ExceptionE(String.valueOf(e)));
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
        if (!fieldkodekeaktifan.getText().isEmpty()) {
            Action.toasterror(StringVariable.GagalUpdate(StringVariable.Keaktifan) + "Bersihkan Field Terlebih Dahulu");
            return;
        }
        Mahasiswa selectedMahasiswa = fieldnim.getValue();
        Pertemuan selectedPertemuan = fieldpertemuan.getValue();
        if (selectedPertemuan != null) {
            if (!fieldketerangan.getText().isEmpty()) {
                String kodePertemuan = selectedPertemuan.getKode_pertemuan();
                String nimMahasiswa = selectedMahasiswa.getNim();
                String keterangan = fieldketerangan.getText();

                URL url = new URL(ApiRoute.StoreKeaktifan);

                int httpResponseCode = fetchApi(kodePertemuan, nimMahasiswa, keterangan, url);

                if (httpResponseCode == 201) {
                    Action.toastinfo(StringVariable.BerhasilTambah(StringVariable.Keaktifan));
                    clear();
                } else {
                    Action.toasterror(StringVariable.GagalTambah(StringVariable.Keaktifan));
                }
                setTabel();
            } else {
                Action.alerterror(StringVariable.EmptyForm);
            }
        } else {
            Action.alerterror(StringVariable.EmptyData(StringVariable.Pertemuan));
        }
    }

    public int fetchApi(String kodePertemuan, String nimMahasiswa, String keterangan, URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(StringVariable.POST);
        conn.setRequestProperty(StringVariable.ContentType, "application/json; utf-8");
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

    public void update() throws Exception {
        if (!fieldkodekeaktifan.getText().isEmpty()) {
            if (!fieldketerangan.getText().isEmpty()) {
                Mahasiswa selectedMahasiswa = fieldnim.getValue();
                Pertemuan selectedPertemuan = fieldpertemuan.getValue();
                String kodePertemuan = selectedPertemuan.getKode_pertemuan();
                String nimMahasiswa = selectedMahasiswa.getNim();
                String keterangan = fieldketerangan.getText();

                URL url = new URL(ApiRoute.setUpdateKeaktifan(fieldkodekeaktifan.getText()));
                int httpResponseCode = fetchApi(kodePertemuan, nimMahasiswa, keterangan, url);

                if (httpResponseCode == 201) {
                    Action.toastinfo(StringVariable.BerhasilUpdate(StringVariable.Keaktifan));
                    clear();
                } else {
                    Action.toasterror(StringVariable.GagalUpdate(StringVariable.Keaktifan));
                }
                setTabel();
            } else {
                Action.toasterror(StringVariable.EmptyForm);
            }
        } else {
            Action.alerterror(StringVariable.EmptyData(StringVariable.Mahasiswa));
        }
    }

    public void hapus() {
        if (!fieldkodekeaktifan.getText().isEmpty()) {
            boolean konfirmasi = Action.alertkonfir(StringVariable.DeleteData);

            if (konfirmasi) {
                try {
                    URL url = new URL(ApiRoute.setDeleteKeaktifan(fieldkodekeaktifan.getText()));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(StringVariable.POST);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 201) {
                        Action.toastinfo(StringVariable.BerhasilHapus(StringVariable.Keaktifan));
                        clear();
                    } else {
                        Action.toasterror(StringVariable.GagalHapus(StringVariable.Keaktifan));
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Action.toasterror(StringVariable.ApiError);
                }
                setTabel();
            }
        } else {
            Action.alerterror(StringVariable.EmptyData(StringVariable.Mahasiswa));
        }
    }


    public void clear() {
        fieldkodekeaktifan.clear();
        fieldketerangan.clear();
    }


}
