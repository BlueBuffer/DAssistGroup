package tutorial.dassist_ui;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    // ---------------- BUTTON ACTIONS ----------------

    @FXML
    private void handleRegister() {

        String username = safeTrim(nameField);
        String email = safeTrim(emailField);
        String password = passwordField == null ? "" : passwordField.getText();

        // 1) Empty checks
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        // 2) Username rule
        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }

        // 3) Email format
        if (!isValidEmail(email)) {
            showError("Invalid email format. Example: user@gmail.com");
            return;
        }

        // ✅ NEW: Email already registered
        if (UserStore.exists(email)) {
            showError("This email is already registered. Please log in.");
            return;
        }

        // 4) Password format
        if (!isValidPassword(password)) {
            showError("""
Password must contain:
• At least 8 characters
• One uppercase letter (A-Z)
• One lowercase letter (a-z)
• One number (0-9)
""");
            return;
        }

        // 5) Username taken (demo check only)
        if (isUsernameTaken(username)) {
            showError("Username is already taken. Try another one.");
            return;
        }

        // ✅ SAVE USER so Login can find it
        UserStore.addUser(email, password);

        // ✅ Success
        showSuccess("Registration successful ✅ You can now login.");

        // OPTIONAL: auto-go to login after 1.2 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(e -> openLogin());
        pause.play();
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        switchScene(event, "/tutorial/dassist_ui/login.fxml", "D-Assist - Login");
    }

    @FXML
    private void goBack(ActionEvent event) {
        switchScene(event, "/tutorial/dassist_ui/login.fxml", "D-Assist - Login");
    }

    // ---------------- NAVIGATION ----------------

    private void openLogin() {
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/tutorial/dassist_ui/login.fxml"));
            stage.setTitle("D-Assist - Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to open login page.");
        }
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

    // ---------------- VALIDATION HELPERS ----------------

    private String safeTrim(TextField tf) {
        return tf == null || tf.getText() == null ? "" : tf.getText().trim();
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

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidPassword(String password) {
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", password);
    }

    private boolean isUsernameTaken(String username) {
        return username.equalsIgnoreCase("admin")
                || username.equalsIgnoreCase("test")
                || username.equalsIgnoreCase("user");
    }
}
