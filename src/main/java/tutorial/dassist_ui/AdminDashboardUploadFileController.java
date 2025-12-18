package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;

public class AdminDashboardUploadFileController {

    /**
     * Handle file selection and upload
     */
    @FXML
    private void onSelectFileClicked(ActionEvent event) {
        // Open file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Configuration File");
        
        // Add file filters
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON Files", "*.json"),
            new FileChooser.ExtensionFilter("XML Files", "*.xml"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // Get the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            // Check file format and navigate accordingly
            String fileName = selectedFile.getName().toLowerCase();
            if (fileName.endsWith(".json") || fileName.endsWith(".xml")) {
                // Valid file format - navigate to upload completed page
                SceneManager.switchTo(event, "admin_dashboard_upload_completed.fxml");
            } else {
                // Unrecognized file format - navigate to error page
                SceneManager.switchTo(event, "admin_dashboard_upload_unrecognized_file_format.fxml");
            }
        }
    }

    /**
     * Cancel upload and return to dashboard
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
