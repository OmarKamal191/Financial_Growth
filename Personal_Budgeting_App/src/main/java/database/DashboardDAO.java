package database;

import model.Dashboard;
import model.Expense;
import model.Income;
import model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {
    public String getUserName(int userId) {
        String userName = "Guest"; // قيمة افتراضية في حال لم يجد المستخدم
        String query = "SELECT Name FROM Users WHERE userid = ?";

        // استخدام try-with-resources لضمان إغلاق الاتصال تلقائياً
        try (Connection conn = database.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // ربط المعامل (userId) بمكان علامة الاستهام في الاستعلام
            pstmt.setInt(1, userId);

            // تنفيذ الاستعلام والحصول على النتائج
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // استخراج قيمة العمود "Name"
                    userName = rs.getString("Name");
                }
            }
        } catch (SQLException e) {
            // طباعة الخطأ في حال حدوث مشكلة في قاعدة البيانات
            System.err.println("Error fetching user name: " + e.getMessage());
            e.printStackTrace();
        }

        return userName;
    }

    public String getFirstLetter(int userId) {
        String firstTwoLetters = "";
        // 1. أضفنا شرط الـ WHERE لاستخدام الـ ID
        String query = "SELECT SUBSTRING(Name, 1, 2) AS FirstLetter FROM Users WHERE userid = ?;";

        try (Connection conn = database.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 2. ربط الـ ID بالاستعلام بشكل صحيح
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 3. جلب القيمة باستخدام الاسم المستعار (Alias) الذي وضعناه في الاستعلام
                    firstTwoLetters = rs.getString("FirstLetter");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching first two letters: " + e.getMessage());
            e.printStackTrace();
        }
        return firstTwoLetters;
    }

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

    public List<Transaction> getRecentTrans(int userId) {
        List<Transaction> list = new ArrayList<>();
        // التعديل: أضفنا * و LIMIT 3 لضمان جلب آخر 3 عمليات فقط
        String sql = "SELECT * FROM Transactions WHERE UserId = ? ORDER BY Date DESC LIMIT 3";

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
            System.err.println("Error fetching recent transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}