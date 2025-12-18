package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminDashboardUploadCompletedController {

    /**
     * Return to dashboard after successful upload
     */
    @FXML
    private void onBackToDashboardClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard.fxml");
    }

    /**
     * Upload another file
     */
    @FXML
    private void onUploadAnotherFileClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard_upload_file.fxml");
    }

    /**
     * Close/OK button - returns to dashboard
     */
    @FXML
    private void onOkClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard.fxml");
    }
}
