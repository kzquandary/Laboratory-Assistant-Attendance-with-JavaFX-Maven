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
import Project.Action;
import Project.ApiRoute;
import Project.StringVariable;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class AbsensiController implements Initializable {
    @FXML
    public ChoiceBox<Pertemuan> kodepertemuan;
    @FXML
    public Text textkodepertemuan;
    private final List<ToggleGroup> toggleGroups = new ArrayList<>();
    public Pagination pagination;
    public Pane contentPane = new Pane();
    private final Map<Integer, Map<String, String>> radioButtonStatusMap = new HashMap<>();
    private final Map<String, String> tempMhs = new HashMap<>();
    private final Map<String, String> statusMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initPertemuan();
        kodepertemuan.getItems().add(0, new Pertemuan("Pilih Pertemuan", null));
        kodepertemuan.getSelectionModel().selectFirst();
        kodepertemuan.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.getKode_pertemuan().equals("Pilih Pertemuan")) {
                    pagination.setVisible(false);
                    textkodepertemuan.setText("");
                    tempMhs.clear();
                } else {
                    initializeDataMahasiswa(newValue);
                }
            }
        });


    }

    private void initializeDataMahasiswa(Pertemuan selectedPertemuan) {
        pagination.setVisible(true);
        textkodepertemuan.setText("Kode Pertemuan : " + selectedPertemuan.getKode_pertemuan());

        final double[] layoutY = {10};
        double radioButtonLayoutX = 200;
        double textLayout = 10;

        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        JSONArray dataMahasiswa = getAbsensiData(kodePertemuan);
        for (int i = 0; i < dataMahasiswa.length(); i++) {
            JSONObject dataMhs = dataMahasiswa.getJSONObject(i);
            tempMhs.put(dataMhs.getString("kode_absen"), dataMhs.getString("status"));
        }
        int mahasiswaPerHalaman = 16;
        int jumlahHalaman = (int) Math.ceil((double) dataMahasiswa.length() / mahasiswaPerHalaman);
        pagination.setPageCount(jumlahHalaman);

        pagination.setPageFactory(pageIndex -> {
            if (pageIndex >= 0 && pageIndex < pagination.getPageCount()) {
                int halaman = pageIndex;
                int awal = halaman * mahasiswaPerHalaman;
                int akhir = Math.min(awal + mahasiswaPerHalaman, dataMahasiswa.length());

                contentPane.getChildren().clear();
                toggleGroups.clear();
                layoutY[0] = 10;

//                Map<String, String> statusMap = radioButtonStatusMap.getOrDefault(pageIndex, new HashMap<>());

                for (int i = awal; i < akhir; i++) {
                    JSONObject mahasiswaObj = dataMahasiswa.getJSONObject(i);
                    String namaMahasiswa = mahasiswaObj.getString("nama");
                    String kodeabsensi = mahasiswaObj.getString("kode_absen");
                    String statusKehadiran = statusMap.getOrDefault(mahasiswaObj.getString("kode_absen"), tempMhs.get(kodeabsensi));

                    Text namaText = new Text(namaMahasiswa);
                    namaText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
                    namaText.setTextAlignment(TextAlignment.RIGHT);
                    namaText.setLayoutY(layoutY[0] + 12);
                    namaText.setLayoutX(textLayout);
                    namaText.setFill(Color.WHITE);

                    ToggleGroup toggleGroup = new ToggleGroup();
                    toggleGroups.add(toggleGroup);

                    RadioButton hadirRadioButton = new RadioButton("Hadir");
                    RadioButton izinRadioButton = new RadioButton("Izin");
                    RadioButton alphaRadioButton = new RadioButton("Alpha");

                    hadirRadioButton.setToggleGroup(toggleGroup);
                    izinRadioButton.setToggleGroup(toggleGroup);
                    alphaRadioButton.setToggleGroup(toggleGroup);
                    hadirRadioButton.setId(kodeabsensi);
                    izinRadioButton.setId(kodeabsensi);
                    alphaRadioButton.setId(kodeabsensi);

                    Toggle selectedToggle = null;
                    if (statusKehadiran.equalsIgnoreCase("Hadir")) {
                        selectedToggle = hadirRadioButton;
                    } else if (statusKehadiran.equalsIgnoreCase("Izin")) {
                        selectedToggle = izinRadioButton;
                    } else if (statusKehadiran.equalsIgnoreCase("Alpha")) {
                        selectedToggle = alphaRadioButton;
                    }

                    hadirRadioButton.setOnAction(event -> {
                        statusMap.put(kodeabsensi, "Hadir");
                        tempMhs.put(kodeabsensi, "Hadir");
                    });
                    izinRadioButton.setOnAction(event -> {
                        statusMap.put(kodeabsensi, "Izin");
                        tempMhs.put(kodeabsensi, "Izin");
                    });
                    alphaRadioButton.setOnAction(event -> {
                        statusMap.put(kodeabsensi, "Alpha");
                        tempMhs.put(kodeabsensi, "Alpha");
                    });

                    toggleGroup.selectToggle(selectedToggle);


                    hadirRadioButton.setLayoutX(radioButtonLayoutX);
                    izinRadioButton.setLayoutX(radioButtonLayoutX + 60);
                    alphaRadioButton.setLayoutX(radioButtonLayoutX + 120);
                    hadirRadioButton.setLayoutY(layoutY[0]);
                    izinRadioButton.setLayoutY(layoutY[0]);
                    alphaRadioButton.setLayoutY(layoutY[0]);

                    hadirRadioButton.setTextFill(Color.WHITE);
                    izinRadioButton.setTextFill(Color.WHITE);
                    alphaRadioButton.setTextFill(Color.WHITE);
                    contentPane.getChildren().addAll(namaText, hadirRadioButton, izinRadioButton, alphaRadioButton);

                    layoutY[0] += 30;
                }

                radioButtonStatusMap.put(pageIndex, statusMap);

                return contentPane;
            } else {
                return null;
            }
        });
    }


    private JSONArray getAbsensiData(String kodePertemuan) {
        String apiUrl = ApiRoute.setGetAbsensiById(kodePertemuan);

        return Action.getObjects(apiUrl);
    }

    public void initPertemuan() {
        getKodePertemuan(kodepertemuan);
    }

    public static void getKodePertemuan(ChoiceBox<Pertemuan> kodepertemuan) {
        Action.GetKodePertemuan(kodepertemuan);
    }

    public void setAllHadir() {
        for (int pageIndex = 0; pageIndex < pagination.getPageCount(); pageIndex++) {
            Map<String, String> statusMap = radioButtonStatusMap.getOrDefault(pageIndex, new HashMap<>());

            for (ToggleGroup toggleGroup : toggleGroups) {
                for (Toggle toggle : toggleGroup.getToggles()) {
                    if (toggle instanceof RadioButton radioButton) {
                        String kodeabsensi = radioButton.getId();
                        if (!statusMap.getOrDefault(kodeabsensi, "").equalsIgnoreCase("Hadir")) {
                            statusMap.put(kodeabsensi, "Hadir");
                            tempMhs.put(kodeabsensi, "Hadir");
                            radioButton.setSelected(true);
                        }
                    }
                }
            }
            radioButtonStatusMap.put(pageIndex, statusMap);
        }
    }


    public void submit() {
        if (!tempMhs.isEmpty()) {
            try {
                URL url = new URL(ApiRoute.UpdateAbsensi);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(StringVariable.POST);
                connection.setRequestProperty(StringVariable.ContentType, "application/x-www-form-urlencoded");

                StringBuilder requestBody = new StringBuilder();
                for (Map.Entry<String, String> entry : tempMhs.entrySet()) {
                    String kodeabsensi = entry.getKey();
                    String toggleValue = entry.getValue();

                    requestBody.append(URLEncoder.encode(kodeabsensi, StandardCharsets.UTF_8))
                            .append("=")
                            .append(URLEncoder.encode(toggleValue, StandardCharsets.UTF_8))
                            .append("&");
                }

                connection.setDoOutput(true);
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(requestBody.toString());
                outputStream.flush();
                outputStream.close();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    Action.toastinfo(StringVariable.BerhasilUpdate(StringVariable.Absensi));
                } else {
                    Action.toasterror(StringVariable.GagalUpdate(StringVariable.Absensi));
                }
                connection.disconnect();
            } catch (Exception e) {
                Action.toasterror(StringVariable.ApiError);
            }
        } else {
            Action.alerterror(StringVariable.EmptyData(StringVariable.Pertemuan));
        }
    }

}
