package tutorial.dassist_ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class MealTimeCalculatorController {

    @FXML
    private TextField currentBloodGlucoseField;

    @FXML
    private TextField carbsToEatField;

    @FXML
    private TextField targetBloodGlucoseField;

    @FXML
    private TextField insulinSensitivityField;

    @FXML
    private TextField carbRatioField;

    @FXML
    private void onCalculateClicked() {
        try {
            // Get input values
            double currentBG = parseDouble(currentBloodGlucoseField.getText(), "Current Blood Glucose");
            double carbsToEat = parseDouble(carbsToEatField.getText(), "Carbs to Eat");
            double targetBG = parseDouble(targetBloodGlucoseField.getText(), "Target Blood Glucose");
            double insulinSensitivity = parseDouble(insulinSensitivityField.getText(), "Insulin Sensitivity");
            double carbRatio = parseDouble(carbRatioField.getText(), "Carb Ratio");

            // Calculate insulin dose
            // Formula: (Current BG - Target BG) / ISF + Carbs / ICR
            double correctionDose = (currentBG - targetBG) / insulinSensitivity;
            double mealDose = carbsToEat / carbRatio;
            double totalDose = correctionDose + mealDose;

            // Round to 1 decimal place
            totalDose = Math.round(totalDose * 10.0) / 10.0;
            correctionDose = Math.round(correctionDose * 10.0) / 10.0;
            mealDose = Math.round(mealDose * 10.0) / 10.0;

            // Display result
            showResult(totalDose, correctionDose, mealDose);

        } catch (NumberFormatException e) {
            showError("Invalid Input", e.getMessage());
        } catch (Exception e) {
            showError("Calculation Error", "An error occurred during calculation: " + e.getMessage());
        }
    }

    private double parseDouble(String value, String fieldName) throws NumberFormatException {
        if (value == null || value.trim().isEmpty()) {
            throw new NumberFormatException("Please enter a value for " + fieldName);
        }
        try {
            double parsedValue = Double.parseDouble(value.trim());
            if (parsedValue < 0) {
                throw new NumberFormatException(fieldName + " cannot be negative");
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid number format for " + fieldName);
        }
    }

    private void showResult(double totalDose, double correctionDose, double mealDose) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Insulin Dose Calculation Result");
        alert.setHeaderText("Recommended Insulin Dose");

        String content = String.format(
            "Total Insulin Dose: %.1f units\n\n" +
            "Breakdown:\n" +
            "  - Correction Dose: %.1f units\n" +
            "  - Meal Dose: %.1f units\n\n" +
            "Disclaimer: This is for informational purposes only.\n" +
            "Please consult your healthcare provider before making any changes to your insulin regimen.",
            totalDose, correctionDose, mealDose
        );

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
