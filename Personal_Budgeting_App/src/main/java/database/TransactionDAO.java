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

    // ─── ADD TRANSACTION ─────────────────────────────────────────────────────

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
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── UPDATE TRANSACTION ──────────────────────────────────────────────────

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
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── DELETE TRANSACTION ──────────────────────────────────────────────────

    public boolean deleteTransaction(int transactionId) {
        String sql = "DELETE FROM Transactions WHERE TransactionId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── GET ALL TRANSACTIONS FOR USER ──────────────────────────────────────

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
                    Income inc = new Income(
                            rs.getInt("TransactionId"),
                            rs.getInt("UserId"),
                            rs.getDouble("Amount"),
                            rs.getString("Description"),
                            rs.getDate("Date").toLocalDate()
                    );
                    list.add(inc);
                } else {
                    Expense exp = new Expense(
                            rs.getInt("TransactionId"),
                            rs.getInt("UserId"),
                            rs.getDouble("Amount"),
                            rs.getString("Description"),
                            rs.getDate("Date").toLocalDate(),
                            rs.getInt("CategoryId")
                    );
                    list.add(exp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ─── CATEGORY OPERATIONS ─────────────────────────────────────────────────

    public boolean addCategory(int userId, String name) {
        String sql = "INSERT INTO Categories (UserId, Name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM Categories WHERE CategoryId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Category> getCategoriesByUser(int userId) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE UserId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Category cat = new Category(
                        rs.getInt("CategoryId"),
                        rs.getInt("UserId"),
                        rs.getString("Name")
                );
                list.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}