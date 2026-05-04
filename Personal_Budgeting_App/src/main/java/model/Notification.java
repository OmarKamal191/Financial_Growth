package model;

import java.time.LocalDateTime;

public class Notification {

    public enum NotificationType {
        WARNING, EXCEEDED, GOAL_ACHIEVED, GOAL_EXPIRED, INFO
    }

    private int notificationId;
    private int userId;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime timestamp;

    public Notification() {
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    public Notification(int userId, String message, NotificationType type) {
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    public void sendNotification() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Notification{id=" + notificationId + ", type=" + type + ", message='" + message + "', read=" + isRead + "}";
    }
}