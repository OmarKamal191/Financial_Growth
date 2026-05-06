package model;

import java.time.LocalDate;

public class Budget {
    private int budgetId;
    private int userId;
    private double amount;
    private int CategoryId;
    private String categoryName; // أضفنا هذا الحقل
    private double spentAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private int alertThreshold;

    public Budget() {}

    public Budget(int userId, double amount, int categoryId, LocalDate startDate, LocalDate endDate, int alertThreshold) {
        this.userId = userId;
        this.amount = amount;
        this.CategoryId = categoryId; //
        this.spentAmount = 0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alertThreshold = alertThreshold;
    }

    public double calculateRemaining() {
        return amount - spentAmount;
    }

    // Getters and Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public int getCategoryId() { return CategoryId; }
    public void setCategoryId(int categoryId) { this.CategoryId = categoryId; }
    public double getSpentAmount() { return spentAmount; }
    public void setSpentAmount(double spentAmount) { this.spentAmount = spentAmount; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(int alertThreshold) { this.alertThreshold = alertThreshold; }
}