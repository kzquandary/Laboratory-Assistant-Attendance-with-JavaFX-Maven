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

import java.io.IOException;
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
}
