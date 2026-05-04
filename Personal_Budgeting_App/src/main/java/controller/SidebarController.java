package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SidebarController {

    @FXML private Button btnDashboard;
    @FXML private Button btnTransactions;
    @FXML private Button btnBudgets;
    @FXML private Button btnGoals;
    @FXML private Button btnReports;
    @FXML private Button btnNotifications;
    @FXML private Button btnProfile;

    // ── Styles ──────────────────────────────────────────────
    private static final String STYLE_ACTIVE =
            "-fx-background-color: white;" +
                    "-fx-background-radius: 4;" +
                    "-fx-text-fill: #159447;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;";

    private static final String STYLE_INACTIVE =
            "-fx-background-color: transparent;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: normal;" +
                    "-fx-cursor: hand;";

    // Which page is currently active (shared across instances)
    private static String currentPage = "Dashboard";

    // ── Init ────────────────────────────────────────────────
    @FXML
    public void initialize() {
        List<Button> allButtons = List.of(
                btnDashboard, btnTransactions, btnBudgets,
                btnGoals, btnReports, btnNotifications, btnProfile
        );

        // Set all to inactive first
        allButtons.forEach(b -> b.setStyle(STYLE_INACTIVE));

        // Highlight the current active page
        getButtonForPage(currentPage).setStyle(STYLE_ACTIVE);

        // Hover effect for inactive buttons
        allButtons.forEach(btn -> {
            btn.setOnMouseEntered(e -> {
                if (!btn.getStyle().contains("#159447")) {
                    btn.setStyle(STYLE_INACTIVE +
                            "-fx-background-color: rgba(255,255,255,0.15);");
                }
            });
            btn.setOnMouseExited(e -> {
                if (!btn.getStyle().contains("#159447")) {
                    btn.setStyle(STYLE_INACTIVE);
                }
            });
        });
    }

    private Button getButtonForPage(String page) {
        return switch (page) {
            case "Transactions"  -> btnTransactions;
            case "Budgets"       -> btnBudgets;
            case "Goals"         -> btnGoals;
            case "Reports"       -> btnReports;
            case "Notifications" -> btnNotifications;
            case "Profile"       -> btnProfile;
            default              -> btnDashboard;
        };
    }

    // ── Navigation helpers ───────────────────────────────────
    private void navigateTo(String page, String fxmlPath) {
        currentPage = page;
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Actions ─────────────────────────────────────────────
    @FXML public void openDashboard()     { navigateTo("Dashboard",     "/view/Dashboard.fxml"); }
    @FXML public void openTransactions()  { navigateTo("Transactions",  "/view/Transaction.fxml"); }
    @FXML public void openBudgets()       { navigateTo("Budgets",       "/view/Budget.fxml"); }
    @FXML public void openGoals()         { navigateTo("Goals",         "/view/Goal.fxml"); }
    @FXML public void openReports()       { navigateTo("Reports",       "/view/Report.fxml"); }
    @FXML public void openNotifications() { navigateTo("Notifications", "/view/Notification.fxml"); }
    @FXML public void openProfile()       { navigateTo("Profile",       "/view/Profile.fxml"); }

    @FXML
    public void logout() {
        currentPage = "Dashboard";
        navigateTo("Dashboard", "/view/Login.fxml");
    }
}