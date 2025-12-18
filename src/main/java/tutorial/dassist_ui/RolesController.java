package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RolesController {

    @FXML private Button adminBtn;
    @FXML private Button staffBtn;
    @FXML private Button loginBtn;
    @FXML private Label messageLabel; // optional (only if you added it in FXML)

    private String selectedRole = null;

    @FXML
    private void selectAdmin() {
        selectedRole = "ADMIN";
        highlightSelected();
        showMsg("Administrator selected ✅", false);
    }

    @FXML
    private void selectStaff() {
        selectedRole = "STAFF";
        highlightSelected();
        showMsg("Pharmacy Staff selected ✅", false);
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        if (selectedRole == null) {
            showMsg("Please choose a role first.", true);
            return;
        }

        // ✅ Load login page and pass the role
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tutorial/dassist_ui/login.fxml"));
            Parent root = loader.load();

            // pass role into LoginController
            LoginController loginController = loader.getController();
            loginController.setSelectedRole(selectedRole);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("D-Assist - Login (" + (selectedRole.equals("ADMIN") ? "Administrator" : "Pharmacy Staff") + ")");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showMsg("Failed to open login page.", true);
        }
    }

    private void highlightSelected() {
        if (adminBtn != null) adminBtn.setStyle(baseRoleStyle(adminBtn) + ( "ADMIN".equals(selectedRole) ? selectedStyle() : "" ));
        if (staffBtn != null) staffBtn.setStyle(baseRoleStyle(staffBtn) + ( "STAFF".equals(selectedRole) ? selectedStyle() : "" ));
    }

    private String baseRoleStyle(Button b) {
        // keep it simple so it doesn't break your existing CSS
        return "-fx-background-color: #EDEDED; -fx-background-radius: 20; -fx-font-size: 16;";
    }

    private String selectedStyle() {
        return " -fx-border-color: #000000; -fx-border-width: 2; -fx-border-radius: 20;";
    }

    private void showMsg(String msg, boolean error) {
        if (messageLabel == null) return;
        messageLabel.setStyle((error ? "-fx-text-fill: red;" : "-fx-text-fill: green;") + " -fx-font-size: 13;");
        messageLabel.setText(msg);
    }
}
