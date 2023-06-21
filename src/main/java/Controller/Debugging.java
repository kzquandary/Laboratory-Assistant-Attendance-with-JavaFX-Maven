package Controller;


//public class Debugging {
//    public static void main(String[] args){
//        String filePath = Main.class.getResource("data.txt").getPath();
//        System.out.println(filePath);
//    }
//}
//import java.io.*;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.*;
//import org.json.*;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//public class Debugging {
//    public static void main(String[] args) {
//        try {
//            // Load file Excel
//            FileInputStream file = new FileInputStream("C:\\Users\\Kzquandary\\Downloads\\dbfix.xlsx");
//
//            // Inisialisasi objek workbook dari file Excel
//            XSSFWorkbook workbook = new XSSFWorkbook(file);
//
//            // Inisialisasi objek JSON utuh
//            JSONObject json = new JSONObject();
//
//            // Membuat LinkedHashMap untuk mempertahankan urutan
//            Map<String, JSONArray> jsonMap = new LinkedHashMap<>();
//
//            // Iterasi setiap halaman (sheet) dalam workbook
//            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//                // Mendapatkan halaman (sheet) berdasarkan indeks
//                XSSFSheet sheet = workbook.getSheetAt(i);
//
//                // Inisialisasi array JSON untuk menyimpan data dari halaman (sheet) saat ini
//                JSONArray sheetData = new JSONArray();
//
//                // Mendapatkan baris pertama sebagai header kolom
//                Row headerRow = sheet.getRow(0);
//
//                // Iterasi setiap baris data mulai dari baris kedua
//                for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
//                    // Mendapatkan baris saat ini
//                    Row dataRow = sheet.getRow(j);
//
//                    // Inisialisasi objek JSON untuk menyimpan data dari baris saat ini
//                    JSONObject rowData = new JSONObject();
//
//                    // Iterasi setiap sel dalam baris saat ini
//                    for (int k = 0; k < dataRow.getPhysicalNumberOfCells(); k++) {
//                        // Mendapatkan sel saat ini
//                        Cell cell = dataRow.getCell(k);
//
//                        // Mendapatkan nilai sel berdasarkan tipe datanya
//                        if (cell != null) {
//                            // Mendapatkan nilai kolom saat ini
//                            String columnName = headerRow.getCell(k).getStringCellValue();
//
//                            // Mendapatkan tipe data sel
//                            CellType cellType = cell.getCellType();
//
//                            // Mendapatkan nilai sel berdasarkan tipe datanya
//                            if (cellType == CellType.STRING) {
//                                String cellValue = cell.getStringCellValue();
//                                rowData.put(columnName, cellValue);
//                            } else if (cellType == CellType.NUMERIC) {
//                                double cellValue = cell.getNumericCellValue();
//                                String stringValue = String.valueOf((long) cellValue); // Mengubah nilai numerik menjadi string
//                                rowData.put(columnName, stringValue);
//                            } else if (cellType == CellType.BOOLEAN) {
//                                boolean cellValue = cell.getBooleanCellValue();
//                                rowData.put(columnName, cellValue);
//                            }
//                        }
//                    }
//
//                    // Menambahkan data baris ke dalam array JSON halaman (sheet) saat ini
//                    sheetData.put(rowData);
//                }
//
//                // Menambahkan array JSON halaman (sheet) ke dalam LinkedHashMap
//                jsonMap.put(sheet.getSheetName(), sheetData);
//            }
//
//            // Menambahkan elemen LinkedHashMap ke dalam objek JSON utuh dengan urutan yang diinginkan
//            jsonMap.forEach(json::put);
//
//            // Tulis objek JSON ke file output
//            FileWriter output = new FileWriter("output.json");
//            output.write(json.toString());
//            output.close();
//
//            // Tutup file Excel
//            file.close();
//
//            System.out.println("Konversi Excel ke JSON selesai.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}


//import project.ApiRoute;

//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class Debugging {
//    public static void main(String[] args) {
//        String csvFile = "C:\\Users\\Kzquandary\\Documents\\Joki Tugas\\HTML\\Mahasiswa.csv";
//        String line;
//        String[] headers = null;
//        String[][] data = null;
//
//        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
//            // Baca baris pertama (header)
//            line = br.readLine();
//            headers = line.split(",");
//            // Hitung jumlah baris data
//            int numRows = 0;
//            while (br.readLine() != null) {
//                numRows++;
//            }
//            // Kembali ke awal file dan baca data
//            br.close();
//            BufferedReader newBr = new BufferedReader(new FileReader(csvFile));
//            newBr.readLine(); // Lewati baris header
//
//            // Baca data
//            data = new String[numRows][];
//            int rowIndex = 0;
//            while ((line = newBr.readLine()) != null) {
//                data[rowIndex++] = line.split(",");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Cetak tabel
//        if (headers != null && data != null) {
//            for (String header : headers) {
//                System.out.print(header + "\t\t");
//            }
//            System.out.println();
//
//            for (String[] row : data) {
//                if (row != null) {
//                    for (String cell : row) {
//                        System.out.print(cell + "\t\t");
//                    }
//                    System.out.println();
//                }
//            }
//        }
//    }
//}


//public class Debugging implements Runnable{
//    public static void main(String[] args){
//        Thread t = new Thread(this);
//        t.start();
//    }
//    public void run(){
//        System.out.println("test");
//    }
//}
//import project.TempVariable;
//import java.io.*;
//import java.util.HashMap;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//public class Debugging {
//    public static void main(String[] args){
//        String accessToken = "12345";
//        String appDataDir = System.getenv("APPDATA");
//        String folderPath = appDataDir + File.separator + "AslabApp";
//        String filePath = folderPath + File.separator + "token.txt";
//
//        try {
//            // Create the folder if it doesn't exist
//            Path folder = Paths.get(folderPath);
//            if (!Files.exists(folder)) {
//                Files.createDirectories(folder);
//            }
//            // Write access token to file
//            FileWriter writer = new FileWriter(filePath);
//            writer.write(accessToken);
//            writer.flush();
//            writer.close();
//            System.out.println("File disimpan di: " + filePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            // Read the file
//            Path file = Paths.get(TempVariable.filePath);
//            BufferedReader reader = new BufferedReader(new FileReader(file.toString()));
//            TempVariable.token = reader.readLine();
//            reader.close();
//
//            // Print the access token
//            System.out.println("Access Token: " + TempVariable.token);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            // Delete the file
//            Path file = Paths.get(TempVariable.filePath);
//            Files.deleteIfExists(file);
//
//            System.out.println("File deleted successfully");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

