package database;

import model.Budget;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public BudgetDAO() {
    }

    // ─── CREATE BUDGET ───────────────────────────────────────────────────────
    public boolean createBudget(Budget budget) {
        String sql = "INSERT INTO Budgets (UserId, Amount, StartDate, EndDate, AlertThreshold, CategoryId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, budget.getUserId());
            stmt.setDouble(2, budget.getAmount());
            stmt.setDate(3, Date.valueOf(budget.getStartDate()));
            stmt.setDate(4, Date.valueOf(budget.getEndDate()));
            stmt.setInt(5, budget.getAlertThreshold());
            stmt.setInt(6, budget.getCategoryId());

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

    // ─── UPDATE SPENT AMOUNT ─────────────────────────────────────────────────
    public boolean updateSpentAmount(int budgetId, double spentAmount) {
        String sql = "UPDATE Budgets SET SpentAmount = ? WHERE BudgetId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, spentAmount);
            stmt.setInt(2, budgetId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateSpentAmount failed: " + e.getMessage());
        }
        return false;
    }

    // ─── GET BUDGETS BY USER (MODIFIED WITH JOIN) ──────────────────────────
    public List<Budget> getBudgetsByUser(int userId) {
        List<Budget> budgets = new ArrayList<>();
        // تم إضافة JOIN لجلب اسم الكاتيجوري
        String sql = "SELECT B.*, C.Name AS CategoryName FROM Budgets B " +
                "LEFT JOIN Categories C ON B.CategoryId = C.CategoryId " +
                "WHERE B.UserId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Budget b = mapRowToBudget(rs);
                    if (b != null) budgets.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("getBudgetsByUser failed: " + e.getMessage());
        }
        return budgets;
    }

    // ─── GET ACTIVE BUDGET (MODIFIED WITH JOIN) ────────────────────────────
    public Budget getActiveBudget(int userId) {
        // تعديل الـ SQL ليجيب أحدث ميزانية مضافة أولاً
        String sql = "SELECT TOP 1 B.*, C.Name AS CategoryName FROM Budgets B " +
                "LEFT JOIN Categories C ON B.CategoryId = C.CategoryId " +
                "WHERE B.UserId = ? AND CAST(GETDATE() AS DATE) BETWEEN B.StartDate AND B.EndDate " +
                "ORDER BY B.BudgetId DESC"; // الترتيب بالتنازلي لجلب الأحدث

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBudget(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("getActiveBudget failed: " + e.getMessage());
        }
        return null;
    }

    // ─── DELETE BUDGET ───────────────────────────────────────────────────────
    public boolean deleteBudget(int budgetId) {
        String sql = "DELETE FROM Budgets WHERE BudgetId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, budgetId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteBudget failed: " + e.getMessage());
        }
        return false;
    }

    // ─── MAP ROW TO BUDGET (Helper) ──────────────────────────────────────────
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
            b.setCategoryId(rs.getInt("CategoryId"));

            // الآن العمود CategoryName متاح بسبب الـ JOIN في الاستعلام
            b.setCategoryName(rs.getString("CategoryName"));

            return b;
        } catch (SQLException e) {
            System.err.println("Mapping error: " + e.getMessage());
            return null;
        }
    }
}