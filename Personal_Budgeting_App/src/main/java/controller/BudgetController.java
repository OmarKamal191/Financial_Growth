package controller;

import database.BudgetDAO;
import model.Budget;
import java.time.LocalDate;
import java.util.List;

public class BudgetController {
    private BudgetDAO budgetDAO;

    public BudgetController() {
        this.budgetDAO = new BudgetDAO();
    }

    public boolean createBudget(int userId, double amount, int categoryId, LocalDate startDate, LocalDate endDate, int alertThreshold) {
        if (amount <= 0) return false;
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) return false;
        if (alertThreshold < 0 || alertThreshold > 100) alertThreshold = 80;

        Budget budget = new Budget(userId, amount, categoryId, startDate, endDate, alertThreshold);
        budget.setCategoryId(categoryId);

        return budgetDAO.createBudget(budget);
    }

    public Budget getActiveBudget(int userId) {
        return budgetDAO.getActiveBudget(userId);
    }

    public List<Budget> getAllBudgets(int userId) {
        return budgetDAO.getBudgetsByUser(userId);
    }

    public boolean deleteBudget(int budgetId) {
        return budgetDAO.deleteBudget(budgetId);
    }

    public void handleExpenseAdded(int userId, double expenseAmount) {
        Budget budget = budgetDAO.getActiveBudget(userId);
        if (budget == null) return;
        double newSpent = budget.getSpentAmount() + expenseAmount;
        budget.setSpentAmount(newSpent);
        budgetDAO.updateSpentAmount(budget.getBudgetId(), newSpent);
    }
}