package controller;

import database.DashboardDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Dashboard;

import java.io.IOException;

public class DashboardController {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @FXML private Label welcomeLabel;
    @FXML private Label balanceLabel;
    @FXML private Label incomeLabel;
    @FXML private Label expenseLabel;
    @FXML private Label transactionsCountLabel;
    @FXML private Label budgetsCountLabel;
    @FXML private Label goalsCountLabel;

    @FXML
    public void initialize() {
        loadDashboardData(1);
    }

    private void loadDashboardData(int userId) {
        Dashboard dashboard = dashboardDAO.getDashboardData(userId);

        welcomeLabel.setText("Hello, User");

        incomeLabel.setText(formatMoney(dashboard.getTotalIncome()));
        expenseLabel.setText(formatMoney(dashboard.getTotalExpense()));
        balanceLabel.setText(formatMoney(dashboard.getBalance()));

        transactionsCountLabel.setText(String.valueOf(dashboard.getTransactionsCount()));
        budgetsCountLabel.setText(String.valueOf(dashboard.getBudgetsCount()));
        goalsCountLabel.setText(String.valueOf(dashboard.getGoalsCount()));
    }

    @FXML
    private void openDashboard(ActionEvent event) throws IOException {
        switchScene(event, "/view/Dashboard.fxml");
    }

    @FXML
    private void openReports(ActionEvent event) throws IOException {
        switchScene(event, "/view/Report.fxml");
    }

    @FXML
    private void openNotifications(ActionEvent event) throws IOException {
        switchScene(event, "/view/Notification.fxml");
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        switchScene(event, "/view/Login.fxml");
    }

    private void switchScene(ActionEvent event, String path) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(path));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private String formatMoney(double amount) {
        return String.format("$%.2f", amount);
    }
}