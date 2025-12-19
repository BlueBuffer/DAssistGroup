package tutorial.dassist_ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private Button registerButton;
    @FXML private Label messageLabel;

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

        // 4) Email already exists
        if (UserStore.exists(email)) {
            showError("This email is already registered. Please log in.");
            return;
        }

        // 5) Password format
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

        // 6) Username taken (demo check)
        if (isUsernameTaken(username)) {
            showError("Username is already taken. Try another one.");
            return;
        }

        // ✅ SAVE USER so Login can find it
        UserStore.addUser(username, email, password);


        // ✅ GO TO SUCCESS PAGE (10 sec redirect will happen inside SuccessController)
        openSuccessPage();
    }

    private void openSuccessPage() {
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Parent root = FXMLLoader.load(
                    getClass().getResource("/tutorial/dassist_ui/success.fxml")
            );
            stage.setTitle("Registration Successful");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open success page.");
        }
    }

    // ---------------- HELPERS ----------------

    private String safeTrim(TextField tf) {
        return tf == null || tf.getText() == null ? "" : tf.getText().trim();
    }

    private void showError(String msg) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13;");
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
