package controller;

import database.NotificationDAO;
import model.Notification;
import model.Notification.NotificationType;

import java.util.List;

public class NotificationController {

    private static NotificationController instance;
    private NotificationDAO notificationDAO;

    private NotificationController() {
        this.notificationDAO = new NotificationDAO();
    }

    public static NotificationController getInstance() {
        if (instance == null) {
            instance = new NotificationController();
        }
        return instance;
    }

    public Notification createNotification(int userId, NotificationType type) {
        String message = buildMessage(type);
        Notification notification = new Notification(userId, message, type);
        notification.sendNotification();
        boolean saved = notificationDAO.saveNotification(notification);
        if (saved) {
            return notification;
        }
        return null;
    }

    public Notification createNotification(int userId, NotificationType type, String customMessage) {
        Notification notification = new Notification(userId, customMessage, type);
        notification.sendNotification();
        boolean saved = notificationDAO.saveNotification(notification);
        return saved ? notification : null;
    }

    public List<Notification> getNotifications(int userId) {
        return notificationDAO.getNotificationsByUser(userId);
    }

    public List<Notification> getUnreadNotifications(int userId) {
        return notificationDAO.getUnreadNotifications(userId);
    }

    public boolean markAsRead(int notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }

    public boolean markAllAsRead(int userId) {
        return notificationDAO.markAllAsRead(userId);
    }

    public int getUnreadCount(int userId) {
        return notificationDAO.getUnreadNotifications(userId).size();
    }

    private String buildMessage(NotificationType type) {
        switch (type) {
            case WARNING:    return "Warning: You are approaching your budget limit!";
            case EXCEEDED:   return "Alert: You have exceeded your budget limit!";
            case GOAL_ACHIEVED: return "Congratulations! You have achieved your financial goal!";
            case GOAL_EXPIRED:  return "Your financial goal has expired without being achieved.";
            default:         return "You have a new notification.";
        }
    }
}
