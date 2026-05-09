package model;

import java.time.LocalDate;

public class Expense extends Transaction {
    private int categoryId;

    public Expense(int transactionId, int userId, double amount, String description, LocalDate date, int categoryId) {
        super(transactionId, userId, amount, description, date, "EXPENSE");
        this.categoryId = categoryId;
    }

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