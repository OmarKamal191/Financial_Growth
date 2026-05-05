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
import model.Transaction;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.List;


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
    @FXML private VBox recentTransactionsBox;

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
        loadRecentTransactions(userId);
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
        SidebarController.setCurrentPage("Transactions");
        switchScene(actionEvent, "/view/Transaction.fxml");
    }

    private void loadRecentTransactions(int userId) {
        recentTransactionsBox.getChildren().clear();

        List<Transaction> recentTransactions = dashboardDAO.getRecentTrans(userId);

        for (Transaction t : recentTransactions) {
            recentTransactionsBox.getChildren().add(createTransactionRow(t));
        }
    }

    private HBox createTransactionRow(Transaction t) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 8 0 8 0;");

        Label icon = new Label(t.getType().equalsIgnoreCase("INCOME") ? "💼" : "💰");
        icon.setMinSize(50, 50);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-background-color: #E8F5E9; -fx-background-radius: 12; -fx-font-size: 22px;");

        VBox info = new VBox(4);

        Label title = new Label(t.getDescription());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label details = new Label(t.getType() + " • " + t.getDate());
        details.setStyle("-fx-font-size: 13px; -fx-text-fill: #777;");

        info.getChildren().addAll(title, details);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label amount = new Label(
                (t.getType().equalsIgnoreCase("INCOME") ? "+" : "-") + formatMoney(t.getAmount())
        );

        amount.setStyle(
                t.getType().equalsIgnoreCase("INCOME")
                        ? "-fx-text-fill: #159447; -fx-font-size: 17px; -fx-font-weight: bold;"
                        : "-fx-text-fill: red; -fx-font-size: 17px; -fx-font-weight: bold;"
        );

        row.getChildren().addAll(icon, info, spacer, amount);

        return row;
    }
}