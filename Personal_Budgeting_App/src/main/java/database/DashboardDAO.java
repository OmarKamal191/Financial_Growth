package database;

import model.Dashboard;
import java.sql.*;

public class DashboardDAO {

    public Dashboard getDashboardData(int userId) {

        double income = getTotal(userId, "income");
        double expense = getTotal(userId, "expense");
        int transactions = count("Transactions", userId);
        int budgets = count("Budgets", userId);
        int goals = count("Goals", userId);

        Dashboard dashboard = new Dashboard(income, expense, income - expense, transactions, budgets, goals);

        return dashboard;
    }

    private double getTotal(int userId, String type) {
        double total = 0;
        String sql = "SELECT COALESCE(SUM(Amount),0) FROM Transactions WHERE UserId=? AND LOWER(Type)=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, type.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    private int count(String table, int userId) {
        int c = 0;
        String sql = "SELECT COUNT(*) FROM [" + table + "] WHERE UserId=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                c = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }
}