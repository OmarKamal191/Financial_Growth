package view;

import controller.TransactionController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Category;
import model.Transaction;

import java.time.LocalDate;
import java.util.List;

public class TransactionView {

    // ─── Add Transaction Form ────────────────────────────────────────────────
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private Label messageLabel;

    // ─── Transaction Table ───────────────────────────────────────────────────
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Double> amountCol;
    @FXML private TableColumn<Transaction, String> descCol;
    @FXML private TableColumn<Transaction, LocalDate> dateCol;
    @FXML private TableColumn<Transaction, String> typeCol;

    // ─── Category Section ────────────────────────────────────────────────────
    @FXML private TextField categoryNameField;
    @FXML private ListView<String> categoryListView;

    private TransactionController transactionController = new TransactionController();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private ObservableList<String> categoryNames = FXCollections.observableArrayList();
    private List<Category> categories;

    // Called automatically by JavaFX after FXML loads
    @FXML
    public void initialize() {
        // Setup type combo
        if (typeCombo != null)
            typeCombo.setItems(FXCollections.observableArrayList("INCOME", "EXPENSE"));

        // Setup table columns
        if (amountCol != null) amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        if (descCol != null)   descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        if (dateCol != null)   dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        if (typeCol != null)   typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        if (transactionTable != null) transactionTable.setItems(transactionList);

        loadTransactions();
        loadCategories();
    }

    // ─── USER STORY 4: Add Transaction ───────────────────────────────────────

    @FXML
    private void handleAddTransaction() {
        String amountText = amountField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();
        String type = typeCombo.getValue();

        if (amountText == null || amountText.isBlank() || description == null || description.isBlank() || date == null || type == null) {
            showMessage("Please fill all fields.", "red");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showMessage("Amount must be a valid number.", "red");
            return;
        }

        Integer categoryId = null;
        if ("EXPENSE".equals(type) && categoryCombo.getValue() != null) {
            String selectedCat = categoryCombo.getValue();
            categoryId = categories.stream()
                    .filter(c -> c.getName().equals(selectedCat))
                    .map(Category::getCategoryId)
                    .findFirst().orElse(null);
        }

        boolean success = transactionController.addTransaction(amount, description, date, type, categoryId);
        if (success) {
            showMessage("Transaction added successfully!", "green");
            clearTransactionForm();
            loadTransactions();
        } else {
            showMessage("Failed to add transaction.", "red");
        }
    }

    // ─── USER STORY 5: Edit / Delete Transaction ──────────────────────────────

    @FXML
    private void handleEditTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Select a transaction to edit.", "red");
            return;
        }

        String amountText = amountField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();

        if (amountText == null || amountText.isBlank() || description == null || date == null) {
            showMessage("Please fill all fields.", "red");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showMessage("Amount must be a valid number.", "red");
            return;
        }

        Integer categoryId = null;
        if (categoryCombo.getValue() != null) {
            String selectedCat = categoryCombo.getValue();
            categoryId = categories.stream()
                    .filter(c -> c.getName().equals(selectedCat))
                    .map(Category::getCategoryId)
                    .findFirst().orElse(null);
        }

        boolean success = transactionController.editTransaction(selected.getTransactionId(), amount, description, date, categoryId);
        if (success) {
            showMessage("Transaction updated.", "green");
            loadTransactions();
        } else {
            showMessage("Failed to update transaction.", "red");
        }
    }

    @FXML
    private void handleDeleteTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Select a transaction to delete.", "red");
            return;
        }

        boolean success = transactionController.deleteTransaction(selected.getTransactionId());
        if (success) {
            showMessage("Transaction deleted.", "green");
            loadTransactions();
        } else {
            showMessage("Failed to delete transaction.", "red");
        }
    }

    // ─── USER STORY 6: Manage Categories ─────────────────────────────────────

    @FXML
    private void handleAddCategory() {
        String name = categoryNameField.getText();
        if (name == null || name.isBlank()) {
            showMessage("Category name cannot be empty.", "red");
            return;
        }
        boolean success = transactionController.addCategory(name);
        if (success) {
            showMessage("Category added.", "green");
            categoryNameField.clear();
            loadCategories();
        } else {
            showMessage("Failed to add category.", "red");
        }
    }

    @FXML
    private void handleDeleteCategory() {
        String selected = categoryListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Select a category to delete.", "red");
            return;
        }
        int categoryId = categories.stream()
                .filter(c -> c.getName().equals(selected))
                .map(Category::getCategoryId)
                .findFirst().orElse(-1);

        if (categoryId == -1) return;

        boolean success = transactionController.deleteCategory(categoryId);
        if (success) {
            showMessage("Category deleted.", "green");
            loadCategories();
        } else {
            showMessage("Failed to delete category.", "red");
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void loadTransactions() {
        transactionList.clear();
        transactionList.addAll(transactionController.getAllTransactions());
    }

    private void loadCategories() {
        categories = transactionController.getCategories();
        categoryNames.clear();
        categories.forEach(c -> categoryNames.add(c.getName()));
        if (categoryListView != null) categoryListView.setItems(categoryNames);
        if (categoryCombo != null) categoryCombo.setItems(categoryNames);
    }

    private void clearTransactionForm() {
        amountField.clear();
        descriptionField.clear();
        datePicker.setValue(null);
        typeCombo.setValue(null);
        categoryCombo.setValue(null);
    }

    private void showMessage(String text, String color) {
        if (messageLabel != null) {
            messageLabel.setText(text);
            messageLabel.setStyle("-fx-text-fill: " + color + ";");
        }
    }
}