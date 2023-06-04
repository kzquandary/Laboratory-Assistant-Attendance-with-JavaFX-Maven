package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            if (selectedFile.getName().endsWith(".csv")) {
                try (Scanner scanner = new Scanner(selectedFile)) {
                    JSONArray jsonArray = new JSONArray();
                    if (scanner.hasNextLine()) {
                        // Skip header line
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

}
