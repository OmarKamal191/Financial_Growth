package model;

import java.time.LocalDate;

public class FinancialGoal {

    public enum GoalStatus {
        CREATED, ACTIVE, ON_TRACK, BEHIND_SCHEDULE, PAUSED, ACHIEVED, EXPIRED, CANCELLED
    }

    private int goalId;
    private int userId;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private LocalDate deadline;
    private GoalStatus status;

    public FinancialGoal() {
        this.status = GoalStatus.CREATED;
        this.currentAmount = 0;
    }

    public FinancialGoal(int userId, String name, double targetAmount, LocalDate deadline) {
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
        this.deadline = deadline;
        this.status = GoalStatus.CREATED;
    }


    public void startGoal() {
        if (status == GoalStatus.CREATED) {
            status = GoalStatus.ACTIVE;
        }
    }

    public void updateProgress(double amount) {
        if (status == GoalStatus.CANCELLED || status == GoalStatus.ACHIEVED || status == GoalStatus.EXPIRED) {
            return;
        }
        currentAmount += amount;

        if (currentAmount >= targetAmount) {
            status = GoalStatus.ACHIEVED;
            return;
        }

        if (LocalDate.now().isAfter(deadline)) {
            status = GoalStatus.EXPIRED;
            return;
        }

        reviewProgress();
    }


    public void reviewProgress() {
        if (status == GoalStatus.CANCELLED || status == GoalStatus.ACHIEVED
                || status == GoalStatus.EXPIRED || status == GoalStatus.PAUSED) {
            return;
        }

        double progressPercent = getProgressPercent();
        double timePercent = getTimeProgressPercent();

        if (progressPercent >= timePercent) {
            status = GoalStatus.ON_TRACK;
        } else {
            status = GoalStatus.BEHIND_SCHEDULE;
        }
    }


    public void pauseGoal() {
        if (status == GoalStatus.ACTIVE || status == GoalStatus.ON_TRACK || status == GoalStatus.BEHIND_SCHEDULE) {
            status = GoalStatus.PAUSED;
        }
    }


    public void resumeGoal() {
        if (status == GoalStatus.PAUSED) {
            status = GoalStatus.ACTIVE;
            reviewProgress();
        }
    }


    public void cancelGoal() {
        if (status != GoalStatus.ACHIEVED && status != GoalStatus.EXPIRED) {
            status = GoalStatus.CANCELLED;
        }
    }


    public double getProgressPercent() {
        if (targetAmount <= 0) return 0;
        return Math.min((currentAmount / targetAmount) * 100, 100);
    }


    public double getTimeProgressPercent() {
        LocalDate today = LocalDate.now();
        if (!today.isBefore(deadline)) return 100;
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(today, deadline);
        if (totalDays <= 0) return 100;
        return Math.max(0, 100 - ((double) totalDays / 365) * 100);
    }

    public double getRemainingAmount() {
        return Math.max(0, targetAmount - currentAmount);
    }

    public boolean isFinalState() {
        return status == GoalStatus.ACHIEVED || status == GoalStatus.EXPIRED || status == GoalStatus.CANCELLED;
    }

    // Getters and Setters

    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public GoalStatus getStatus() { return status; }
    public void setStatus(GoalStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "FinancialGoal{id=" + goalId + ", name='" + name + "', progress=" + String.format("%.1f", getProgressPercent()) + "%, status=" + status + "}";
    }
}
