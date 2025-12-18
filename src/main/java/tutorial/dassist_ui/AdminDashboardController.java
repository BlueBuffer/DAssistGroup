package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminDashboardController {

    /**
     * Navigate to the upload file page
     */
    @FXML
    private void onUploadFileClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard_upload_file.fxml");
    }

    /**
     * Navigate to the delete configuration page
     */
    @FXML
    private void onDeleteConfigurationClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard_delete_configuration.fxml");
    }

    /**
     * Navigate to the notifications page
     */
    @FXML
    private void onNotificationsClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_notification_mainpage.fxml");
    }

    /**
     * Handle logout action
     */
    @FXML
    private void onLogoutClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_logout.fxml");
    }
}
