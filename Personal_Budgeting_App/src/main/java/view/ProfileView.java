package view;

import controller.AuthController;
import database.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;
import java.io.IOException;

public class ProfileView {

    @FXML private Label nameLabel;
    @FXML private Label emailLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        if (AuthController.getCurrentUser() != null) {
            int userId = AuthController.getCurrentUser().getUserId();
            loadUserData(userId);
        }
    }

    private void loadUserData(int userId) {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
        }
    }

    @FXML
    private void goToUpdateProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/updateProfile.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}