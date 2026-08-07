package test;

import model.Reward;
import repository.RewardRepository;
import service.RewardService;

public class RewardTest {
    public static void main(String[] args) {
        RewardRepository repository = new RewardRepository();
        RewardService service = new RewardService(repository);

        int userId = 1;

        Reward reward =
                service.addReward(
                        userId,
                        "Savings Champion",
                        500,
                        "Saved money consistently for one month"
                );

        System.out.println(
                "Reward Created Successfully"
        );

        System.out.println(
                "Reward ID: "
                + reward.getRewardId()
        );

        System.out.println(
                "\nReward History:"
        );

        service.showRewards(userId);

        System.out.println(
                "\nTotal Points: "
                + service.getTotalPoints(userId)
        );

    }
}
