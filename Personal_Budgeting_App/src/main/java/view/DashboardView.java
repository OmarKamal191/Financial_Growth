package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class DashboardView {

    public static Parent load() {
        try {
            return FXMLLoader.load(
                    DashboardView.class.getResource("/view/Dashboard.fxml")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}