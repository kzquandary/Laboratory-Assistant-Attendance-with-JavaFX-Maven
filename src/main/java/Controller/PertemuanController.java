package Controller;

import Model.Pertemuan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import Project.Action;
import Project.ApiRoute;
import Project.StringVariable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class PertemuanController implements Initializable {
    public TextField fieldkodepertemuan;
    public TextField fieldjudulpertemuan;
    public DatePicker fieldtanggalpertemuan;
    @FXML
    private TableView<Pertemuan> tabelpertemuan;
    @FXML
    private TableColumn<Pertemuan, String> kodepertemuan;
    @FXML
    private TableColumn<Model.Pertemuan, String> tanggalpertemuan;
    @FXML
    private TableColumn<Model.Pertemuan, String> judulpertemuan;
    @FXML
    private Text warningTanggal;
    @FXML
    private Text warningJudul;

    public void setTabel() {
        try {
            URL url = new URL(ApiRoute.GetPertemuan);
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
                ObservableList<Pertemuan> dataPertemuan = FXCollections.observableArrayList();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String kode_pertemuan = jsonObject.getString("kode_pertemuan");
                    LocalDate tanggal_pertemuan = LocalDate.parse(jsonObject.getString("tanggal_pertemuan"));
                    String judul_pertemuan = jsonObject.getString("judul_pertemuan");
                    dataPertemuan.add(new Pertemuan(kode_pertemuan, tanggal_pertemuan, judul_pertemuan));
                }
                kodepertemuan.setCellValueFactory(new PropertyValueFactory<>("kode_pertemuan"));
                tanggalpertemuan.setCellValueFactory(new PropertyValueFactory<>("tanggal_pertemuan"));
                judulpertemuan.setCellValueFactory(new PropertyValueFactory<>("judul_pertemuan"));
                tabelpertemuan.setItems(dataPertemuan);
            }
            con.disconnect();
        } catch (ConnectException e) {
            Action.alerterror(StringVariable.ApiError);
        } catch (Exception e) {
            Action.alerterror(StringVariable.ExceptionE(String.valueOf(e)));
        }
        tabelpertemuan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fieldkodepertemuan.setText(newValue.getKode_pertemuan());
                fieldtanggalpertemuan.setValue(newValue.getTanggal_pertemuan());
                fieldjudulpertemuan.setText(newValue.getJudul_pertemuan());
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
        fieldjudulpertemuan.clear();
    }

    public void tambah() {
        if(!fieldkodepertemuan.getText().isEmpty()){
            Action.toasterror(StringVariable.GagalTambah(StringVariable.Pertemuan) + "Bersihkan Field Terlebih Dahulu");
            return;
        }
        if (fieldtanggalpertemuan.getValue() != null) {
            if(fieldtanggalpertemuan.getValue().isBefore(LocalDate.now())){
                Action.alerterror(StringVariable.FormatError);
                return;
            }
            warningTanggal.setVisible(false);
            warningJudul.setVisible(false);
            try {
                URL url = new URL(ApiRoute.StorePertemuan);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(StringVariable.POST);
                conn.setRequestProperty(StringVariable.ContentType, "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                LocalDate date = fieldtanggalpertemuan.getValue();
                DateTimeFormatter apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String apiDate = date.format(apiDateFormatter);
                String requestBody = String.format("{\"tanggal_pertemuan\":\"%s\", \"judul_pertemuan\":\"%s\"}", apiDate, fieldjudulpertemuan.getText());
                byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
                conn.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestBodyBytes, 0, requestBodyBytes.length);
                }

                int httpResponseCode = conn.getResponseCode();
                if (httpResponseCode == 201) {
                    Action.toastinfo(StringVariable.BerhasilTambah(StringVariable.Pertemuan));
                    clear();
                } else {
                    Action.toasterror(StringVariable.GagalTambah(StringVariable.Pertemuan));
                }
                setTabel();
            } catch (ConnectException e) {
                Action.alerterror(StringVariable.ApiError);
            } catch (Exception e) {
                Action.toasterror(StringVariable.ErrorTambah(StringVariable.Pertemuan));
            }
        } else {
            warningTanggal.setVisible(true);
            warningJudul.setVisible(true);
            Action.toasterror(StringVariable.FormatError);
        }
    }

    public void hapus() {
        if (!fieldkodepertemuan.getText().isEmpty()) {
            warningTanggal.setVisible(false);
            warningJudul.setVisible(false);

            boolean konfirmasi = Action.alertkonfir(StringVariable.DeleteData);

            if (konfirmasi) {
                try {
                    URL url = new URL(ApiRoute.setDeletePertemuan(fieldkodepertemuan.getText()));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(StringVariable.POST);
                    conn.connect();

                    int responseCode = conn.getResponseCode();

                    if (responseCode == 201) {
                        Action.toastinfo(StringVariable.BerhasilHapus(StringVariable.Pertemuan));
                        clear();
                    } else {
                        Action.toasterror(StringVariable.GagalHapus(StringVariable.Pertemuan));
                    }
                    conn.disconnect();
                    setTabel();
                } catch (ConnectException e) {
                    Action.alerterror(StringVariable.ApiError);
                } catch (Exception e) {
                    Action.toasterror(StringVariable.ErrorHapus(StringVariable.Pertemuan));
                }
            }
        } else {
            warningTanggal.setVisible(false);
            warningJudul.setVisible(false);
            Action.toasterror(StringVariable.PilihData(StringVariable.Pertemuan));
        }
    }


    public void update() {
        if (!fieldkodepertemuan.getText().isEmpty() && !fieldjudulpertemuan.getText().isEmpty() && fieldtanggalpertemuan.getValue() != null) {
            warningTanggal.setVisible(false);
            warningJudul.setVisible(false);
            try {
                URL url = new URL(ApiRoute.setUpdatePertemuan(fieldkodepertemuan.getText()));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(StringVariable.POST);
                conn.setRequestProperty(StringVariable.ContentType, "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                LocalDate date = fieldtanggalpertemuan.getValue();
                DateTimeFormatter apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String apiDate = date.format(apiDateFormatter);
                String requestBody = String.format("{\"kode_pertemuan\":\"%s\",\"tanggal_pertemuan\":\"%s\",\"judul_pertemuan\":\"%s\"}",
                        fieldkodepertemuan.getText(), apiDate, fieldjudulpertemuan.getText());
                byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
                conn.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestBodyBytes, 0, requestBodyBytes.length);
                }
                int responseCode = conn.getResponseCode();
                if (responseCode == 201) {
                    Action.toastinfo(StringVariable.BerhasilUpdate(StringVariable.Pertemuan));
                    clear();
                } else {
                    Action.alerterror(StringVariable.GagalUpdate(StringVariable.Pertemuan));
                }
                conn.disconnect();
                setTabel();
            } catch (ConnectException e) {
                Action.toasterror(StringVariable.ApiError);
            } catch (Exception e) {
                Action.toasterror(StringVariable.ErrorUpdate(StringVariable.Pertemuan));
            }
        } else {
            warningTanggal.setVisible(true);
            warningJudul.setVisible(true);
            Action.toasterror(StringVariable.DataFormatError);
        }
    }
}
