package view;

import controller.NotificationController;
import model.Notification;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class NotificationView {

    private NotificationController notificationController;
    private int userId;

    public NotificationView(int userId) {
        this.userId = userId;
        this.notificationController = NotificationController.getInstance();
    }

    public Scene getScene(Stage stage) {
        VBox root = new VBox(16);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #1e1e2e;");

        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label title = new Label("🔔 Notifications");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#cdd6f4"));

        int unread = notificationController.getUnreadCount(userId);
        if (unread > 0) {
            Label badge = new Label(String.valueOf(unread));
            badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            badge.setTextFill(Color.web("#1e1e2e"));
            badge.setStyle("-fx-background-color: #f38ba8; -fx-background-radius: 12; -fx-padding: 2 8;");
            header.getChildren().addAll(title, badge);
        } else {
            header.getChildren().add(title);
        }

        Button markAllBtn = new Button("Mark All as Read");
        markAllBtn.setFont(Font.font("Segoe UI", 13));
        markAllBtn.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-background-radius: 8; -fx-padding: 6 14;");
        markAllBtn.setOnAction(e -> {
            notificationController.markAllAsRead(userId);
            refreshScene(stage);
        });

        HBox topBar = new HBox(20, header, markAllBtn);
        topBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox notificationsList = buildNotificationsList();

        root.getChildren().addAll(topBar, notificationsList);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #1e1e2e;");

        return new Scene(scrollPane, 700, 600);
    }

    private VBox buildNotificationsList() {
        VBox listBox = new VBox(10);

        List<Notification> notifications = notificationController.getNotifications(userId);

        if (notifications.isEmpty()) {
            Label noData = new Label("No notifications yet.");
            noData.setTextFill(Color.web("#6c7086"));
            noData.setFont(Font.font("Segoe UI", 14));
            listBox.getChildren().add(noData);
            return listBox;
        }

        for (Notification n : notifications) {
            listBox.getChildren().add(buildNotificationCard(n));
        }

        return listBox;
    }

    private HBox buildNotificationCard(Notification notification) {
        HBox card = new HBox(14);
        card.setPadding(new Insets(14));
        String bgColor = notification.isRead() ? "#313244" : "#3d3d5c";
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10;");
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label icon = new Label(getIcon(notification.getType()));
        icon.setFont(Font.font(20));

        VBox content = new VBox(4);
        Label msg = new Label(notification.getMessage());
        msg.setTextFill(Color.web(notification.isRead() ? "#bac2de" : "#cdd6f4"));
        msg.setFont(Font.font("Segoe UI", notification.isRead() ? FontWeight.NORMAL : FontWeight.BOLD, 14));
        msg.setWrapText(true);

        Label time = new Label(notification.getTimestamp().toString().replace("T", " ").substring(0, 16));
        time.setTextFill(Color.web("#6c7086"));
        time.setFont(Font.font("Segoe UI", 11));

        content.getChildren().addAll(msg, time);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button readBtn = null;
        if (!notification.isRead()) {
            readBtn = new Button("✓");
            readBtn.setStyle("-fx-background-color: #45475a; -fx-text-fill: #a6e3a1; -fx-background-radius: 6; -fx-padding: 4 8;");
            int id = notification.getNotificationId();
            readBtn.setOnAction(e -> notificationController.markAsRead(id));
        }

        card.getChildren().addAll(icon, content, spacer);
        if (readBtn != null) card.getChildren().add(readBtn);

        return card;
    }

    private String getIcon(Notification.NotificationType type) {
        switch (type) {
            case WARNING:        return "⚠️";
            case EXCEEDED:       return "🚨";
            case GOAL_ACHIEVED:  return "🏆";
            case GOAL_EXPIRED:   return "⏰";
            default:             return "ℹ️";
        }
    }

    private void refreshScene(Stage stage) {
        stage.setScene(getScene(stage));
    }
}