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
import project.Action;
import project.ApiRoute;
import project.StringVariable;

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

    public DatePicker fieldtanggalpertemuan;
    @FXML
    private TableView<Pertemuan> tabelpertemuan;
    @FXML
    private TableColumn<Pertemuan, String> kodepertemuan;
    @FXML
    private TableColumn<Model.Pertemuan, String> tanggalpertemuan;
    @FXML
    private Text warningTanggal;

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
                    dataPertemuan.add(new Pertemuan(kode_pertemuan, tanggal_pertemuan));
                }
                kodepertemuan.setCellValueFactory(new PropertyValueFactory<>("kode_pertemuan"));
                tanggalpertemuan.setCellValueFactory(new PropertyValueFactory<>("tanggal_pertemuan"));
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

    public void tambah() {
        if (fieldtanggalpertemuan.getValue() != null) {
            if(fieldtanggalpertemuan.getValue().isBefore(LocalDate.now())){
                Action.alerterror("Harap masukan tanggal yang benar");
                return;
            }
            warningTanggal.setVisible(false);
            try {
                URL url = new URL(ApiRoute.StorePertemuan);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(StringVariable.POST);
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                LocalDate date = fieldtanggalpertemuan.getValue();
                DateTimeFormatter apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String apiDate = date.format(apiDateFormatter);
                String requestBody = String.format("{\"tanggal_pertemuan\":\"%s\"}", apiDate);
                byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
                conn.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestBodyBytes, 0, requestBodyBytes.length);
                }

                int httpResponseCode = conn.getResponseCode();
                if (httpResponseCode == 201) {
                    Action.alertinfo(StringVariable.BerhasilTambah(StringVariable.Pertemuan));
                } else {
                    Action.alerterror(StringVariable.GagalTambah(StringVariable.Pertemuan));
                }
                setTabel();
            } catch (ConnectException e) {
                Action.alerterror(StringVariable.ApiError);
            } catch (Exception e) {
                Action.alerterror(StringVariable.ErrorTambah(StringVariable.Pertemuan));
            }
        } else {
            warningTanggal.setVisible(true);
            Action.alerterror(StringVariable.FormatError);
        }
    }

    public void hapus() {
        if (!fieldkodepertemuan.getText().isEmpty()) {
            warningTanggal.setVisible(false);
            try {
                URL url = new URL(ApiRoute.setDeletePertemuan(fieldkodepertemuan.getText()));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(StringVariable.POST);
                conn.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                int responseCode = conn.getResponseCode();

                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                String message = jsonObject.getString("message");

                System.out.println(message);
                if (responseCode == 201) {
                    Action.alertinfo(StringVariable.BerhasilHapus(StringVariable.Pertemuan));
                } else {
                    Action.alerterror(StringVariable.GagalHapus(StringVariable.Pertemuan));
                }
                conn.disconnect();
                setTabel();
            } catch (ConnectException e) {
                Action.alerterror(StringVariable.ApiError);
            } catch (Exception e) {
                Action.alerterror(StringVariable.ErrorHapus(StringVariable.Pertemuan));
            }
        } else {
            warningTanggal.setVisible(false);
            Action.alerterror(StringVariable.PilihData(StringVariable.Pertemuan));
        }
    }

    public void update() {
        if (!fieldkodepertemuan.getText().isEmpty() && fieldtanggalpertemuan.getValue() != null) {
            warningTanggal.setVisible(false);
            try {
                URL url = new URL(ApiRoute.setUpdatePertemuan(fieldkodepertemuan.getText()));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(StringVariable.POST);
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
                    Action.alertinfo(StringVariable.BerhasilUpdate(StringVariable.Pertemuan));
                } else {
                    Action.alerterror(StringVariable.GagalUpdate(StringVariable.Pertemuan));
                }
                conn.disconnect();
                setTabel();
            } catch (ConnectException e) {
                Action.alerterror(StringVariable.ApiError);
            } catch (Exception e) {
                Action.alerterror(StringVariable.ErrorUpdate(StringVariable.Pertemuan));
            }
        } else {
            warningTanggal.setVisible(true);
            Action.alerterror(StringVariable.DataFormatError);
        }
    }
}
