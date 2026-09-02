package service;

import model.Reward;
import repository.interfaces.IRewardRepository;

import java.util.List;

public class RewardService {
    private IRewardRepository rewardRepository;

    public RewardService(IRewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    // Create Reward
    public Reward addReward(int userId,
                            String rewardName,
                            int points,
                            String description) {

        if (rewardName == null || rewardName.isBlank()) {
            throw new IllegalArgumentException("Reward name cannot be empty");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Reward description cannot be empty");
        }

        if (points < 0) {
            throw new IllegalArgumentException("Reward points cannot be negative");
        }

        Reward reward = new Reward(
                0,
                rewardName.trim(),
                points,
                description.trim()
        );

        rewardRepository.save(reward, userId);
        return reward;
    }

    // Get Rewards Of User
    public List<Reward> getUserRewards(int userId) {
        return rewardRepository.findByUserId(userId);
    }

    // Delete Reward
    public void deleteReward(int rewardId) {
        rewardRepository.delete(rewardId);
    }

    // Calculate Total Points
    public int getTotalPoints(int userId) {
        int totalPoints = 0;
        List<Reward> rewards = rewardRepository.findByUserId(userId);
        for (Reward reward : rewards) {
            totalPoints += reward.getPoints();
        }
        return totalPoints;
    }

    // Display Rewards
    public void showRewards(int userId) {
        List<Reward> rewards = rewardRepository.findByUserId(userId);
        if (rewards.isEmpty()) {
            System.out.println("No rewards available");
            return;
        }
        for (Reward reward : rewards) {
            reward.displayRewardDetails();
        }
    }
}