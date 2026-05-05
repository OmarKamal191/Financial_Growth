package view;

import controller.AuthController;
import controller.BudgetController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Budget;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class BudgetView {

    @FXML private TextField amountField;
    @FXML private TextField thresholdField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label statusLabel;

    @FXML private Label totalBudgetLabel;
    @FXML private Label spentLabel;
    @FXML private Label remainingLabel;
    @FXML private Label periodLabel;
    @FXML private Label percentLabel;
    @FXML private ProgressBar budgetProgressBar;

    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, Double> colAmount;
    @FXML private TableColumn<Budget, Double> colSpent;
    @FXML private TableColumn<Budget, Double> colRemaining;
    @FXML private TableColumn<Budget, LocalDate> colStart;
    @FXML private TableColumn<Budget, LocalDate> colEnd;

    private BudgetController budgetController = new BudgetController();

    // تأكد أن هذا الـ ID هو رقم 1 كما هو مسجل في قاعدة بياناتك حالياً
    private int currentUserId ;

    @FXML
    public void initialize() {
        // ربط الأعمدة بأسماء المتغيرات في كلاس Budget.java بالظبط
        if (AuthController.getCurrentUser() != null) {
            this.currentUserId = AuthController.getCurrentUser().getUserId();
        }
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colSpent.setCellValueFactory(new PropertyValueFactory<>("spentAmount"));

        // حساب الـ Remaining للجدول
        colRemaining.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().calculateRemaining()).asObject());

        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // تحميل البيانات فوراً عند فتح الصفحة
        refreshUI();

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusMonths(1));
    }

    private void refreshUI() {
        try {
            // جلب البيانات من الـ DAO باستخدام الـ UserId
            List<Budget> allBudgets = budgetController.getAllBudgets(currentUserId);
            ObservableList<Budget> observableBudgets = FXCollections.observableArrayList(allBudgets);

            // تحديث الجدول
            budgetTable.setItems(observableBudgets);
            budgetTable.refresh();

            // تحديث الـ Active Budget
            Budget active = budgetController.getActiveBudget(currentUserId);
            if (active != null) {
                updateActiveInfo(active);
            } else {
                clearActiveInfo();
            }
        } catch (Exception e) {
            System.err.println("Error refreshing UI: " + e.getMessage());
        }
    }

    private void updateActiveInfo(Budget active) {
        double remaining = active.calculateRemaining();
        totalBudgetLabel.setText("Total Budget: $" + String.format("%.2f", active.getAmount()));
        spentLabel.setText("Spent: $" + String.format("%.2f", active.getSpentAmount()));
        remainingLabel.setText("Remaining: $" + String.format("%.2f", remaining));
        remainingLabel.setTextFill(remaining >= 0 ? Color.GREEN : Color.RED);
        periodLabel.setText("Period: " + active.getStartDate() + " to " + active.getEndDate());

        double progress = active.getAmount() > 0 ? (active.getSpentAmount() / active.getAmount()) : 0;
        budgetProgressBar.setProgress(Math.min(progress, 1.0));
        percentLabel.setText(String.format("%.1f%% used", progress * 100));
        percentLabel.setTextFill(Color.BLACK);
    }

    @FXML
    private void handleCreateBudget(ActionEvent event) {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            int threshold = Integer.parseInt(thresholdField.getText().trim());
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();

            boolean success = budgetController.createBudget(currentUserId, amount, start, end, threshold);
            if (success) {
                statusLabel.setText("✓ Success!");
                statusLabel.setTextFill(Color.GREEN);
                refreshUI(); // مناداة التحديث فور الإضافة
            } else {
                statusLabel.setText("✗ Failed to save.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            statusLabel.setText("✗ Invalid Input!");
            statusLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleDeleteBudget(ActionEvent event) {
        Budget selected = budgetTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (budgetController.deleteBudget(selected.getBudgetId())) {
                refreshUI();
            }
        }
    }

    private void clearActiveInfo() {
        totalBudgetLabel.setText("Total Budget: —");
        spentLabel.setText("Spent: —");
        remainingLabel.setText("Remaining: —");
        budgetProgressBar.setProgress(0);
        percentLabel.setText("No active budget");
    }

    @FXML
    private void backToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}