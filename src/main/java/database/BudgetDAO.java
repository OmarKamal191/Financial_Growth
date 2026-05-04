package database;

import model.Budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Budget.
 * Handles all database operations: create, read, update, delete.
 */
public class BudgetDAO {

    private Connection connection;

    public BudgetDAO() {
        try {
            this.connection = DBConnection.getConnection();
        } catch (SQLException e) {
            System.out.println("خطأ: فشل الاتصال بقاعدة البيانات!");
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new budget into the database.
     * @param budget the budget to save
     * @return true if successful
     */
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

    /**
     * Updates spentAmount for a given budget.
     * @param budgetId the budget to update
     * @param spentAmount new spent amount
     * @return true if successful
     */
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

    /**
     * Retrieves all budgets for a given user.
     * @param userId the user's ID
     * @return list of Budget objects
     */
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

    /**
     * Retrieves the active budget for a user (EndDate >= today).
     * @param userId the user's ID
     * @return active Budget or null
     */
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

    /**
     * Deletes a budget by ID.
     * @param budgetId the budget to delete
     * @return true if successful
     */
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

    /**
     * Maps a ResultSet row to a Budget object.
     * @param rs the ResultSet positioned at a valid row
     * @return Budget object, or null if mapping fails
     */
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
