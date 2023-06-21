package Controller;

import Model.Pertemuan;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class NilaiController implements Initializable {
    @FXML
    public ChoiceBox<Pertemuan> kodepertemuan;
    @FXML
    public Text textkodepertemuan;
    public Pagination pagination;
    public Pane contentPane = new Pane();
    private final Map<Integer, Map<String, String>> nilaiFieldMap = new HashMap<>();
    private final Map<String, String> tempMhs = new HashMap<>();

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
        double textLayout = 10;
        double formLayoutX = 180;

        String kodePertemuan = selectedPertemuan.getKode_pertemuan();
        JSONArray dataMahasiswa = getAbsensiData(kodePertemuan);
        for (int i = 0; i < dataMahasiswa.length(); i++) {
            JSONObject dataMhs = dataMahasiswa.getJSONObject(i);
            tempMhs.put(dataMhs.getString("kode_nilai"), String.valueOf(dataMhs.getString("nilai")));
        }
        int mahasiswaPerHalaman = 12;
        int jumlahHalaman = (int) Math.ceil((double) dataMahasiswa.length() / mahasiswaPerHalaman);
        pagination.setPageCount(jumlahHalaman);

        pagination.setPageFactory(pageIndex -> {
            if (pageIndex >= 0 && pageIndex < pagination.getPageCount()) {
                int halaman = pageIndex;
                int awal = halaman * mahasiswaPerHalaman;
                int akhir = Math.min(awal + mahasiswaPerHalaman, dataMahasiswa.length());

                contentPane.getChildren().clear();
                layoutY[0] = 10;

                Map<String, String> nilaiMap = nilaiFieldMap.getOrDefault(pageIndex, new HashMap<>());

                for (int i = awal; i < akhir; i++) {
                    JSONObject mahasiswaObj = dataMahasiswa.getJSONObject(i);
                    String namaMahasiswa = mahasiswaObj.getString("nama");
                    String kodenilai = mahasiswaObj.getString("kode_nilai");

                    Text namaText = new Text(namaMahasiswa);
                    namaText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
                    namaText.setTextAlignment(TextAlignment.RIGHT);
                    namaText.setLayoutY(layoutY[0] + 24);
                    namaText.setLayoutX(textLayout);
                    namaText.setFill(Color.WHITE);

                    TextField nilaiField = new TextField(nilaiMap.getOrDefault(kodenilai, String.valueOf(mahasiswaObj.getInt("nilai"))));
                    nilaiField.setPrefWidth(250);
                    nilaiField.setPrefHeight(20);
                    nilaiField.setLayoutX(formLayoutX);
                    nilaiField.setLayoutY(layoutY[0]);
                    nilaiField.setId(kodenilai);
                    nilaiField.setOnKeyReleased(keyEvent -> {
                                try {
                                    String input = nilaiField.getText();
                                    if (!input.matches("\\d*")) {
                                        nilaiField.setText("0");
                                        tempMhs.put(kodenilai, null);
                                        Action.alerterror(StringVariable.FormatError);
                                    } else {
                                        int nilai = Integer.parseInt(input);
                                        if (nilai > 100) {
                                            nilaiField.setText("100");
                                        } else if (nilai < 0) {
                                            nilaiField.setText("0");
                                        }

                                        nilaiMap.put(kodenilai, nilaiField.getText());
                                        tempMhs.put(kodenilai, nilaiField.getText());
                                    }
                                } catch (NumberFormatException e) {
                                    tempMhs.put(kodenilai, null);
                                }
                            }
                    );

                    contentPane.getChildren().addAll(namaText, nilaiField);

                    layoutY[0] += 30;
                }

                nilaiFieldMap.put(pageIndex, nilaiMap);

                return contentPane;
            } else {
                return null;
            }
        });
    }

    private JSONArray getAbsensiData(String kodePertemuan) {
        String apiUrl = ApiRoute.setGetNilaiById(kodePertemuan);

        return Action.getObjects(apiUrl);
    }

    public void initPertemuan() {
        getKodePertemuan(kodepertemuan);
    }

    public static void getKodePertemuan(ChoiceBox<Pertemuan> kodepertemuan) {
        Action.GetKodePertemuan(kodepertemuan);
    }



    public void submit() {
        if (!tempMhs.isEmpty()) {
            boolean hasNullValues = false;
            for (Map.Entry<String, String> entry : tempMhs.entrySet()) {
                if (entry.getValue() == null) {
                    hasNullValues = true;
                    break;
                }
            }
            if (hasNullValues) {
                Action.toasterror(StringVariable.FormatError);
            } else {
                try {
                    URL url = new URL(ApiRoute.UpdateNilai);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(StringVariable.POST);
                    connection.setRequestProperty(StringVariable.ContentType, "application/x-www-form-urlencoded");

                    StringBuilder requestBody = new StringBuilder();
                    for (Map.Entry<String, String> entry : tempMhs.entrySet()) {
                        String kodeNilai = entry.getKey();
                        String nilai = entry.getValue();

                        // Skip null values
                        if (nilai != null) {
                            requestBody.append(kodeNilai).append("=").append(nilai).append("&");
                        }
                    }

                    connection.setDoOutput(true);
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(requestBody.toString());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        Action.toastinfo(StringVariable.BerhasilUpdate(StringVariable.Nilai));
                    } else {
                        Action.toasterror(StringVariable.GagalUpdate(StringVariable.Nilai));                    }
                    connection.disconnect();
                } catch (Exception e) {
                    Action.toasterror(StringVariable.EmptyData(StringVariable.Pertemuan));
                }
            }
        } else {
            Action.toasterror(StringVariable.EmptyData(StringVariable.Pertemuan));
        }
    }

}
