package model;

public class Dashboard {

    private double income;
    private double expense;
    private double balance;
    private int transactions;
    private int budgets;
    private int goals;

    public Dashboard(double income, double expense, double balance,
                     int transactions, int budgets, int goals) {

        this.income = income;
        this.expense = expense;
        this.balance = balance;
        this.transactions = transactions;
        this.budgets = budgets;
        this.goals = goals;
    }

    public double getTotalIncome() { return income; }
    public double getTotalExpense() { return expense; }
    public double getBalance() { return balance; }
    public int getTransactionsCount() { return transactions; }
    public int getBudgetsCount() { return budgets; }
    public int getGoalsCount() { return goals; }
}