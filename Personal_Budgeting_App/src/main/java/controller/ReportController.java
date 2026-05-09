package controller;

import database.ReportDAO;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import model.Report;
import model.User;
import java.util.List;

public class ReportController {

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML private PieChart expensePieChart;
    @FXML private BarChart<String, Number> incomeExpenseBarChart;
    @FXML private Label insightLabel;

    @FXML
    public void initialize() {
        User currentUser = AuthController.getCurrentUser();

        if (currentUser != null) {
            int userId = currentUser.getUserId();

            loadExpensePieChart(userId);
            loadIncomeExpenseBarChart(userId);
            loadInsight(userId);

            List<Report> expenses = reportDAO.getExpensesByCategory(userId);
            System.out.println("Debug: Expenses by category count = " + expenses.size());
            for (Report r : expenses) {
                System.out.println(r.getLabel() + " = " + r.getTotalAmount());
            }
        } else {
            insightLabel.setText("Please login to see your reports.");
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
}