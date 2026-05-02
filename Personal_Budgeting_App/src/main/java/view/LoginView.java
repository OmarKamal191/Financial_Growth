package view;

import controller.AuthController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import util.Validator;

public class LoginView {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private AuthController authController = new AuthController();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (!Validator.isNotEmpty(email) || !Validator.isNotEmpty(password)) {
            showStatus("Please fill in all fields", "red");
            return;
        }

        if (authController.login(email, password)) {
            showStatus("Login Successful! Redirecting...", "green");

        } else {
            showStatus("Invalid email or password. Try again.", "red");
        }
    }

    @FXML
    private void handleSignUpLink() {
        System.out.println("Navigating to Sign Up...");
    }

    private void showStatus(String message, String color) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}