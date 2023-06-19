package Project;

import Model.Pertemuan;
import com.aslabapp.aslabapp.Main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Action {
    private double xOffset = 0;
    private double yOffset = 0;
    private static final AtomicBoolean isMoving = new AtomicBoolean(false);

    public static void alerterror(String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image((Objects.requireNonNull(Main.class.getResourceAsStream("logo.png")))));
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static boolean alertkonfir(String content) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Konfirmasi");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText(content);
        Stage stage = (Stage) confirmationDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("logo.png"))));

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    public static void alertinfo(String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image((Objects.requireNonNull(Main.class.getResourceAsStream("logo.png")))));
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static void toastinfo(String content){
        Notifications.create()
                .title("Informasi")
                .text(content)
                .darkStyle()
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(Duration.seconds(2))
                .showInformation();
    }
    public static void toasterror(String content){
        Notifications.create()
                .title("Error")
                .text(content)
                .darkStyle()
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(Duration.seconds(3))
                .showError();
    }
    public void handleMouseDragged(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }


    public void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    public void handleExit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void handleMinimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }


    public static void Move(FXMLLoader loader, Pane content) {
        if (!isMoving.get()) {
            isMoving.set(true);

            Pane loadingPane = createLoadingPane(content);
            content.getChildren().add(loadingPane);

            Task<Void> loadingTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1000);

                    Platform.runLater(() -> {
                        try {
                            Pane loadedPane = loader.load();
                            content.getChildren().remove(loadingPane);
                            content.getChildren().add(loadedPane);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            isMoving.set(false);
                        }
                    });

                    return null;
                }
            };

            Thread loadingThread = new Thread(loadingTask);
            loadingThread.setDaemon(true);
            loadingThread.start();
        }
    }

    public static Pane createLoadingPane(Pane content) {
        Pane loadingPane = new Pane();
        loadingPane.setPrefSize(content.getWidth(), content.getHeight());

        ImageView loadingImage = new ImageView(new Image(Objects.requireNonNull(Action.class.getResourceAsStream("/com/aslabapp/aslabapp/Loading.gif"))));
        LoadingAnimation(loadingPane, loadingImage);
        return loadingPane;
    }

    public static void LoadingAnimation(Pane loadingPane, ImageView loadingImage) {
        loadingImage.setFitWidth(100);
        loadingImage.setFitHeight(100);
        double loadingImageX = (900 - loadingImage.getFitWidth()) / 2;
        double loadingImageY = (600 - loadingImage.getFitHeight()) / 2;
        loadingImage.setLayoutX(loadingImageX);
        loadingImage.setLayoutY(loadingImageY);
        loadingPane.getChildren().add(loadingImage);
    }

    public static boolean loginWithCredentials(String username, String password) {
        try {
            String urlString = ApiRoute.Login;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.POST);
            connection.setDoOutput(true);

            String parameters = "username=" + username
                    + "&password=" + password;
            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            connection.setRequestProperty(StringVariable.ContentType, "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataLength));

            try (DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
                dataOutputStream.write(postData);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                JSONObject jsonResponse = new JSONObject(response.toString());
                String message = jsonResponse.getString("message");

                return message.equals("Login successful");
            } else {
                return false;
            }
        } catch (IOException | JSONException e) {
            Action.alerterror(StringVariable.ApiError);
            return false;
        }
    }

    public static String loginWithToken(String username, String password) {
        try {
            String urlString = ApiRoute.LoginToken;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.POST);
            connection.setDoOutput(true);

            String parameters = "username=" + username
                    + "&password=" + password;
            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            connection.setRequestProperty(StringVariable.ContentType, "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataLength));

            try (DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
                dataOutputStream.write(postData);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                JSONObject jsonResponse = new JSONObject(response.toString());
                String token = jsonResponse.getString("token");

                TempVariable.username = username;
                TempVariable.password = password;
                TempVariable.token = token;

                saveTokenToFile(token);

                return token;
            } else {
                return null;
            }
        } catch (IOException | JSONException e) {
            Action.alerterror(StringVariable.ApiError);
            return null;
        }
    }


    private static void saveTokenToFile(String token) {
        try {
            String folderPath = TempVariable.folderPath;
            String filePath = TempVariable.filePath;

            Path folder = Paths.get(folderPath);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            FileWriter writer = new FileWriter(filePath);
            writer.write(token);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTokenFile() {
        String filePath = TempVariable.filePath;

        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.out.println("Gagal menghapus file token.txt");
            }
        }
    }


    public static void logoutFromAPI(String username) {
        try {
            String urlString = ApiRoute.Logout;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.POST);
            connection.setDoOutput(true);
            String parameters = "username=" + username;
            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            connection.setRequestProperty(StringVariable.ContentType, "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataLength));

            try (DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
                dataOutputStream.write(postData);
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Logout successful");
            } else {
                System.out.println("Logout failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkAPIStatus() {
        try {
            URL url = new URL(ApiRoute.URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.GET);

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean validateToken(String token) {
        try {
            String url = ApiRoute.setCheckToken(token);
            URL apiUrl = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod(StringVariable.GET);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String responseBody = reader.readLine();

                JSONObject json = new JSONObject(responseBody);
                String username = json.getString("username");
                String password = json.getString("password");

                TempVariable.username = username;
                TempVariable.password = password;
                TempVariable.token = token;

                return true;
            } else {
                return false;
            }
        } catch (IOException | JSONException e) {
            Action.alerterror(StringVariable.ApiError);
            return false;
        }
    }
    public static void GetKodePertemuan(ChoiceBox<Pertemuan> kodepertemuan) {
        String apiUrl = ApiRoute.GetPertemuan;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.GET);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.lines().collect(Collectors.joining());

                JSONArray jsonArray = new JSONArray(response);
                ObservableList<Pertemuan> pertemuanList = FXCollections.observableArrayList();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String kodePertemuan = jsonObject.getString("kode_pertemuan");
                    LocalDate tanggalPertemuan = LocalDate.parse(jsonObject.getString("tanggal_pertemuan"));

                    Pertemuan pertemuan = new Pertemuan(kodePertemuan, tanggalPertemuan);
                    pertemuanList.add(pertemuan);
                }
                kodepertemuan.setItems(pertemuanList);
                kodepertemuan.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Pertemuan pertemuan) {
                        if (pertemuan != null) {
                            return pertemuan.getKode_pertemuan();
                        } else {
                            return "Pertemuan kosong";
                        }
                    }

                    @Override
                    public Pertemuan fromString(String s) {
                        return null;
                    }
                });
                if (!pertemuanList.isEmpty()) {
                    kodepertemuan.setValue(pertemuanList.get(0));
                }
            }
        } catch (Exception e) {
            Action.alerterror(StringVariable.ApiError);
        }
    }
    public static JSONArray getObjects(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(StringVariable.GET);
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

}
