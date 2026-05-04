package database;

import model.FinancialGoal;
import model.FinancialGoal.GoalStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

    private Connection connection;

    public GoalDAO() {
        try {
            this.connection = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("GoalDAO Connection Error: " + e.getMessage());
        }
    }

    public boolean createGoal(FinancialGoal goal) {
        String sql = "INSERT INTO Goals (UserId, Name, TargetAmount, CurrentAmount, Deadline, Status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getName());
            stmt.setDouble(3, goal.getTargetAmount());
            stmt.setDouble(4, goal.getCurrentAmount());
            stmt.setDate(5, Date.valueOf(goal.getDeadline()));
            stmt.setString(6, goal.getStatus().name());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) goal.setGoalId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("createGoal failed: " + e.getMessage());
        }
        return false;
    }

    public boolean updateGoal(FinancialGoal goal) {
        String sql = "UPDATE Goals SET CurrentAmount = ?, Status = ? WHERE GoalId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, goal.getCurrentAmount());
            stmt.setString(2, goal.getStatus().name());
            stmt.setInt(3, goal.getGoalId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateGoal failed: " + e.getMessage());
        }
        return false;
    }

    public List<FinancialGoal> getGoalsByUser(int userId) {
        List<FinancialGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM Goals WHERE UserId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                goals.add(mapRowToGoal(rs));
            }
        } catch (SQLException e) {
            System.err.println("getGoalsByUser failed: " + e.getMessage());
        }
        return goals;
    }

    public FinancialGoal getGoalById(int goalId) {
        String sql = "SELECT * FROM Goals WHERE GoalId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, goalId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRowToGoal(rs);
        } catch (SQLException e) {
            System.err.println("getGoalById failed: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteGoal(int goalId) {
        String sql = "DELETE FROM Goals WHERE GoalId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, goalId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteGoal failed: " + e.getMessage());
        }
        return false;
    }

    private FinancialGoal mapRowToGoal(ResultSet rs) throws SQLException {
        FinancialGoal g = new FinancialGoal();
        g.setGoalId(rs.getInt("GoalId"));
        g.setUserId(rs.getInt("UserId"));
        g.setName(rs.getString("Name"));
        g.setTargetAmount(rs.getDouble("TargetAmount"));
        g.setCurrentAmount(rs.getDouble("CurrentAmount"));
        g.setDeadline(rs.getDate("Deadline").toLocalDate());
        g.setStatus(GoalStatus.valueOf(rs.getString("Status")));
        return g;
    }
}
