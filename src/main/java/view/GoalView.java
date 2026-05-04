package view;

import controller.GoalController;
import model.FinancialGoal;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class GoalView {

    private GoalController goalController;
    private int userId;

    public GoalView(int userId) {
        this.userId = userId;
        this.goalController = new GoalController();
    }

    public Scene getScene(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #1e1e2e;");

        Label title = new Label("🎯 Financial Goals");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#cdd6f4"));

        VBox formBox = createGoalForm(root, stage);
        VBox goalsListBox = createGoalsList();

        root.getChildren().addAll(title, formBox, goalsListBox);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #1e1e2e;");

        return new Scene(scrollPane, 700, 650);
    }

    private VBox createGoalForm(VBox root, Stage stage) {
        VBox formBox = new VBox(12);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: #313244; -fx-background-radius: 12;");

        Label formTitle = new Label("Create New Goal");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        formTitle.setTextFill(Color.web("#cba6f7"));

        TextField nameField = styledTextField("Goal Name (e.g. New Laptop)");
        TextField amountField = styledTextField("Target Amount (e.g. 10000)");

        DatePicker deadlinePicker = new DatePicker(LocalDate.now().plusMonths(3));
        deadlinePicker.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4;");

        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font("Segoe UI", 13));

        Button createBtn = styledButton("Create Goal", "#cba6f7", "#1e1e2e");
        createBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                double target = Double.parseDouble(amountField.getText().trim());
                LocalDate deadline = deadlinePicker.getValue();

                boolean success = goalController.createGoal(userId, name, target, deadline);
                if (success) {
                    statusLabel.setTextFill(Color.web("#a6e3a1"));
                    statusLabel.setText("✓ Goal created!");
                    nameField.clear();
                    amountField.clear();
                    // Refresh goals list
                    root.getChildren().remove(root.getChildren().size() - 1);
                    root.getChildren().add(createGoalsList());
                } else {
                    statusLabel.setTextFill(Color.web("#f38ba8"));
                    statusLabel.setText("✗ Failed. Check inputs.");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setTextFill(Color.web("#f38ba8"));
                statusLabel.setText("✗ Invalid amount.");
            }
        });

        formBox.getChildren().addAll(formTitle, nameField, amountField, deadlinePicker, createBtn, statusLabel);
        return formBox;
    }

    private VBox createGoalsList() {
        VBox listBox = new VBox(12);
        listBox.setPadding(new Insets(20));
        listBox.setStyle("-fx-background-color: #313244; -fx-background-radius: 12;");

        Label listTitle = new Label("Your Goals");
        listTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        listTitle.setTextFill(Color.web("#89b4fa"));

        listBox.getChildren().add(listTitle);

        List<FinancialGoal> goals = goalController.getAllGoals(userId);
        if (goals.isEmpty()) {
            Label noGoals = new Label("No goals yet. Create one above!");
            noGoals.setTextFill(Color.web("#6c7086"));
            listBox.getChildren().add(noGoals);
            return listBox;
        }

        for (FinancialGoal goal : goals) {
            listBox.getChildren().add(buildGoalCard(goal));
        }

        return listBox;
    }

    private VBox buildGoalCard(FinancialGoal goal) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #45475a; -fx-background-radius: 10;");

        String statusColor = getStatusColor(goal.getStatus());

        Label nameLbl = new Label(goal.getName());
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        nameLbl.setTextFill(Color.web("#cdd6f4"));

        Label statusLbl = new Label("● " + goal.getStatus().name().replace("_", " "));
        statusLbl.setFont(Font.font("Segoe UI", 12));
        statusLbl.setTextFill(Color.web(statusColor));

        Label progressText = new Label(String.format("%.2f / %.2f (%.1f%%)",
                goal.getCurrentAmount(), goal.getTargetAmount(), goal.getProgressPercent()));
        progressText.setTextFill(Color.web("#bac2de"));

        ProgressBar bar = new ProgressBar(goal.getProgressPercent() / 100);
        bar.setPrefWidth(600);
        bar.setStyle("-fx-accent: " + statusColor + ";");

        Label deadlineLbl = new Label("Deadline: " + goal.getDeadline());
        deadlineLbl.setTextFill(Color.web("#6c7086"));
        deadlineLbl.setFont(Font.font("Segoe UI", 12));

        HBox actions = new HBox(8);

        if (!goal.isFinalState()) {
            // Add Progress button
            TextField addAmountField = styledTextField("Add amount");
            addAmountField.setPrefWidth(120);
            Button addBtn = styledButton("+ Add", "#a6e3a1", "#1e1e2e");
            addBtn.setPrefWidth(80);
            addBtn.setOnAction(e -> {
                try {
                    double amount = Double.parseDouble(addAmountField.getText().trim());
                    goalController.updateProgress(goal.getGoalId(), amount);
                    addAmountField.clear();
                } catch (NumberFormatException ex) {
                    addAmountField.setStyle("-fx-border-color: #f38ba8;");
                }
            });

            if (goal.getStatus() == FinancialGoal.GoalStatus.PAUSED) {
                Button resumeBtn = styledButton("Resume", "#89b4fa", "#1e1e2e");
                resumeBtn.setOnAction(e -> goalController.resumeGoal(goal.getGoalId()));
                actions.getChildren().addAll(addAmountField, addBtn, resumeBtn);
            } else {
                Button pauseBtn = styledButton("Pause", "#fab387", "#1e1e2e");
                pauseBtn.setOnAction(e -> goalController.pauseGoal(goal.getGoalId()));
                Button cancelBtn = styledButton("Cancel", "#f38ba8", "#1e1e2e");
                cancelBtn.setOnAction(e -> goalController.cancelGoal(goal.getGoalId()));
                actions.getChildren().addAll(addAmountField, addBtn, pauseBtn, cancelBtn);
            }
        }

        card.getChildren().addAll(nameLbl, statusLbl, progressText, bar, deadlineLbl, actions);
        return card;
    }

    private String getStatusColor(FinancialGoal.GoalStatus status) {
        switch (status) {
            case ON_TRACK:        return "#a6e3a1";
            case ACHIEVED:        return "#a6e3a1";
            case BEHIND_SCHEDULE: return "#f38ba8";
            case EXPIRED:         return "#f38ba8";
            case CANCELLED:       return "#6c7086";
            case PAUSED:          return "#fab387";
            default:              return "#89b4fa";
        }
    }

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-prompt-text-fill: #6c7086; -fx-background-radius: 8; -fx-padding: 8;");
        return tf;
    }

    private Button styledButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-background-radius: 8; -fx-padding: 8 16;");
        return btn;
    }
}
