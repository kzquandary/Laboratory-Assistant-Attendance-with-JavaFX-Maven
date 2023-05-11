package controller;
import com.aslabapp.aslabapp.Main;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class SettingController {
    @FXML
    private void aqua() {
        LinearGradient gradient = new LinearGradient(
                0.0, 0.0, 1.0, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.RED),
                new Stop(0.5, Color.GREEN),
                new Stop(1.0, Color.BLUE)
        );
        Main.changebg(gradient);
    }
}
