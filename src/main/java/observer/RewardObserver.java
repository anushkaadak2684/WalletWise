package observer;

import model.SavingsGoal;
import model.Transaction;
import model.User;
import model.Wallet;
import service.RewardService;

import java.math.BigDecimal;

public class RewardObserver implements WalletEventListener {
    private RewardService rewardService;

    public RewardObserver(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @Override
    public void onTransactionCreated(User user, Transaction transaction) {
        // Transactions do not directly issue milestone points
    }

    @Override
    public void onSavingsGoalAchieved(User user, SavingsGoal goal) {
        if (user == null || rewardService == null) return;
        try {
            rewardService.addReward(
                    user.getUserId(),
                    "Goal Achieved Reward",
                    100,
                    "Achieved savings goal: " + goal.getGoalName()
            );
        } catch (Exception ex) {
            System.err.println("RewardObserver Error: " + ex.getMessage());
        }
    }

    @Override
    public void onSpendingLimitExceeded(User user, Wallet wallet, BigDecimal amount) {
        // No reward points for exceeding limit
    }
}
