package Controller;

import Model.Pertemuan;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.json.JSONArray;
import org.json.JSONObject;
import project.Route;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class LaporanController implements Initializable {
    @FXML
    public ChoiceBox<Pertemuan> kodepertemuan;
    @FXML
    public Text textkodepertemuan;
    private final List<ToggleGroup> toggleGroups = new ArrayList<>();
    public Pane contentPane = new Pane();
    private final Map<Integer, Map<String, String>> radioButtonStatusMap = new HashMap<>();
    public Map<String, String> tempMhs = new HashMap<>();
    public Pagination pagination;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initPertemuan();
        kodepertemuan.getItems().add(0, new Pertemuan("Pilih Pertemuan", null));
        kodepertemuan.getSelectionModel().selectFirst();
        kodepertemuan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                initializeDataMahasiswa(newValue);
            }
        });

    }
    private void initializeDataMahasiswa(Pertemuan selectedPertemuan) {
        pagination.setVisible(true);
        textkodepertemuan.setText("Kode Pertemuan : " + selectedPertemuan.getKode_pertemuan());
        final double[] layoutY = {10};
        double radioButtonLayoutX = 140;
        double textLayout = 5;

        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        JSONArray dataMahasiswa = getLaporanData(kodePertemuan);
        for (int i = 0; i < dataMahasiswa.length(); i++) {
            JSONObject dataMhs = dataMahasiswa.getJSONObject(i);
            tempMhs.put(dataMhs.getString("kode_laporan"), dataMhs.getString("status"));
        }
        int mahasiswaPerHalaman = 16;
        int jumlahHalaman = (int) Math.ceil((double) dataMahasiswa.length() / mahasiswaPerHalaman);
        pagination.setPageCount(jumlahHalaman);

        toggleGroups.clear();
        pagination.setPageFactory(pageIndex -> {
            if (pageIndex >= 0 && pageIndex < pagination.getPageCount()) {
                int halaman = pageIndex;
                int awal = halaman * mahasiswaPerHalaman;
                int akhir = Math.min(awal + mahasiswaPerHalaman, dataMahasiswa.length());

                contentPane.getChildren().clear();
                toggleGroups.clear();
                layoutY[0] = 10;

                Map<String, String> statusMap = radioButtonStatusMap.getOrDefault(pageIndex, new HashMap<>());

                for (int i = awal; i < akhir; i++) {
                    JSONObject mahasiswaObj = dataMahasiswa.getJSONObject(i);
                    String namaMahasiswa = mahasiswaObj.getString("nama");
                    String statusLaporan = statusMap.getOrDefault(mahasiswaObj.getString("kode_laporan"),"Telat");
                    String kodeLaporan = mahasiswaObj.getString("kode_laporan");

                    Text namaText = new Text(namaMahasiswa);
                    namaText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
                    namaText.setTextAlignment(TextAlignment.RIGHT);
                    namaText.setLayoutY(layoutY[0] + 12);
                    namaText.setLayoutX(textLayout);
                    namaText.setFill(Color.WHITE);

                    ToggleGroup toggleGroup = new ToggleGroup();
                    toggleGroups.add(toggleGroup);

                    RadioButton mengumpulkanRadioButton = new RadioButton("Mengumpulkan");
                    RadioButton telatRadioButton = new RadioButton("Telat");
                    RadioButton tidakMengumpulkanRadioButton = new RadioButton("Tidak Mengumpulkan");

                    mengumpulkanRadioButton.setToggleGroup(toggleGroup);
                    telatRadioButton.setToggleGroup(toggleGroup);
                    tidakMengumpulkanRadioButton.setToggleGroup(toggleGroup);
                    mengumpulkanRadioButton.setId(kodeLaporan);
                    telatRadioButton.setId(kodeLaporan);
                    tidakMengumpulkanRadioButton.setId(kodeLaporan);

                    Toggle selectedToggle = null;
                    if (statusLaporan.equalsIgnoreCase("Mengumpulkan")) {
                        selectedToggle = mengumpulkanRadioButton;
                    } else if (statusLaporan.equalsIgnoreCase("Telat")) {
                        selectedToggle = telatRadioButton;
                    } else if (statusLaporan.equalsIgnoreCase("Tidak Mengumpulkan")) {
                        selectedToggle = tidakMengumpulkanRadioButton;
                    }

                    mengumpulkanRadioButton.setOnAction(event -> {
                        statusMap.put(kodeLaporan, "Mengumpulkan");
                        tempMhs.put(kodeLaporan, "Mengumpulkan");
                    });
                    telatRadioButton.setOnAction(event -> {
                        statusMap.put(kodeLaporan, "Telat");
                        tempMhs.put(kodeLaporan, "Telat");
                    });
                    tidakMengumpulkanRadioButton.setOnAction(event -> {
                        statusMap.put(kodeLaporan, "Tidak Mengumpulkan");
                        tempMhs.put(kodeLaporan, "Tidak Mengumpulkan");
                    });

                    toggleGroup.selectToggle(selectedToggle);


                    mengumpulkanRadioButton.setLayoutX(radioButtonLayoutX);
                    telatRadioButton.setLayoutX(radioButtonLayoutX + 120);
                    tidakMengumpulkanRadioButton.setLayoutX(radioButtonLayoutX + 180);
                    mengumpulkanRadioButton.setLayoutY(layoutY[0]);
                    telatRadioButton.setLayoutY(layoutY[0]);
                    tidakMengumpulkanRadioButton.setLayoutY(layoutY[0]);

                    mengumpulkanRadioButton.setTextFill(Color.WHITE);
                    telatRadioButton.setTextFill(Color.WHITE);
                    tidakMengumpulkanRadioButton.setTextFill(Color.WHITE);
                    contentPane.getChildren().addAll(namaText, mengumpulkanRadioButton, telatRadioButton, tidakMengumpulkanRadioButton);

                    layoutY[0] += 30;
                }

                radioButtonStatusMap.put(pageIndex, statusMap);

                return contentPane;
            } else {
                return null;
            }
        });
    }

    private JSONArray getLaporanData(String kodePertemuan) {
        String apiUrl = Route.URL + "laporan/" + kodePertemuan;

        return getObjects(apiUrl);
    }

    static JSONArray getObjects(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.lines().collect(Collectors.joining());

                return new JSONArray(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    public void initPertemuan() {
        getKodePertemuan(kodepertemuan);
    }

    public static void getKodePertemuan(ChoiceBox<Pertemuan> kodepertemuan) {
        NilaiController.GetKodePertemuan(kodepertemuan);
    }

    @FXML
    public void setAllHadir() {
        for (int pageIndex = 0; pageIndex < pagination.getPageCount(); pageIndex++) {
            Map<String, String> statusMap = radioButtonStatusMap.getOrDefault(pageIndex, new HashMap<>());

            for (ToggleGroup toggleGroup : toggleGroups) {
                for (Toggle toggle : toggleGroup.getToggles()) {
                    if (toggle instanceof RadioButton radioButton) {
                        String kodelaporan = radioButton.getId();
                        if (!statusMap.getOrDefault(kodelaporan, "").equalsIgnoreCase("Mengumpulkan")) {
                            statusMap.put(kodelaporan, "Mengumpulkan");
                            tempMhs.put(kodelaporan, "Mengumpulkan");
                            radioButton.setSelected(true);
                        }
                    }
                }
            }
            radioButtonStatusMap.put(pageIndex, statusMap);
        }
    }
    public void submit() {
        try {
            // Create the HTTP connection
            URL url = new URL(Route.URL + "laporan/update");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Build the request body
            StringBuilder requestBody = new StringBuilder();
            for (Map.Entry<String, String> entry : tempMhs.entrySet()) {
                String kodelaporan = entry.getKey();
                String toggleValue = entry.getValue();

                requestBody.append(URLEncoder.encode(kodelaporan, StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(toggleValue, StandardCharsets.UTF_8))
                        .append("&");
            }

            // Send the request body
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody.toString());
            outputStream.flush();
            outputStream.close();

            // Get the response
            int responseCode = connection.getResponseCode();

            Alert alert;
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informasi");
                alert.setHeaderText(null);
                alert.setContentText("Laporan Berhasil Diupdate");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Gagal Mengupdate Laporan");
            }
            alert.showAndWait();

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
