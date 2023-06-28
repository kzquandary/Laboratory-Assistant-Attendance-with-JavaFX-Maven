package Controller;

import Model.Mahasiswa;
import Project.Action;
import Project.ApiRoute;
import Project.StringVariable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class MahasiswaController implements Initializable {
    public TextField fieldnim;

    public TextField fieldnama;

    public TextField fieldnohp;
    @FXML
    private TableView<Model.Mahasiswa> tabelmahasiswa;
    @FXML
    private TableColumn<Model.Mahasiswa, String> nim;
    @FXML
    private TableColumn<Model.Mahasiswa, String> nama;
    @FXML
    private TableColumn<Model.Mahasiswa, String> nohp;
    @FXML
    private Text warningNAMA;

    @FXML
    private Text warningNIM;
    @FXML
    private Text warningNoHP;
    @FXML
    private Text emptyNAMA;
    @FXML
    private Text emptyNIM;
    @FXML
    private Text emptyNoHP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTabel();
    }

    public void setTabel() {
        try {
            URL url = new URL(ApiRoute.GetMahasiswa);
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
            Action.alerterror(StringVariable.ApiError);
        }
        tabelmahasiswa.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fieldnim.setText(newValue.getNim());
                fieldnama.setText(newValue.getNama());
                fieldnohp.setText(newValue.getNohp());
            }
        });
    }

    public void clearmhs() {
        fieldnim.clear();
        fieldnama.clear();
        fieldnohp.clear();
    }

    public void tambah() throws Exception {
        if (checkEmpty()) {
            if (validasiMahasiswa()) {
                URL url = new URL(ApiRoute.StoreMahasiswa);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(StringVariable.POST);
                conn.setRequestProperty(StringVariable.ContentType, "application/json; utf-8");
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
                    Action.toastinfo(StringVariable.BerhasilTambah(StringVariable.Mahasiswa));
                    clearmhs();
                } else {
                    Action.toasterror(StringVariable.GagalTambah(StringVariable.Mahasiswa));
                }
                setTabel();
            } else {
                emptyNIM.setVisible(false);
                emptyNAMA.setVisible(false);
                emptyNoHP.setVisible(false);
                Action.toasterror(StringVariable.FormatError);
            }
        } else {
            warningNIM.setVisible(false);
            warningNAMA.setVisible(false);
            warningNoHP.setVisible(false);
            emptyNIM.setVisible(fieldnim.getText().isEmpty());
            emptyNAMA.setVisible(fieldnama.getText().isEmpty());
            emptyNoHP.setVisible(fieldnohp.getText().isEmpty());
            Action.toasterror(StringVariable.EmptyForm);
        }
    }

    public void hapus() {
        if (!fieldnim.getText().isEmpty()) {
            boolean confirmed = Action.alertkonfir(StringVariable.DeleteData);
            if (confirmed) {
                try {
                    URL url = new URL(ApiRoute.setDeleteMahasiswa(fieldnim.getText()));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(StringVariable.POST);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 201) {
                        Action.toastinfo(StringVariable.BerhasilHapus(StringVariable.Mahasiswa));
                        clearmhs();
                    } else {
                        Action.toasterror(StringVariable.GagalHapus(StringVariable.Mahasiswa));
                    }
                    conn.disconnect();
                } catch (IOException e) {
                    Action.toasterror(StringVariable.ErrorHapus(StringVariable.Mahasiswa));
                }
                setTabel();
            }
        } else {
            emptyNIM.setVisible(true);
            emptyNAMA.setVisible(false);
            emptyNoHP.setVisible(false);
            warningNIM.setVisible(false);
            warningNAMA.setVisible(false);
            warningNoHP.setVisible(false);
            Action.alerterror(StringVariable.EmptyForm);
        }
    }


    public void update() {
        if (checkEmpty()) {
            if (validasiMahasiswa()) {
                try {
                    URL url = new URL(ApiRoute.setUpdateMahasiswa(fieldnim.getText()));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod(StringVariable.POST);
                    conn.setRequestProperty(StringVariable.ContentType, "application/json; utf-8");
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
                        Action.toastinfo(StringVariable.BerhasilUpdate(StringVariable.Mahasiswa));
                        clearmhs();
                    } else {
                        Action.toasterror(StringVariable.GagalUpdate(StringVariable.Mahasiswa));
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Action.toasterror(StringVariable.GagalUpdate(StringVariable.Mahasiswa));
                }
                setTabel();
            } else {
                emptyNIM.setVisible(false);
                emptyNAMA.setVisible(false);
                emptyNoHP.setVisible(false);
                Action.toasterror(StringVariable.FormatError);
            }
        } else {
            warningNIM.setVisible(false);
            warningNAMA.setVisible(false);
            warningNoHP.setVisible(false);
            emptyNIM.setVisible(fieldnim.getText().isEmpty());
            emptyNAMA.setVisible(fieldnama.getText().isEmpty());
            emptyNoHP.setVisible(fieldnohp.getText().isEmpty());
            Action.toasterror(StringVariable.EmptyForm);
        }
    }
    public void pesantugas() {
        String nim = fieldnim.getText();
        String nama = fieldnama.getText();
        String noHp = fieldnohp.getText();
        if (nim.isEmpty() || nama.isEmpty() || noHp.isEmpty()) {
            Action.alerterror(StringVariable.EmptyData(StringVariable.Mahasiswa));
            return;
        }

        try {
            String apiUrl = ApiRoute.CheckMhs + URLEncoder.encode(nim, StandardCharsets.UTF_8);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.GET);

            int responseCode = connection.getResponseCode();

            if (responseCode == 201) {
                String encodedMessage = URLEncoder.encode("Halo " + capitalizeName(nama) + ", Silahkan Kumpulkan Tugas Praktikum Pemrograman Objek 2, Karena Tengat Waktu Sudah Dekat", StandardCharsets.UTF_8);
                String formattedNoHp = formatPhoneNumber(noHp);
                String whatsappUrl = "https://wa.me/" + formattedNoHp + "?text=" + encodedMessage;
                Desktop.getDesktop().browse(new URI(whatsappUrl));
            } else {
                Action.alerterror(StringVariable.Error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String capitalizeName(String name) {
        String[] words = name.split(" ");
        StringBuilder capitalizedString = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                capitalizedString.append(capitalizedWord).append(" ");
            }
        }

        return capitalizedString.toString().trim();
    }
    public String formatPhoneNumber(String noHp) {
        if (noHp.startsWith("08")) {
            noHp = "628" + noHp.substring(2);
        }
        return noHp;
    }

    public boolean validasiMahasiswa() {
        boolean validNIM = Pattern.matches(StringVariable.RegNIM, fieldnim.getText());
        boolean validNama = Pattern.matches(StringVariable.RegNama, fieldnama.getText());
        boolean validNoHP = Pattern.matches(StringVariable.RegNoHP, fieldnohp.getText());
        warningNIM.setVisible(!validNIM);
        warningNAMA.setVisible(!validNama);
        warningNoHP.setVisible(!validNoHP);

        return validNIM && validNama && validNoHP;
    }

    public boolean checkEmpty() {
        return !fieldnim.getText().isEmpty() && !fieldnama.getText().isEmpty() && !fieldnohp.getText().isEmpty();
    }

}
