package controller;

import database.GoalDAO;
import model.FinancialGoal;
import model.FinancialGoal.GoalStatus;
import model.Notification.NotificationType;

import java.time.LocalDate;
import java.util.List;

public class GoalController {

    private GoalDAO goalDAO;
    private NotificationController notificationController;

    public GoalController() {
        this.goalDAO = new GoalDAO();
        this.notificationController = NotificationController.getInstance();
    }

    public boolean createGoal(int userId, String name, double targetAmount, LocalDate deadline) {
        if (name == null || name.isBlank()) return false;
        if (targetAmount <= 0) return false;
        if (deadline == null || deadline.isBefore(LocalDate.now())) return false;

        FinancialGoal goal = new FinancialGoal(userId, name, targetAmount, deadline);
        goal.startGoal();
        goal.reviewProgress();

        boolean created = goalDAO.createGoal(goal);
        if (created) {
            notificationController.createNotification(userId, NotificationType.INFO,
                    "Goal '" + name + "' created! Target: " + String.format("%.2f", targetAmount));
        }
        return created;
    }


    public String updateProgress(int goalId, double amount) {
        FinancialGoal goal = goalDAO.getGoalById(goalId);
        if (goal == null || goal.isFinalState()) return null;

        goal.updateProgress(amount);
        goalDAO.updateGoal(goal);

        GoalStatus status = goal.getStatus();

        if (status == GoalStatus.ACHIEVED) {
            notificationController.createNotification(goal.getUserId(), NotificationType.GOAL_ACHIEVED,
                    "You achieved your goal '" + goal.getName() + "'!");
        } else if (status == GoalStatus.EXPIRED) {
            notificationController.createNotification(goal.getUserId(), NotificationType.GOAL_EXPIRED,
                    "Your goal '" + goal.getName() + "' expired without being achieved.");
        }

        return status.name();
    }

    public boolean pauseGoal(int goalId) {
        FinancialGoal goal = goalDAO.getGoalById(goalId);
        if (goal == null) return false;
        goal.pauseGoal();
        return goalDAO.updateGoal(goal);
    }

    public boolean resumeGoal(int goalId) {
        FinancialGoal goal = goalDAO.getGoalById(goalId);
        if (goal == null) return false;
        goal.resumeGoal();
        return goalDAO.updateGoal(goal);
    }

    public boolean cancelGoal(int goalId) {
        FinancialGoal goal = goalDAO.getGoalById(goalId);
        if (goal == null) return false;
        goal.cancelGoal();
        return goalDAO.updateGoal(goal);
    }

    public List<FinancialGoal> getAllGoals(int userId) {
        return goalDAO.getGoalsByUser(userId);
    }

    public FinancialGoal getGoalById(int goalId) {
        return goalDAO.getGoalById(goalId);
    }

    public boolean deleteGoal(int goalId) {
        return goalDAO.deleteGoal(goalId);
    }
}
