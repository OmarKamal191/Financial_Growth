package model;

import java.time.LocalDate;

public class Expense extends Transaction {
    private int categoryId;

    public Expense() {
        super();
        setType("EXPENSE");
    }

    public Expense(int transactionId, int userId, double amount, String description, LocalDate date, int categoryId) {
        super(transactionId, userId, amount, description, date, "EXPENSE");
        this.categoryId = categoryId;
    }

    public void addExpense(double amount, int categoryId) {
        setAmount(amount);
        this.categoryId = categoryId;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public boolean update() {
        return true;
    }

    @Override
    public boolean delete() {
        return true;
    }
}