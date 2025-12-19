package MainApp;

import javafx.application.Application;
import javafx.stage.Stage;
import util.SceneNavigator;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        SceneNavigator.setStage(stage);
        SceneNavigator.goTo("user_dashboard.fxml");

        stage.setTitle("D-Assist");
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
