package project;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Action {
    private double xOffset = 0;
    private double yOffset = 0;
    private static final AtomicBoolean isMoving = new AtomicBoolean(false);

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
            String urlString = Route.URL + "login/";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set request parameters
            String parameters = "username=" + username
                    + "&password=" + password;
            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            // Set request headers
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataLength));

            // Send request
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
            e.printStackTrace();
            return false;
        }
    }

    public static String loginWithToken(String username, String password) {
        try {
            String urlString = Route.URL + "login/logintoken";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set request parameters
            String parameters = "username=" + username
                    + "&password=" + password;
            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            // Set request headers
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataLength));

            // Send request
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

                VarTemp.username = username;
                VarTemp.password = password;
                VarTemp.token = token;

                saveTokenToFile(token);

                return token;
            } else {
                return null;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void saveTokenToFile(String token) {
        try {
            String folderPath = VarTemp.folderPath;
            String filePath = VarTemp.filePath;

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
        String filePath = VarTemp.filePath;

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
            String urlString = Route.URL + "login/logout";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String parameters = "username=" + username;
            byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
    public static boolean validateToken(String token) {
        try {
            // Build the URL with token
            String url = "http://127.0.0.1:8000/api/login/token/" + token;
            URL apiUrl = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Token is valid
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String responseBody = reader.readLine();

                // Parse the response body and extract username and password
                // Assuming the response is in JSON format
                JSONObject json = new JSONObject(responseBody);
                String username = json.getString("username");
                String password = json.getString("password");

                // Store the username, password, and token in VarTemp
                VarTemp.username = username;
                VarTemp.password = password;
                VarTemp.token = token;

                return true;
            } else {
                // Token is invalid or expired
                return false;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
