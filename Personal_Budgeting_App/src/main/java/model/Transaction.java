package model;

import java.time.LocalDate;

public abstract class Transaction {
    private int transactionId;
    private int userId;
    private double amount;
    private String description;
    private LocalDate date;
    private String type;

    public Transaction() {}

    public Transaction(int transactionId, int userId, double amount, String description, LocalDate date, String type) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    public abstract boolean save();
    public abstract boolean update();
    public abstract boolean delete();

    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}