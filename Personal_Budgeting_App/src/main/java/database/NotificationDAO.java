package database;

import model.Notification;
import model.Notification.NotificationType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private Connection connection;

    public NotificationDAO() {
        try {
            this.connection = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Error: Could not establish connection in NotificationDAO");
            e.printStackTrace();
        }
    }

    public boolean saveNotification(Notification notification) {
        String sql = "INSERT INTO Notifications (UserId, Message, Type, IsRead, CreatedAt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getType().name());
            stmt.setBoolean(4, notification.isRead());
            stmt.setTimestamp(5, Timestamp.valueOf(notification.getTimestamp()));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) notification.setNotificationId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("saveNotification failed: " + e.getMessage());
        }
        return false;
    }

    public List<Notification> getNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE UserId = ? ORDER BY CreatedAt DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapRowToNotification(rs));
            }
        } catch (SQLException e) {
            System.err.println("getNotificationsByUser failed: " + e.getMessage());
        }
        return notifications;
    }

    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE UserId = ? AND IsRead = 0 ORDER BY CreatedAt DESC";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapRowToNotification(rs));
            }
        } catch (SQLException e) {
            System.err.println("getUnreadNotifications failed: " + e.getMessage());
        }
        return notifications;
    }

    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET IsRead = 1 WHERE NotificationId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("markAsRead failed: " + e.getMessage());
        }
        return false;
    }

    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE Notifications SET IsRead = 1 WHERE UserId = ? AND IsRead = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("markAllAsRead failed: " + e.getMessage());
        }
        return false;
    }

    private Notification mapRowToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(rs.getInt("NotificationId"));
        n.setUserId(rs.getInt("UserId"));
        n.setMessage(rs.getString("Message"));
        n.setType(NotificationType.valueOf(rs.getString("Type")));
        n.setRead(rs.getBoolean("IsRead"));
        n.setTimestamp(rs.getTimestamp("CreatedAt").toLocalDateTime());
        return n;
    }
}