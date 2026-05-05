package database;

import model.Budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BudgetDAO {

    private Connection connection;

    public BudgetDAO() {
        try {
            this.connection = DBConnection.getConnection();
        } catch (SQLException e) {
            System.out.println("Error connecting to database.");
            e.printStackTrace();
        }
    }


    public boolean createBudget(Budget budget) {
        String sql = "INSERT INTO Budgets (UserId, Amount, StartDate, EndDate, AlertThreshold) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, budget.getUserId());
            stmt.setDouble(2, budget.getAmount());
            stmt.setDate(3, Date.valueOf(budget.getStartDate()));
            stmt.setDate(4, Date.valueOf(budget.getEndDate()));
            stmt.setInt(5, budget.getAlertThreshold());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) budget.setBudgetId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("createBudget failed: " + e.getMessage());
        }
        return false;
    }


    public boolean updateSpentAmount(int budgetId, double spentAmount) {
        String sql = "UPDATE Budgets SET SpentAmount = ? WHERE BudgetId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, spentAmount);
            stmt.setInt(2, budgetId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateSpentAmount failed: " + e.getMessage());
        }
        return false;
    }


    public List<Budget> getBudgetsByUser(int userId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM Budgets WHERE UserId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Budget b = mapRowToBudget(rs);
                if (b != null) budgets.add(b);
            }
        } catch (SQLException e) {
            System.err.println("getBudgetsByUser failed: " + e.getMessage());
        }
        return budgets;
    }


    public Budget getActiveBudget(int userId) {
        String sql = "SELECT * FROM Budgets WHERE UserId = ? AND EndDate >= CAST(GETDATE() AS DATE)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRowToBudget(rs);
        } catch (SQLException e) {
            System.err.println("getActiveBudget failed: " + e.getMessage());
        }
        return null;
    }


    public boolean deleteBudget(int budgetId) {
        String sql = "DELETE FROM Budgets WHERE BudgetId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, budgetId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteBudget failed: " + e.getMessage());
        }
        return false;
    }


    private Budget mapRowToBudget(ResultSet rs) {
        try {
            Budget b = new Budget();
            b.setBudgetId(rs.getInt("BudgetId"));
            b.setUserId(rs.getInt("UserId"));
            b.setAmount(rs.getDouble("Amount"));
            b.setSpentAmount(rs.getDouble("SpentAmount"));
            b.setStartDate(rs.getDate("StartDate").toLocalDate());
            b.setEndDate(rs.getDate("EndDate").toLocalDate());
            b.setAlertThreshold(rs.getInt("AlertThreshold"));
            return b;
        } catch (SQLException e) {
            System.err.println("mapRowToBudget - column error: " + e.getMessage());
            return null;
        } catch (NullPointerException e) {
            System.err.println("mapRowToBudget - null date in row: " + e.getMessage());
            return null;
        }
    }
}