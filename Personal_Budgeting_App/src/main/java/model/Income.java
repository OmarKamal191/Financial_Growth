package model;

import java.time.LocalDate;

public class Income extends Transaction {

    public Income() {
        super();
        setType("INCOME");
    }

    public Income(int transactionId, int userId, double amount, String description, LocalDate date) {
        super(transactionId, userId, amount, description, date, "INCOME");
    }

    @Override
    public boolean save() {
        // Handled via TransactionDAO
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