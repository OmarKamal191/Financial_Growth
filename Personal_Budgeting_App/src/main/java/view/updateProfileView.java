package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;

public class updateProfileView {

    public static Parent load() {
        try {
            URL fxmlLocation = updateProfileView.class.getResource("/view/UpdateProfile.fxml");

            if (fxmlLocation == null) {
                throw new IOException("FXML file not found at /view/UpdateProfile.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            return loader.load();

        } catch (IOException e) {
            System.err.println("Error loading Update Profile View: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}