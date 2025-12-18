package tutorial.dassist_ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class SceneManager {
    
    /**
     * Switch to a different FXML scene
     * @param event The ActionEvent from the button click
     * @param fxmlFile The FXML file name (e.g., "admin_dashboard.fxml")
     */
    public static void switchTo(ActionEvent event, String fxmlFile) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
            Parent root = loader.load();
            
            // Get the stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Create and set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlFile);
            e.printStackTrace();
        }
    }
    
    /**
     * Switch to a different FXML scene with custom size
     * @param event The ActionEvent from the button click
     * @param fxmlFile The FXML file name
     * @param width Window width
     * @param height Window height
     */
    public static void switchTo(ActionEvent event, String fxmlFile, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
