package database;

import model.Category;
import model.Expense;
import model.Income;
import model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // ─── ADD TRANSACTION (Modified to update Budget) ──────────────────────────
    public boolean addTransaction(int userId, double amount, String description, LocalDate date, String type, Integer categoryId) {
        String sql = "INSERT INTO Transactions (UserId, Amount, Description, Date, Type, CategoryId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, description);
            pstmt.setDate(4, Date.valueOf(date));
            pstmt.setString(5, type);
            if (categoryId != null) pstmt.setInt(6, categoryId);
            else pstmt.setNull(6, Types.INTEGER);

            boolean success = pstmt.executeUpdate() > 0;

            if (success && "EXPENSE".equalsIgnoreCase(type) && categoryId != null) {
                updateBudgetSpending(userId, categoryId, amount, date);
            }

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateBudgetSpending(int userId, int categoryId, double amount, LocalDate date) {
        String sql = "UPDATE Budgets SET SpentAmount = SpentAmount + ? " +
                "WHERE UserId = ? AND CategoryId = ? " +
                "AND ? BETWEEN StartDate AND EndDate";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, categoryId);
            pstmt.setDate(4, Date.valueOf(date));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating budget spending: " + e.getMessage());
        }
    }

    // ─── DELETE TRANSACTION (Modified to revert Budget) ────────────────────────
    public boolean deleteTransaction(int transactionId) {
        String getSql = "SELECT UserId, CategoryId, Amount, Type, Date FROM Transactions WHERE TransactionId = ?";
        String deleteSql = "DELETE FROM Transactions WHERE TransactionId = ?";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(getSql)) {
                pstmt.setInt(1, transactionId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && "EXPENSE".equalsIgnoreCase(rs.getString("Type"))) {
                    updateBudgetSpending(rs.getInt("UserId"), rs.getInt("CategoryId"), -rs.getDouble("Amount"), rs.getDate("Date").toLocalDate());
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, transactionId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTransaction(int transactionId, double amount, String description, LocalDate date, Integer categoryId) {
        String sql = "UPDATE Transactions SET Amount = ?, Description = ?, Date = ?, CategoryId = ? WHERE TransactionId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, description);
            pstmt.setDate(3, Date.valueOf(date));
            if (categoryId != null) pstmt.setInt(4, categoryId);
            else pstmt.setNull(4, Types.INTEGER);
            pstmt.setInt(5, transactionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Transaction> getTransactionsByUser(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE UserId = ? ORDER BY Date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("Type");
                if ("INCOME".equals(type)) {
                    list.add(new Income(rs.getInt("TransactionId"), rs.getInt("UserId"), rs.getDouble("Amount"), rs.getString("Description"), rs.getDate("Date").toLocalDate()));
                } else {
                    list.add(new Expense(rs.getInt("TransactionId"), rs.getInt("UserId"), rs.getDouble("Amount"), rs.getString("Description"), rs.getDate("Date").toLocalDate(), rs.getInt("CategoryId")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addCategory(int userId, String name) {
        String sql = "INSERT INTO Categories (UserId, Name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Category> getCategoriesByUser(int userId) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE UserId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Category(rs.getInt("CategoryId"), rs.getInt("UserId"), rs.getString("Name")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Transaction> getFilteredTransactions(int userId, String type, Integer categoryId) {
        List<Transaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Transactions WHERE UserId = ?");
        if (type != null && !type.equals("All Types")) sql.append(" AND Type = ?");
        if (categoryId != null && categoryId > 0) sql.append(" AND CategoryId = ?");
        sql.append(" ORDER BY Date DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1; pstmt.setInt(idx++, userId);
            if (type != null && !type.equals("All Types")) pstmt.setString(idx++, type);
            if (categoryId != null && categoryId > 0) pstmt.setInt(idx++, categoryId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String t = rs.getString("Type");
                if ("INCOME".equals(t)) {
                    list.add(new Income(rs.getInt("TransactionId"), rs.getInt("UserId"), rs.getDouble("Amount"), rs.getString("Description"), rs.getDate("Date").toLocalDate()));
                } else {
                    list.add(new Expense(rs.getInt("TransactionId"), rs.getInt("UserId"), rs.getDouble("Amount"), rs.getString("Description"), rs.getDate("Date").toLocalDate(), rs.getInt("CategoryId")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteCategory(int categoryId) {
        String updateTransactionsSql = "UPDATE Transactions SET CategoryId = NULL WHERE CategoryId = ?";
        String updateBudgetsSql = "UPDATE Budgets SET CategoryId = NULL WHERE CategoryId = ?";
        String deleteCategorySql = "DELETE FROM Categories WHERE CategoryId = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt1 = conn.prepareStatement(updateTransactionsSql)) {
                pstmt1.setInt(1, categoryId);
                pstmt1.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(updateBudgetsSql)) {
                pstmt2.setInt(1, categoryId);
                pstmt2.executeUpdate();
            }

            try (PreparedStatement pstmt3 = conn.prepareStatement(deleteCategorySql)) {
                pstmt3.setInt(1, categoryId);
                int rowsDeleted = pstmt3.executeUpdate();

                if (rowsDeleted > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}