package controller;

import database.UserDAO;
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

    @FXML
    private void backToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}