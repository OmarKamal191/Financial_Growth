package view;

import controller.AuthController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.Validator;
import java.io.IOException;

public class RegisterView {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private AuthController authController = new AuthController();

    @FXML
    private void handleSignUp() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!Validator.isNotEmpty(name) || !Validator.isNotEmpty(email) || !Validator.isNotEmpty(password)) {
            showMessage("Please fill all fields", "red");
            return;
        }

        if (!Validator.isValidEmail(email)) {
            showMessage("Invalid email format", "red");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match", "red");
            return;
        }

        if (!Validator.isPasswordStrong(password)) {
            showMessage("Password must be 8+ chars with numbers", "red");
            return;
        }

        boolean success = authController.register(name, email, password);

        if (success) {
            showMessage("Registration successful!", "green");
        } else {
            showMessage("Registration failed. Email might exist.", "red");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Personal Budgeting - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String text, String color) {
        if (messageLabel != null) {
            messageLabel.setText(text);
            messageLabel.setStyle("-fx-text-fill: " + color + ";");
        }
    }
}