package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class AdminDashboardDeleteConfigurationController {

    @FXML
    private ListView<String> configurationListView;

    /**
     * Initialize the controller
     */
    @FXML
    private void initialize() {
        // Initialize the list view with sample configurations
        // In a real application, this would load from a database or file
    }

    /**
     * Confirm deletion and navigate to success page
     */
    @FXML
    private void onDeleteClicked(ActionEvent event) {
        // Get selected item
        String selectedItem = configurationListView.getSelectionModel().getSelectedItem();
        
        if (selectedItem != null) {
            // Perform deletion logic here
            // Navigate to success page
            SceneManager.switchTo(event, "admin_dashboard_delete_successfully.fxml");
        } else {
            // Show error message if no item selected
            System.out.println("No configuration selected");
        }
    }

    /**
     * Cancel deletion and return to dashboard
     */
    @FXML
    private void onCancelClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard.fxml");
    }

    /**
     * Go back to dashboard
     */
    @FXML
    private void onBackToDashboardClicked(ActionEvent event) {
        SceneManager.switchTo(event, "admin_dashboard.fxml");
    }
}
