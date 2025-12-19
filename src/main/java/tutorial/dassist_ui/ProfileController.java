package tutorial.dassist_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class ProfileController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private String currentEmail;

    // ✅ JavaFX calls initialize() with NO parameters
    @FXML
    private void initialize() {

        // ✅ Read current logged-in user from session
        if (!UserSession.isLoggedIn()) {
            showError("No active session. Please login again.");
            return;
        }

        currentEmail = UserSession.getEmail();

        // ✅ Load user from store
        UserStore.User user = UserStore.getUser(currentEmail);
        if (user == null) {
            showError("User not found. Please login again.");
            return;
        }

        // ✅ Fill fields (don’t show real password)
        nameField.setText(user.getName() == null ? "" : user.getName());
        emailField.setText(user.getEmail() == null ? "" : user.getEmail());
        passwordField.setText("********");

        // ✅ Lock fields initially
        nameField.setDisable(true);
        emailField.setDisable(true);
        passwordField.setDisable(true);
    }

    // ---------------- NAME ----------------
    @FXML
    private void editName(ActionEvent e) {
        if (nameField.isDisable()) {
            nameField.setDisable(false);
            nameField.requestFocus();
            showInfo("Edit name, then press Enter to save.");
            nameField.setOnAction(ev -> saveName());
        } else {
            saveName();
        }
    }

    private void saveName() {
        String newName = safe(nameField.getText());
        if (newName.length() < 3) {
            showError("Name must be at least 3 characters.");
            return;
        }

        if (!UserStore.updateName(currentEmail, newName)) {
            showError("Failed to update name.");
            return;
        }

        nameField.setDisable(true);
        showSuccess("Name updated ✅");
    }

    // ---------------- EMAIL ----------------
    @FXML
    private void editEmail(ActionEvent e) {
        if (emailField.isDisable()) {
            emailField.setDisable(false);
            emailField.requestFocus();
            showInfo("Edit email, then press Enter to save.");
            emailField.setOnAction(ev -> saveEmail());
        } else {
            saveEmail();
        }
    }

    private void saveEmail() {
        String newEmail = safe(emailField.getText()).toLowerCase();

        if (!isValidEmail(newEmail)) {
            showError("Invalid email format.");
            return;
        }

        if (newEmail.equalsIgnoreCase(currentEmail)) {
            emailField.setDisable(true);
            showInfo("No changes made.");
            return;
        }

        if (UserStore.exists(newEmail)) {
            showError("This email is already registered.");
            return;
        }

        // ✅ Update store (also updates session inside UserStore.updateEmail)
        boolean ok = UserStore.updateEmail(currentEmail, newEmail);
        if (!ok) {
            showError("Failed to update email.");
            return;
        }

        // ✅ Update local variable
        currentEmail = newEmail;

        emailField.setDisable(true);
        showSuccess("Email updated ✅");
    }

    // ---------------- PASSWORD ----------------
    @FXML
    private void editPassword(ActionEvent e) {
        if (passwordField.isDisable()) {
            passwordField.setDisable(false);
            passwordField.clear();
            passwordField.requestFocus();
            passwordField.setPromptText("Enter new password");
            showInfo("Enter a new password, then press Enter to save.");
            passwordField.setOnAction(ev -> savePassword());
        } else {
            savePassword();
        }
    }

    private void savePassword() {
        String newPass = passwordField.getText() == null ? "" : passwordField.getText();

        if (!isValidPassword(newPass)) {
            showError("""
Password must contain:
• At least 8 characters
• One uppercase letter (A-Z)
• One lowercase letter (a-z)
• One number (0-9)
""");
            return;
        }

        if (!UserStore.updatePassword(currentEmail, newPass)) {
            showError("Failed to update password.");
            return;
        }

        passwordField.setDisable(true);
        passwordField.setText("********");
        showSuccess("Password updated ✅");
    }

    // ---------------- LOGOUT ----------------
    @FXML
    private void handleLogout(ActionEvent event) {
        // ✅ Clear session
        UserSession.logout();

        // ✅ Go back to login
        switchScene(event, "/tutorial/dassist_ui/login.fxml", "D-Assist - Login");
    }

    // ---------------- HELPERS ----------------
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

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidPassword(String password) {
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", password);
    }

    private void showError(String msg) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13;");
            messageLabel.setText(msg);
        }
    }

    private void showSuccess(String msg) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13;");
            messageLabel.setText(msg);
        }
    }

    private void showInfo(String msg) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: #444; -fx-font-size: 13;");
            messageLabel.setText(msg);
        }
    }
}
