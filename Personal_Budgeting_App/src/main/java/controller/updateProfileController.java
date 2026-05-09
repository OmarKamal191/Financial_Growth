package controller;

import database.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class updateProfileController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleUpdate() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPass = passwordField.getText().trim();
        String confirmPass = confirmPasswordField.getText().trim();

        if (newName.isEmpty() && newEmail.isEmpty() && newPass.isEmpty()) {
            statusLabel.setText("No changes entered.");
            return;
        }

        if (!newPass.isEmpty() && !newPass.equals(confirmPass)) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Passwords do not match!");
            return;
        }

        boolean success = userDAO.updateProfile(1, newName, newEmail, newPass);

        if (success) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Profile updated successfully!");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Update failed. Check database.");
        }
    }
}