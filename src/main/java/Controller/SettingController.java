package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import project.Route;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.Scanner;

public class SettingController implements Initializable {
   
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
            URL url = new URL(Route.URL + "mahasiswa/batchstore");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
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
        fileChooser.setInitialFileName("Mahasiswa");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Menampilkan dialog untuk memilih lokasi penyimpanan file
        java.io.File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            try {
                String apiURL = "http://127.0.0.1:8000/api/mahasiswa/backup";
                URL url = new URL(apiURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();

                    // Membuka file untuk menyimpan data CSV
                    FileOutputStream outputStream = new FileOutputStream(selectedFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    // Menutup file
                    outputStream.close();
                    inputStream.close();

                    System.out.println("File backup berhasil disimpan: " + selectedFile.getAbsolutePath());
                } else {
                    System.out.println("Gagal melakukan backup. Response code: " + responseCode);
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Batal menyimpan file backup.");
        }
    }
}
