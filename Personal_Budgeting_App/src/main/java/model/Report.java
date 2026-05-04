package model;

public class Report {
    private String label;
    private double totalAmount;

    public Report(String label, double totalAmount) {
        this.label = label;
        this.totalAmount = totalAmount;
    }

    public String getLabel() {
        return label;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}