module tutorial.dassist_ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;



    opens tutorial.dassist_ui to javafx.fxml;
    exports tutorial.dassist_ui;
}