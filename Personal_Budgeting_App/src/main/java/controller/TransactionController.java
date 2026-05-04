package controller;

import controller.AuthController;
import database.TransactionDAO;
import model.Category;
import model.Transaction;

import java.time.LocalDate;
import java.util.List;

public class TransactionController {

    private TransactionDAO transactionDAO = new TransactionDAO();

    // ─── USER STORY 4: ADD TRANSACTION ───────────────────────────────────────

    public boolean addTransaction(double amount, String description, LocalDate date, String type, Integer categoryId) {
        int userId = AuthController.getCurrentUser().getUserId();
        if (amount <= 0 || description == null || description.isBlank() || date == null || type == null) {
            return false;
        }
        return transactionDAO.addTransaction(userId, amount, description, date, type, categoryId);
    }

    // ─── USER STORY 5: EDIT / DELETE TRANSACTION ─────────────────────────────

    public boolean editTransaction(int transactionId, double amount, String description, LocalDate date, Integer categoryId) {
        if (amount <= 0 || description == null || description.isBlank() || date == null) {
            return false;
        }
        return transactionDAO.updateTransaction(transactionId, amount, description, date, categoryId);
    }

    public boolean deleteTransaction(int transactionId) {
        return transactionDAO.deleteTransaction(transactionId);
    }

    public List<Transaction> getAllTransactions() {
        int userId = AuthController.getCurrentUser().getUserId();
        return transactionDAO.getTransactionsByUser(userId);
    }

    // ─── USER STORY 6: MANAGE CATEGORIES ─────────────────────────────────────

    public boolean addCategory(String name) {
        if (name == null || name.isBlank()) return false;
        int userId = AuthController.getCurrentUser().getUserId();
        return transactionDAO.addCategory(userId, name);
    }

    public boolean deleteCategory(int categoryId) {
        return transactionDAO.deleteCategory(categoryId);
    }

    public List<Category> getCategories() {
        int userId = AuthController.getCurrentUser().getUserId();
        return transactionDAO.getCategoriesByUser(userId);
    }
}