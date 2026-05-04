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
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Personal Budgeting - Transactions");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showStatus("Invalid email or password. Try again.", "red");
        }
    }

    @FXML
    private void handleSignUpLink() {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Register.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Personal Budgeting - Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showStatus(String message, String color) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}