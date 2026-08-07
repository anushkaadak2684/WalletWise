package model;

import java.time.LocalDate;

public class Reward {

    private int rewardId;
    private String rewardName;
    private int points;
    private String description;
    private LocalDate earnedDate;

    public Reward() {
        this.earnedDate = LocalDate.now();
    }

    public Reward(int rewardId,
                  String rewardName,
                  int points,
                  String description) {

        if(points < 0) {
            throw new IllegalArgumentException(
                "Reward points cannot be negative"
            );
        }

        this.rewardId = rewardId;
        this.rewardName = rewardName;
        this.points = points;
        this.description = description;
        this.earnedDate = LocalDate.now();

    }

    // Getters
    public int getRewardId() {
        return rewardId;
    }

    public String getRewardName() {
        return rewardName;
    }

    public int getPoints() {
        return points;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getEarnedDate() {
        return earnedDate;
    }


    // Setters
    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
    }
    
    public void setEarnedDate(LocalDate earnedDate) {
        this.earnedDate = earnedDate;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public void setPoints(int points) {
        if(points < 0) {
            throw new IllegalArgumentException(
                "Reward points cannot be negative"
            );
        }
        this.points = points;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void displayRewardDetails() {

        System.out.println("\n========== REWARD ==========");


        System.out.println(
                "Reward Name : " + rewardName
        );


        System.out.println(
                "Points      : " + points
        );


        System.out.println(
                "Description : " + description
        );


        System.out.println(
                "Earned Date : " + earnedDate
        );


        System.out.println(
                "============================"
        );

    }

    @Override
    public String toString() {
        return "Reward{" +
                "rewardId=" + rewardId +
                ", rewardName='" +
                rewardName + '\'' +
                ", points=" + points +
                ", earnedDate=" +
                earnedDate +
                '}';

    }

}