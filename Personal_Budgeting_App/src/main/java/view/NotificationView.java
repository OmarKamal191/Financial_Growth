package view;

import controller.NotificationController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Notification;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationView {

    @FXML private Label unreadBadge;
    @FXML private Label summaryLabel;
    @FXML private TableView<Notification> notificationTable;
    @FXML private TableColumn<Notification, Notification.NotificationType> colType;
    @FXML private TableColumn<Notification, String> colMessage;
    @FXML private TableColumn<Notification, LocalDateTime> colTime;
    @FXML private TableColumn<Notification, Boolean> colRead;

    private NotificationController notificationController = NotificationController.getInstance();
    private int currentUserId = 1;

    @FXML
    public void initialize() {
        setupTable();
        refreshUI();
    }

    private void setupTable() {
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colRead.setCellValueFactory(new PropertyValueFactory<>("read"));

        notificationTable.setRowFactory(tv -> new TableRow<Notification>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (!item.isRead()) {
                    setStyle("-fx-background-color: #f0f0f0; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    @FXML
    private void handleMarkAllRead(ActionEvent event) {
        notificationController.markAllAsRead(currentUserId);
        refreshUI();
    }

    @FXML
    private void handleMarkSelectedRead(ActionEvent event) {
        Notification selected = notificationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            notificationController.markAsRead(selected.getNotificationId());
            refreshUI();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshUI();
    }

    private void refreshUI() {
        List<Notification> notifications = notificationController.getNotifications(currentUserId);
        ObservableList<Notification> observableList = FXCollections.observableArrayList(notifications);
        notificationTable.setItems(observableList);

        int unreadCount = notificationController.getUnreadCount(currentUserId);
        if (unreadCount > 0) {
            unreadBadge.setText(unreadCount + " New");
            unreadBadge.setVisible(true);
        } else {
            unreadBadge.setVisible(false);
        }

        summaryLabel.setText("Showing " + notifications.size() + " notifications. You have " + unreadCount + " unread alerts.");
    }

    @FXML
    private void backToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}