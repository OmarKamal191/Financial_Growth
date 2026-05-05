package database;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(String name, String email, String password) {
        String sql = "INSERT INTO Users (Name, Email, PasswordHash, Balance) VALUES (?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND PasswordHash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setName(rs.getString("Name"));
                user.setEmail(rs.getString("Email"));
                user.setBalance(rs.getDouble("Balance"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(int userId, String newName, String newEmail, String newPassword) {
        StringBuilder sql = new StringBuilder("UPDATE Users SET ");
        boolean first = true;

        if (!newName.isEmpty()) {
            sql.append("Name = ?");
            first = false;
        }
        if (!newEmail.isEmpty()) {
            if (!first) sql.append(", ");
            sql.append("Email = ?");
            first = false;
        }
        if (!newPassword.isEmpty()) {
            if (!first) sql.append(", ");
            sql.append("Password = ?");
        }

        sql.append(" WHERE UserId = ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (!newName.isEmpty()) stmt.setString(paramIndex++, newName);
            if (!newEmail.isEmpty()) stmt.setString(paramIndex++, newEmail);
            if (!newPassword.isEmpty()) stmt.setString(paramIndex++, newPassword);

            stmt.setInt(paramIndex, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE UserId = ?"; // تأكد أن اسم العمود UserId صح في جدولك

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserId"));
                user.setName(rs.getString("Name"));
                user.setEmail(rs.getString("Email"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // لو اليوزر مش موجود
    }
}