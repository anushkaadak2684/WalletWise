package repository;

import repository.interfaces.IRewardRepository;
import model.Reward;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RewardRepository implements IRewardRepository {

    // Save Reward
    public void save(Reward reward, int userId) {

        String sql = "INSERT INTO rewards " +
                "(user_id, reward_name, points, description, earned_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userId);
            statement.setString(2, reward.getRewardName());
            statement.setInt(3, reward.getPoints());
            statement.setString(4, reward.getDescription());
            statement.setDate(5, Date.valueOf(reward.getEarnedDate()));

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()) {
                reward.setRewardId(rs.getInt(1));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error saving reward", e);
        }
    }


    // Find Reward By ID
    public Reward findById(int rewardId) {

        String sql = "SELECT * FROM rewards WHERE reward_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, rewardId);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return mapReward(rs);
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error finding reward", e);
        }
        return null;
    }


    // Find Rewards By User
    public List<Reward> findByUserId(int userId) {

        List<Reward> rewards = new ArrayList<>();

        String sql = "SELECT * FROM rewards WHERE user_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                rewards.add(mapReward(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error fetching rewards", e);
        }

        return rewards;
    }


    // Get All Rewards
    public List<Reward> findAll() {

        List<Reward> rewards = new ArrayList<>();

        String sql = "SELECT * FROM rewards";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()) {

            while(rs.next()) {
                rewards.add(mapReward(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error fetching rewards", e);
        }

        return rewards;
    }


    // Update Reward
    public void update(Reward reward) {

        String sql =
                "UPDATE rewards SET reward_name=?, points=?, " +
                "description=?, earned_date=? WHERE reward_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1,
                    reward.getRewardName());

            statement.setInt(2,
                    reward.getPoints());

            statement.setString(3,
                    reward.getDescription());

            statement.setDate(4,
                    Date.valueOf(reward.getEarnedDate()));

            statement.setInt(5,
                    reward.getRewardId());

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException("Error updating reward", e);
        }
    }


    // Delete Reward
    public void delete(int rewardId) {

        String sql = "DELETE FROM rewards WHERE reward_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, rewardId);

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException("Error deleting reward", e);
        }
    }


    // ResultSet -> Reward Object
    private Reward mapReward(ResultSet rs)
            throws SQLException {

        Reward reward = new Reward(
                rs.getInt("reward_id"),
                rs.getString("reward_name"),
                rs.getInt("points"),
                rs.getString("description")
        );

        reward.setEarnedDate(rs.getDate("earned_date").toLocalDate());
        return reward;
    }
}
