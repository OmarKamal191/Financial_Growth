package view;

import controller.AuthController;
import controller.BudgetController;
import database.TransactionDAO;
import model.Category;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import model.Budget;

import java.time.LocalDate;
import java.util.List;

public class BudgetView {

    @FXML private ComboBox<Category> categoryComboBox;
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
    @FXML private TableColumn<Budget, String> colCategory;
    @FXML private TableColumn<Budget, Double> colAmount;
    @FXML private TableColumn<Budget, Double> colSpent;
    @FXML private TableColumn<Budget, Double> colRemaining;
    @FXML private TableColumn<Budget, LocalDate> colStart;
    @FXML private TableColumn<Budget, LocalDate> colEnd;

    private BudgetController budgetController = new BudgetController();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private int currentUserId;

    @FXML
    public void initialize() {
        if (AuthController.getCurrentUser() != null) {
            this.currentUserId = AuthController.getCurrentUser().getUserId();
        }

        loadCategories();

        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colSpent.setCellValueFactory(new PropertyValueFactory<>("spentAmount"));
        colRemaining.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().calculateRemaining()).asObject());
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        budgetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        budgetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateActiveInfo(newSelection);
            }
        });

        refreshUI();

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusMonths(1));
    }

    private void loadCategories() {
        List<Category> categories = transactionDAO.getCategoriesByUser(currentUserId);
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));

        categoryComboBox.setConverter(new javafx.util.StringConverter<Category>() {
            @Override public String toString(Category object) { return object == null ? "" : object.getName(); }
            @Override public Category fromString(String string) { return null; }
        });
    }

    private void refreshUI() {
        List<Budget> allBudgets = budgetController.getAllBudgets(currentUserId);
        if (allBudgets != null) {
            budgetTable.setItems(FXCollections.observableArrayList(allBudgets));
        }

        Budget active = budgetController.getActiveBudget(currentUserId);
        if (active != null) {
            updateActiveInfo(active);
        } else {
            clearActiveInfo();
        }
    }

    private void updateActiveInfo(Budget active) {
        double spent = active.getSpentAmount();
        double total = active.getAmount();
        double remaining = active.calculateRemaining();

        totalBudgetLabel.setText("Total: $" + String.format("%.2f", total));
        spentLabel.setText("Spent: $" + String.format("%.2f", spent));
        remainingLabel.setText("Remaining: $" + String.format("%.2f", remaining));
        periodLabel.setText("Period: " + active.getStartDate() + " to " + active.getEndDate());

        if (total > 0) {
            double progress = spent / total;
            budgetProgressBar.setProgress(Math.min(progress, 1.0));
            percentLabel.setText(String.format("%.1f%% used", progress * 100));

            if (progress >= (active.getAlertThreshold() / 100.0)) {
                budgetProgressBar.setStyle("-fx-accent: red;");
            } else {
                budgetProgressBar.setStyle("-fx-accent: #159447;");
            }
        }
    }

    @FXML
    private void handleCreateBudget(ActionEvent event) {
        try {
            Category selected = categoryComboBox.getValue();
            if (selected == null) {
                statusLabel.setText("✗ Select Category!");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            double amount = Double.parseDouble(amountField.getText().trim());
            int threshold = Integer.parseInt(thresholdField.getText().trim());
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();

            boolean success = budgetController.createBudget(currentUserId, amount, selected.getCategoryId(), start, end, threshold);

            if (success) {
                statusLabel.setText("✓ Success!");
                statusLabel.setTextFill(Color.GREEN);
                refreshUI();
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
        if (selected != null && budgetController.deleteBudget(selected.getBudgetId())) {
            refreshUI();
        }
    }

    private void clearActiveInfo() {
        totalBudgetLabel.setText("Total Budget: —");
        spentLabel.setText("Spent: —");
        remainingLabel.setText("Remaining: —");
        periodLabel.setText("Period: —");
        budgetProgressBar.setProgress(0);
        percentLabel.setText("No active budget");
    }

}