package tutorial.dassist_ui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SuccessController {

    @FXML
    private void initialize() {
        // stay 10 seconds, then go to login
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> openLogin());
        pause.play();
    }

    private void openLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/tutorial/dassist_ui/login.fxml"));
            Stage stage = (Stage) root.getScene().getWindow(); // may be null if not attached yet
        } catch (Exception ignored) {
            // fallback below
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/tutorial/dassist_ui/login.fxml"));
            Stage stage = (Stage) javafx.stage.Window.getWindows().stream()
                    .filter(w -> w.isShowing())
                    .findFirst()
                    .orElseThrow()
                    .getScene()
                    .getWindow();

            stage.setTitle("D-Assist - Login");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
