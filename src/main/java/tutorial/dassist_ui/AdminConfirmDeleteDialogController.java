package tutorial.dassist_ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AdminConfirmDeleteDialogController {

    @FXML private Label messageLabel;
    @FXML private Button yesButton;
    @FXML private Button noButton;
    @FXML private Button closeButton;

    private boolean confirmed = false;

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML private void onYes() {
        confirmed = true;
        close();
    }

    @FXML private void onNo() {
        confirmed = false;
        close();
    }

    private void close() {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }
}
