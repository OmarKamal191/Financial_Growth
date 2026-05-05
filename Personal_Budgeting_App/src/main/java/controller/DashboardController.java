package controller;

import database.DashboardDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Dashboard;
import model.Transaction;


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
    @FXML private Label userIconLabel;

    @FXML
    public void initialize() {
        loadDashboardData(AuthController.getCurrentUser().getUserId());
    }

    private void loadDashboardData(int userId) {
        Dashboard dashboard = dashboardDAO.getDashboardData(userId);

        welcomeLabel.setText("Hello, " + dashboardDAO.getUserName(userId));

        incomeLabel.setText(formatMoney(dashboard.getTotalIncome()));
        expenseLabel.setText(formatMoney(dashboard.getTotalExpense()));
        balanceLabel.setText(formatMoney(dashboard.getBalance()));

        transactionsCountLabel.setText(String.valueOf(dashboard.getTransactionsCount()));
        budgetsCountLabel.setText(String.valueOf(dashboard.getBudgetsCount()));
        goalsCountLabel.setText(String.valueOf(dashboard.getGoalsCount()));
        userIconLabel.setText(dashboardDAO.getFirstLetter(userId));
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

    public void seeAllTransactions(ActionEvent actionEvent) throws IOException {
        switchScene(actionEvent, "/view/Transaction.fxml");
    }
}