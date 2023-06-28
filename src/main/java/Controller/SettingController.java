package Controller;

import Project.Action;
import Project.ApiRoute;
import Project.StringVariable;
import Project.TempVariable;
import com.swardana.materialiconfx.control.MaterialIcon;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

public class SettingController implements Initializable {
    @FXML
    private PasswordField field_confirmation_for_password;

    @FXML
    private PasswordField field_newpassword_for_password;

    @FXML
    private PasswordField field_oldpassword_for_password;

    @FXML
    private PasswordField field_password_for_username;

    @FXML
    private TextField field_username_for_username;

    @FXML
    private Text label_confirmation_for_password;

    @FXML
    private Text label_newpassword_for_password;

    @FXML
    private Text label_oldpassword_for_password;

    @FXML
    private Text label_oldpassword_for_username;

    @FXML
    private Text label_username_for_username;

    @FXML
    private Button submit_for_password;

    @FXML
    private Button submit_for_username;
    @FXML
    private MaterialIcon tombol_kembali;
    @FXML
    private Button button_ganti_password;

    @FXML
    private Button button_ganti_username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setPasswordStatus(Boolean status) {
        tombol_kembali.setVisible(status);
        field_confirmation_for_password.setVisible(status);
        field_newpassword_for_password.setVisible(status);
        field_oldpassword_for_password.setVisible(status);
        label_confirmation_for_password.setVisible(status);
        label_newpassword_for_password.setVisible(status);
        label_oldpassword_for_password.setVisible(status);
        submit_for_password.setVisible(status);
    }

    public void setUsernameStatus(Boolean status) {
        tombol_kembali.setVisible(status);
        label_username_for_username.setVisible(status);
        field_username_for_username.setVisible(status);
        label_oldpassword_for_username.setVisible(status);
        field_password_for_username.setVisible(status);
        submit_for_username.setVisible(status);
    }

    public void setMenuStatus(Boolean status) {
        button_ganti_password.setVisible(status);
        button_ganti_username.setVisible(status);
    }

    @FXML
    private void Upload() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("File CSV atau Excel", "*.csv", "*.xlsx");
        fileChooser.getExtensionFilters().add(allFilter);

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            if (selectedFile.getName().endsWith(".csv")) {
                try (Scanner scanner = new Scanner(selectedFile)) {
                    JSONArray jsonArray = new JSONArray();
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        jsonArray.put(parseLineToJson(line));
                    }
                    sendBatchData(jsonArray);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (selectedFile.getName().endsWith(".xlsx")) {
                try (Workbook workbook = new XSSFWorkbook(selectedFile)) {
                    Sheet sheet = workbook.getSheetAt(0);
                    JSONArray jsonArray = new JSONArray();
                    for (Row row : sheet) {
                        if (row.getRowNum() != 0) {
                            StringBuilder lineBuilder = new StringBuilder();
                            for (Cell cell : row) {
                                String cellValue = getCellValue(cell);
                                lineBuilder.append(cellValue).append(",");
                            }
                            String line = lineBuilder.toString();
                            line = line.substring(0, line.length() - 1);
                            jsonArray.put(parseLineToJson(line));
                        }
                    }
                    sendBatchData(jsonArray);
                } catch (IOException | InvalidFormatException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File yang dipilih bukan file CSV atau XLSX.");
            }
        }
    }

