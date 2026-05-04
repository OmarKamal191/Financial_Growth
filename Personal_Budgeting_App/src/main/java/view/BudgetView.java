package view;

import controller.BudgetController;
import model.Budget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;


public class BudgetView {

    private BudgetController budgetController;
    private int userId;

    public BudgetView(int userId) {
        this.userId = userId;
        this.budgetController = new BudgetController();
    }

    /**
     * Builds and returns the Budget management scene.
     * @param stage the primary stage
     * @return Scene for budget management
     */
    public Scene getScene(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #1e1e2e;");

        // Title
        Label title = new Label("💰 Budget Manager");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#cdd6f4"));

        // --- Create Budget Form ---
        VBox formBox = createFormBox();

        // --- Current Budget Info ---
        VBox infoBox = createBudgetInfoBox();

        root.getChildren().addAll(title, formBox, infoBox);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #1e1e2e;");

        return new Scene(scrollPane, 700, 600);
    }

    private VBox createFormBox() {
        VBox formBox = new VBox(12);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: #313244; -fx-background-radius: 12;");

        Label formTitle = new Label("Create New Budget");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        formTitle.setTextFill(Color.web("#a6e3a1"));

        TextField amountField = styledTextField("Budget Amount (e.g. 5000)");

        DatePicker startPicker = new DatePicker(LocalDate.now());
        startPicker.setPromptText("Start Date");
        startPicker.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4;");

        DatePicker endPicker = new DatePicker(LocalDate.now().plusMonths(1));
        endPicker.setPromptText("End Date");
        endPicker.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4;");

        TextField thresholdField = styledTextField("Alert Threshold % (e.g. 80)");

        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font("Segoe UI", 13));

        Button createBtn = styledButton("Create Budget", "#a6e3a1", "#1e1e2e");
        createBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                LocalDate start = startPicker.getValue();
                LocalDate end = endPicker.getValue();
                int threshold = Integer.parseInt(thresholdField.getText().trim());

                boolean success = budgetController.createBudget(userId, amount, start, end, threshold);
                if (success) {
                    statusLabel.setTextFill(Color.web("#a6e3a1"));
                    statusLabel.setText("✓ Budget created successfully!");
                    amountField.clear();
                    thresholdField.clear();
                } else {
                    statusLabel.setTextFill(Color.web("#f38ba8"));
                    statusLabel.setText("✗ Failed to create budget. Check your inputs.");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setTextFill(Color.web("#f38ba8"));
                statusLabel.setText("✗ Invalid amount or threshold.");
            }
        });

        formBox.getChildren().addAll(formTitle, amountField, startPicker, endPicker, thresholdField, createBtn, statusLabel);
        return formBox;
    }

    private VBox createBudgetInfoBox() {
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle("-fx-background-color: #313244; -fx-background-radius: 12;");

        Label infoTitle = new Label("Active Budget");
        infoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        infoTitle.setTextFill(Color.web("#89b4fa"));

        Budget activeBudget = budgetController.getActiveBudget(userId);

        if (activeBudget == null) {
            Label noData = new Label("No active budget found.");
            noData.setTextFill(Color.web("#6c7086"));
            infoBox.getChildren().addAll(infoTitle, noData);
        } else {
            double remaining = activeBudget.calculateRemaining();
            double percent = activeBudget.getAmount() > 0
                    ? (activeBudget.getSpentAmount() / activeBudget.getAmount()) * 100
                    : 0;

            Label amountLbl = label("Total Budget: " + String.format("%.2f", activeBudget.getAmount()), "#cdd6f4");
            Label spentLbl = label("Spent: " + String.format("%.2f", activeBudget.getSpentAmount()), "#fab387");
            Label remainLbl = label("Remaining: " + String.format("%.2f", remaining),
                    remaining >= 0 ? "#a6e3a1" : "#f38ba8");
            Label periodLbl = label("Period: " + activeBudget.getStartDate() + " → " + activeBudget.getEndDate(), "#bac2de");

            ProgressBar progressBar = new ProgressBar(Math.min(percent / 100, 1.0));
            progressBar.setPrefWidth(600);
            progressBar.setStyle(percent >= 100 ? "-fx-accent: #f38ba8;" :
                    percent >= activeBudget.getAlertThreshold() ? "-fx-accent: #fab387;" : "-fx-accent: #a6e3a1;");

            Label percentLbl = label(String.format("%.1f%% used", percent),
                    percent >= 100 ? "#f38ba8" : "#cdd6f4");

            infoBox.getChildren().addAll(infoTitle, amountLbl, spentLbl, remainLbl, periodLbl, progressBar, percentLbl);
        }

        return infoBox;
    }

    // Helper Methods

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-prompt-text-fill: #6c7086; -fx-background-radius: 8; -fx-padding: 10;");
        return tf;
    }

    private Button styledButton(String text, String bg, String fg) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btn.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-background-radius: 8; -fx-padding: 10 20;");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private Label label(String text, String color) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", 14));
        lbl.setTextFill(Color.web(color));
        return lbl;
    }
}