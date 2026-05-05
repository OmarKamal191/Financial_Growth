package view;

import controller.AuthController;
import controller.GoalController;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.FinancialGoal;

import java.io.IOException;
import java.time.LocalDate;

public class GoalView {

    @FXML private TextField goalNameField;
    @FXML private TextField targetAmountField;
    @FXML private DatePicker deadlinePicker;
    @FXML private Label statusLabel;
    @FXML private TextField addAmountField;
    @FXML private Button pauseResumeBtn;

    @FXML private TableView<FinancialGoal> goalsTable;
    @FXML private TableColumn<FinancialGoal, String> colGoalName;
    @FXML private TableColumn<FinancialGoal, Double> colTarget;
    @FXML private TableColumn<FinancialGoal, Double> colSaved;
    @FXML private TableColumn<FinancialGoal, FinancialGoal.GoalStatus> colGoalStatus;
    @FXML private TableColumn<FinancialGoal, LocalDate> colDeadline;

    @FXML private ProgressBar goalProgressBar;
    @FXML private Label progressLabel;

    private GoalController goalController = new GoalController();
    private int currentUserId = 1;

    @FXML
    public void initialize() {
        if (AuthController.getCurrentUser() != null) {
            this.currentUserId = AuthController.getCurrentUser().getUserId();
        }
        setupTable();
        refreshUI();
        deadlinePicker.setValue(LocalDate.now().plusMonths(3));

        goalsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateSelectionDetails(newSelection);
            }
        });
    }

    private void setupTable() {
        colGoalName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTarget.setCellValueFactory(new PropertyValueFactory<>("targetAmount"));
        colSaved.setCellValueFactory(new PropertyValueFactory<>("currentAmount"));
        colGoalStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
    }

    @FXML
    private void handleCreateGoal(ActionEvent event) {
        try {
            String name = goalNameField.getText();
            double target = Double.parseDouble(targetAmountField.getText());
            LocalDate deadline = deadlinePicker.getValue();

            if (goalController.createGoal(currentUserId, name, target, deadline)) {
                showStatus("✓ Goal Created!", Color.GREEN);
                clearForm();
                refreshUI();
            } else {
                showStatus("✗ Creation Failed!", Color.RED);
            }
        } catch (Exception e) {
            showStatus("✗ Invalid Input!", Color.RED);
        }
    }

    @FXML
    private void handleUpdateProgress(ActionEvent event) {
        FinancialGoal selected = goalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("! Select a goal first", Color.ORANGE);
            return;
        }

        try {
            double amount = Double.parseDouble(addAmountField.getText());
            goalController.updateProgress(selected.getGoalId(), amount);
            addAmountField.clear();
            refreshUI();
            goalsTable.getSelectionModel().select(goalController.getGoalById(selected.getGoalId()));
        } catch (Exception e) {
            showStatus("✗ Invalid Amount!", Color.RED);
        }
    }

    @FXML
    private void handlePauseResume(ActionEvent event) {
        FinancialGoal selected = goalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (selected.getStatus() == FinancialGoal.GoalStatus.PAUSED) {
            goalController.resumeGoal(selected.getGoalId());
        } else {
            goalController.pauseGoal(selected.getGoalId());
        }
        refreshUI();
        goalsTable.getSelectionModel().select(goalController.getGoalById(selected.getGoalId()));
    }

    @FXML
    private void handleCancelGoal(ActionEvent event) {
        FinancialGoal selected = goalsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            goalController.cancelGoal(selected.getGoalId());
            refreshUI();
        }
    }

    @FXML
    private void handleDeleteGoal(ActionEvent event) {
        FinancialGoal selected = goalsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            goalController.deleteGoal(selected.getGoalId());
            refreshUI();
            resetDetails();
        }
    }

    private void refreshUI() {
        ObservableList<FinancialGoal> goals = FXCollections.observableArrayList(goalController.getAllGoals(currentUserId));
        goalsTable.setItems(goals);
    }

    private void updateSelectionDetails(FinancialGoal goal) {
        double progress = goal.getProgressPercent() / 100.0;
        goalProgressBar.setProgress(progress);
        progressLabel.setText(String.format("%.2f / %.2f (%.1f%%) - %s",
                goal.getCurrentAmount(), goal.getTargetAmount(), goal.getProgressPercent(), goal.getStatus()));

        if (goal.getStatus() == FinancialGoal.GoalStatus.PAUSED) {
            pauseResumeBtn.setText("RESUME");
            pauseResumeBtn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: white;");
        } else {
            pauseResumeBtn.setText("PAUSE");
            pauseResumeBtn.setStyle("-fx-background-color: #fab387; -fx-text-fill: white;");
        }
    }

    private void resetDetails() {
        goalProgressBar.setProgress(0);
        progressLabel.setText("Select a goal to see progress");
    }

    private void clearForm() {
        goalNameField.clear();
        targetAmountField.clear();
        deadlinePicker.setValue(LocalDate.now().plusMonths(3));
    }

    private void showStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(color);
    }

    @FXML
    private void backToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}