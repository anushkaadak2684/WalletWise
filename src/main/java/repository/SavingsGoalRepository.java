package repository;

import repository.interfaces.ISavingsGoalRepository;
import model.SavingsGoal;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavingsGoalRepository implements ISavingsGoalRepository {

    @Override
    public void save(SavingsGoal goal, int walletId) {
        String sql = "INSERT INTO savings_goals (wallet_id, goal_name, target_amount, saved_amount, target_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, walletId);
            statement.setString(2, goal.getGoalName());
            statement.setBigDecimal(3, goal.getTargetAmount());
            statement.setBigDecimal(4, goal.getSavedAmount());
            statement.setDate(5, Date.valueOf(goal.getTargetDate()));

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    goal.setGoalId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving savings goal: " + e.getMessage(), e);
        }
    }

    @Override
    public SavingsGoal findById(int goalId) {
        String sql = "SELECT * FROM savings_goals WHERE goal_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, goalId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapGoal(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding savings goal: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<SavingsGoal> findByWalletId(int walletId) {
        List<SavingsGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM savings_goals WHERE wallet_id=? ORDER BY goal_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, walletId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapGoal(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching savings goals: " + e.getMessage(), e);
        }
        return goals;
    }

    @Override
    public List<SavingsGoal> findAll() {
        List<SavingsGoal> goals = new ArrayList<>();
        String sql = "SELECT * FROM savings_goals ORDER BY goal_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                goals.add(mapGoal(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching savings goals: " + e.getMessage(), e);
        }
        return goals;
    }

    @Override
    public void update(SavingsGoal goal) {
        try (Connection connection = DBConnection.getConnection()) {
            update(connection, goal);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating savings goal: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Connection connection, SavingsGoal goal) throws SQLException {
        String sql = "UPDATE savings_goals SET goal_name=?, target_amount=?, saved_amount=?, target_date=? WHERE goal_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, goal.getGoalName());
            statement.setBigDecimal(2, goal.getTargetAmount());
            statement.setBigDecimal(3, goal.getSavedAmount());
            statement.setDate(4, Date.valueOf(goal.getTargetDate()));
            statement.setInt(5, goal.getGoalId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int goalId) {
        String sql = "DELETE FROM savings_goals WHERE goal_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, goalId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting savings goal: " + e.getMessage(), e);
        }
    }

    private SavingsGoal mapGoal(ResultSet rs) throws SQLException {
        SavingsGoal goal = new SavingsGoal(
                rs.getInt("goal_id"),
                rs.getString("goal_name"),
                rs.getBigDecimal("target_amount"),
                rs.getDate("target_date").toLocalDate()
        );
        goal.setSavedAmount(rs.getBigDecimal("saved_amount"));
        return goal;
    }
}
