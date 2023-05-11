package controller;

import com.aslabapp.aslabapp.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import project.Route;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TambahPertemuan {
    public Button back;
    public Button mhsview;
    public Button tambah;

    public Button minimize;

    public Button close;
    public TextField kode_pertemuan;
    public Button submit;
    public Label tbmhsvalid;
    public DatePicker tanggal_pertemuan;


    public void submit() throws Exception {
        URL url = new URL(Route.URL+"pertemuan/store");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");

        conn.setDoOutput(true);

        DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date = tanggal_pertemuan.getValue();

        String apiDate = date.format(apiDateFormatter);

        String requestBody = String.format("{\"kode_pertemuan\":\"%s\",\"tanggal_pertemuan\":\"%s\"}",
                kode_pertemuan.getText(), apiDate);

        byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);

        conn.setRequestProperty("Content-Length", Integer.toString(requestBodyBytes.length));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBodyBytes, 0, requestBodyBytes.length);
        }

        int httpResponseCode = conn.getResponseCode();

        System.out.println("HTTP response code: " + httpResponseCode);
        if(httpResponseCode == 201){
            tbmhsvalid.setText("Pertemuan Ditambahkan");
        } else {
            tbmhsvalid.setText("Gagal Menambahkan");
        }
    }

    public void viewpertemuan() throws IOException {
        Stage stage = (Stage) tambah.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(Route.ViewPertemuan));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Aslab App");
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));
        stage.setResizable(false);
        stage.show();
    }
}

