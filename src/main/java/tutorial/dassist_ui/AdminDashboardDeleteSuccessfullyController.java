package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminDashboardDeleteSuccessfullyController {

    /**
     * Return to dashboard after successful deletion
     */
    @FXML
    private void onBackToDashboardClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard.fxml");
    }

    /**
     * Delete another configuration
     */
    @FXML
    private void onDeleteAnotherClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard_delete_configuration.fxml");
    }

    /**
     * OK button - returns to dashboard
     */
    @FXML
    private void onOkClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard.fxml");
    }
}
