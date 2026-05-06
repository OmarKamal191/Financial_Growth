package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ReportView {

    public static Parent load() {
        try {
            return FXMLLoader.load(
                    ReportView.class.getResource("/view/Report.fxml")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}