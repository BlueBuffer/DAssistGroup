package tutorial.dassist_ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/tutorial/dassist_ui/admin_dashboard.fxml"
        ));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Upload Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}