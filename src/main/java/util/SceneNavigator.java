package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class SceneNavigator {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static FXMLLoader goTo(String fxml) {
        try {
            String path = "/dassist/fxml/" + fxml;
            URL resource = SceneNavigator.class.getResource(path);

            if (resource == null) {
                throw new RuntimeException("FXML not found at: " + path);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            stage.setScene(new Scene(loader.load()));
            return loader;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
