package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private Label messageLabel;

    // Role passed from Roles page
    private String selectedRole = "STAFF"; // default

    // ✅ This runs when the FXML loads (helps detect fx:id problems)
    @FXML
    private void initialize() {
        if (emailField == null || passwordField == null) {
            System.out.println("❌ fx:id problem: emailField/passwordField is NULL. Check login.fxml fx:id names.");
        } else {
            System.out.println("✅ LoginController loaded successfully. Role = " + selectedRole);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("✅ handleLogin fired!");

        String email = (emailField == null || emailField.getText() == null) ? "" : emailField.getText().trim();
        String pass  = (passwordField == null || passwordField.getText() == null) ? "" : passwordField.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Please enter email and password.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showError("Invalid email format.");
            return;
        }

        // ✅ Now it checks the SAME store used by RegisterController
        if (!UserStore.exists(email)) {
            showError("Account not found. Please sign up first.");
            return;
        }

        if (!UserStore.validate(email, pass)) {
            showError("Wrong password. Please try again.");
            return;
        }

        // ✅ Success
        if (rememberMeCheckBox != null && rememberMeCheckBox.isSelected()) {
            System.out.println("Remember Me: ON for " + email);
            // Later you can save this in Preferences/file
        }

        showSuccess("Login successful ✅ (" + selectedRole + ")");

        // ✅ Route based on role
        if ("ADMIN".equalsIgnoreCase(selectedRole)) {
            switchScene(event, "/tutorial/dassist_ui/admin_dashboard.fxml", "Admin Dashboard");
        } else {
            switchScene(event, "/tutorial/dassist_ui/pharmacy_dashboard.fxml", "Pharmacy Staff Dashboard");
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        switchScene(event, "/tutorial/dassist_ui/register.fxml", "D-Assist - Register");
    }

    @FXML
    private void goBack(ActionEvent event) {
        switchScene(event, "/tutorial/dassist_ui/roles.fxml", "D-Assist - Roles");
    }

    private void showError(String msg) {
        if (messageLabel == null) {
            System.out.println("ERROR: " + msg);
            return;
        }
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13;");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        if (messageLabel == null) {
            System.out.println("SUCCESS: " + msg);
            return;
        }
        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13;");
        messageLabel.setText(msg);
    }

    private void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation failed: " + e.getMessage());
        }
    }

    // ✅ Stores the selected role
    public void setSelectedRole(String selectedRole) {
        if (selectedRole != null && !selectedRole.isBlank()) {
            this.selectedRole = selectedRole;
        }
    }
}
