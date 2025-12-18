module tutorial.dassist_ui {
    requires javafx.controls;
    requires javafx.fxml;


    opens tutorial.dassist_ui to javafx.fxml;
    exports tutorial.dassist_ui;
}