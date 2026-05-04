package controller;

import database.ReportDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Report;

import java.io.IOException;
import java.util.List;

public class ReportController {

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML private PieChart expensePieChart;
    @FXML private BarChart<String, Number> incomeExpenseBarChart;
    @FXML private CategoryAxis monthAxis;
    @FXML private NumberAxis amountAxis;
    @FXML private Label insightLabel;

    @FXML
    public void initialize() {
        int userId = 1;

        loadExpensePieChart(userId);
        loadIncomeExpenseBarChart(userId);
        loadInsight(userId);

        List<Report> expenses = reportDAO.getExpensesByCategory(1);
        System.out.println("Expenses by category = " + expenses.size());

        for (Report r : expenses) {
            System.out.println(r.getLabel() + " = " + r.getTotalAmount());
        }
    }

    private void loadExpensePieChart(int userId) {
        expensePieChart.getData().clear();

        List<Report> expenses = reportDAO.getExpensesByCategory(userId);

        for (Report report : expenses) {
            expensePieChart.getData().add(
                    new PieChart.Data(report.getLabel(), report.getTotalAmount())
            );
        }

        expensePieChart.setLegendVisible(true);
        expensePieChart.setLabelsVisible(true);
    }

    private void loadIncomeExpenseBarChart(int userId) {

        incomeExpenseBarChart.getData().clear();

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        List<Report> incomeData = reportDAO.getMonthlyData(userId, "income");
        List<Report> expenseData = reportDAO.getMonthlyData(userId, "expense");

        for (Report r : incomeData) {
            incomeSeries.getData().add(new XYChart.Data<>(r.getLabel(), r.getTotalAmount()));
        }

        for (Report r : expenseData) {
            expenseSeries.getData().add(new XYChart.Data<>(r.getLabel(), r.getTotalAmount()));
        }

        incomeExpenseBarChart.getData().addAll(incomeSeries, expenseSeries);
    }

    private void loadInsight(int userId) {
        List<Report> expenses = reportDAO.getExpensesByCategory(userId);

        if (expenses.isEmpty()) {
            insightLabel.setText("No expense data available yet.");
            return;
        }

        Report highest = expenses.get(0);

        for (Report report : expenses) {
            if (report.getTotalAmount() > highest.getTotalAmount()) {
                highest = report;
            }
        }

        insightLabel.setText(
                "💡 You spent the most on " + highest.getLabel() +
                        ". Consider reviewing this category."
        );
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
}