package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class devTeamController {

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        switchScene(event, "/view/Login.fxml");
    }

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        switchScene(event, "/view/Register.fxml");
    }

    private void switchScene(ActionEvent event, String path) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}