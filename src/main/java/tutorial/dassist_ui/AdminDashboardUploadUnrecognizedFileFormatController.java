package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminDashboardUploadUnrecognizedFileFormatController {

    /**
     * Try uploading another file
     */
    @FXML
    private void onTryAgainClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard_upload_file.fxml");
    }

    /**
     * Return to dashboard
     */
    @FXML
    private void onBackToDashboardClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard.fxml");
    }

    /**
     * Cancel and return to dashboard
     */
    @FXML
    private void onCancelClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard.fxml");
    }
}
