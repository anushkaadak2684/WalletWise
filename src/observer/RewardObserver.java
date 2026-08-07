package observer;

import model.SavingsGoal;
import model.Transaction;
import model.User;
import service.RewardService;

public class RewardObserver implements WalletEventListener {
    private RewardService rewardService;

    public RewardObserver(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @Override
    public void onTransactionCreated(User user, Transaction transaction) {
        // No points awarded for standard deposits/withdrawals
    }

    @Override
    public void onSavingsGoalAchieved(User user, SavingsGoal goal) {
        if (user == null || rewardService == null) return;
        try {
            rewardService.addReward(
                    user.getUserId(),
                    "Goal Achieved Reward",
                    100,
                    "Earned 100 pts for achieving goal: " + goal.getGoalName()
            );
        } catch (Exception ex) {
            System.err.println("RewardObserver Error: " + ex.getMessage());
        }
    }
}