    public void databaseupload() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("File Excel", "*.xlsx");
        fileChooser.getExtensionFilters().add(excelFilter);

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setProgress(-1);
            progressIndicator.setPrefSize(50, 50);
            Task<Boolean> uploadTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    JSONObject json = convertExcelToJson(selectedFile);
                    return sendJsonToUrl(json, ApiRoute.Import);
                }
            };
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Upload Progress");
            alert.setHeaderText(null);
            alert.getDialogPane().setContent(new StackPane(progressIndicator));

            alert.setOnCloseRequest(event -> {
                if (uploadTask.isRunning()) {
                    event.consume();
                }
            });

            alert.show();

            uploadTask.setOnSucceeded(event -> {
                boolean isUploadSuccessful = uploadTask.getValue();

                if (isUploadSuccessful) {
                    Action.alertinfo(StringVariable.BerhasilTambah("Data"));
                } else {
                    Action.alerterror(StringVariable.GagalTambah("Data"));
                }

                alert.setResult(ButtonType.OK);
                alert.close();
            });

            Thread uploadThread = new Thread(uploadTask);
            uploadThread.start();
        } else {
            System.out.println(StringVariable.Cancel);
        }
    }

    private JSONObject convertExcelToJson(File excelFile) throws IOException {
        FileInputStream file = new FileInputStream(excelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        JSONObject json = new JSONObject();
        Map<String, JSONArray> jsonMap = new LinkedHashMap<>();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            JSONArray sheetData = new JSONArray();
            Row headerRow = sheet.getRow(0);

            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row dataRow = sheet.getRow(j);
                JSONObject rowData = new JSONObject();

                for (int k = 0; k < dataRow.getPhysicalNumberOfCells(); k++) {
                    Cell cell = dataRow.getCell(k);

                    if (cell != null) {
                        String columnName = headerRow.getCell(k).getStringCellValue();
                        CellType cellType = cell.getCellType();

                        if (cellType == CellType.STRING) {
                            String cellValue = cell.getStringCellValue();
                            rowData.put(columnName, cellValue);
                        } else if (cellType == CellType.NUMERIC) {
                            double cellValue = cell.getNumericCellValue();
                            String stringValue = String.valueOf((long) cellValue);
                            rowData.put(columnName, stringValue);
                        } else if (cellType == CellType.BOOLEAN) {
                            boolean cellValue = cell.getBooleanCellValue();
                            rowData.put(columnName, cellValue);
                        }
                    }
                }

                sheetData.put(rowData);
            }

            jsonMap.put(sheet.getSheetName(), sheetData);
        }

        jsonMap.forEach(json::put);

        file.close();
        return json;
    }

    private boolean sendJsonToUrl(JSONObject json, String url) throws IOException {
        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod(StringVariable.POST);
        connection.setRequestProperty(StringVariable.ContentType, "application/json");
        connection.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(json.toString());
        writer.flush();

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        return (responseCode >= 200 && responseCode < 300);
    }

    private JSONObject parseLineToJson(String line) {
        String[] values = line.split(",");
        JSONObject jsonObject = new JSONObject();

        DecimalFormat decimalFormat = new DecimalFormat("0");

        String nim = decimalFormat.format(Double.parseDouble(values[0]));
        jsonObject.put("nim", nim);
        jsonObject.put("nama", values[1]);
        jsonObject.put("no_hp", values[2]);

        return jsonObject;
    }

    private void sendBatchData(JSONArray jsonArray) {
        try {
            URL url = new URL(ApiRoute.BatchStoreMahasiswa);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.POST);
            connection.setRequestProperty(StringVariable.ContentType, "application/json");
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(jsonArray.toString());
            writer.flush();
            writer.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Data mahasiswa berhasil ditambahkan!");
            } else {
                System.out.println("Gagal mengirim data. Kode respons: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell.getCellType() == CellType.STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            cellValue = String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            cellValue = String.valueOf(cell.getBooleanCellValue());
        }
        return cellValue;
    }

    public void backup(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan File Backup");
        String fileName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel File", "*.xlsx"));

        java.io.File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            try {
                String apiURL = ApiRoute.BackupMahasiswa;
                URL url = new URL(apiURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(StringVariable.GET);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();

                    FileOutputStream outputStream = new FileOutputStream(selectedFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Download Berhasil");
                    alert.setHeaderText("File backup berhasil diunduh.");
                    alert.setContentText("Pilih opsi untuk membuka file atau menutup dialog.");

                    ButtonType bukaDirektoriButton = new ButtonType("Buka Direktori", ButtonBar.ButtonData.OK_DONE);
                    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(bukaDirektoriButton, okButton);

                    alert.setOnCloseRequest(e -> {
                        if (alert.getResult() == bukaDirektoriButton) {
                            try {
                                Desktop.getDesktop().open(selectedFile.getParentFile());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                    alert.showAndWait();
                } else {
                    Action.alerterror(StringVariable.Error);
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(StringVariable.Cancel);
        }
    }

    @FXML
    void kembali() {
        setUsernameStatus(false);
        setPasswordStatus(false);
        tombol_kembali.setVisible(false);
        setMenuStatus(true);
    }

    @FXML
    void ganti_password() {
        setPasswordStatus(true);
        setMenuStatus(false);
    }

    @FXML
    void ganti_username() {
        setUsernameStatus(true);
        setMenuStatus(false);
    }

    @FXML
    void submitpassword() {
        String oldUsername = TempVariable.username;
        String oldPassword = field_oldpassword_for_password.getText();
        String newPassword = field_newpassword_for_password.getText();
        String confirmPassword = field_confirmation_for_password.getText();

        try {
            String url = ApiRoute.updatePassword;
            URL apiUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();

            conn.setRequestMethod(StringVariable.POST);
            conn.setRequestProperty(StringVariable.ContentType, "application/json");
            conn.setDoOutput(true);
            String requestBody = "{\"username\":\"" + oldUsername + "\",\"password\":\"" + oldPassword + "\",\"new_password\":\"" + newPassword + "\",\"confirm_password\":\"" + confirmPassword + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(requestBodyBytes, 0, requestBodyBytes.length);
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                String message = jsonResponse.getString("message");
                Action.alertinfo(message);
                TempVariable.password = newPassword;
            } else {
                Action.alerterror(StringVariable.GagalUpdate("Password"));
            }
            conn.disconnect();
        } catch (Exception e) {
            Action.alerterror(StringVariable.Error);
        }
    }


    @FXML
    void submitusername() {
        String newUsername = field_username_for_username.getText();
        String password = field_password_for_username.getText();
        String oldUsername = TempVariable.username;

        try {
            String url = ApiRoute.updateUsername;
            URL apiUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();

            conn.setRequestMethod(StringVariable.POST);
            conn.setRequestProperty(StringVariable.ContentType, "application/json");
            conn.setDoOutput(true);
            String requestBody = "{\"username\":\"" + oldUsername + "\",\"password\":\"" + password + "\",\"new_username\":\"" + newUsername + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(requestBodyBytes, 0, requestBodyBytes.length);
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                //noinspection MismatchedQueryAndUpdateOfStringBuilder
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Action.alertinfo(StringVariable.BerhasilUpdate("Username"));
                TempVariable.username = newUsername;
                try {
                    File tokenFile = new File(TempVariable.filePath);
                    if (tokenFile.exists()) {
                        boolean deleted = tokenFile.delete();
                        if (deleted) {
                            System.out.println(StringVariable.BerhasilHapus("Token"));
                        }
                    }
                } catch (Exception e) {
                    System.out.println(StringVariable.Error);
                }
            } else {
                Action.alerterror(StringVariable.GagalUpdate("Username"));
            }
            conn.disconnect();
        } catch (Exception e) {
            Action.alerterror(StringVariable.Error);
        }
    }

    @FXML
    void truncate() {
        boolean confirmation = Action.alertkonfir(StringVariable.Confirmation);
        if (confirmation) {
            try {
                String url = ApiRoute.TruncateData;
                URL apiUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();

                conn.setRequestMethod(StringVariable.GET);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Action.alertinfo(StringVariable.BerhasilHapus("Data"));
                } else {
                    Action.alerterror(StringVariable.GagalHapus("Data"));
                }
                conn.disconnect();
            } catch (Exception e) {
                Action.alerterror(StringVariable.Error);
            }
        }
    }

    public void printreport() {
        try {
            Desktop.getDesktop().browse(new URI(ApiRoute.Report));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printrekap() {
        try {
            Desktop.getDesktop().browse(new URI(ApiRoute.Rekap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadreport(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan File Backup");
        String fileName = "report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        fileChooser.setInitialFileName(fileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel File", "*.xlsx"));

        java.io.File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            try {
                String apiURL = ApiRoute.DownloadReport;
                URL url = new URL(apiURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(StringVariable.GET);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();

                    FileOutputStream outputStream = new FileOutputStream(selectedFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Download Berhasil");
                    alert.setHeaderText("File backup berhasil diunduh.");
                    alert.setContentText("Pilih opsi untuk membuka file atau menutup dialog.");

                    ButtonType bukaDirektoriButton = new ButtonType("Buka Direktori", ButtonBar.ButtonData.OK_DONE);
                    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(bukaDirektoriButton, okButton);

                    alert.setOnCloseRequest(e -> {
                        if (alert.getResult() == bukaDirektoriButton) {
                            try {
                                Desktop.getDesktop().open(selectedFile.getParentFile());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                    alert.showAndWait();
                } else {
                    Action.alerterror(StringVariable.Error);
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(StringVariable.Cancel);
        }
    }
}
