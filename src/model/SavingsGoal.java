package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsGoal {

    private int goalId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal savedAmount;
    private LocalDate targetDate;

    public SavingsGoal() {
        this.savedAmount = BigDecimal.ZERO;
    }

    public SavingsGoal(int goalId,
                       String goalName,
                       BigDecimal targetAmount,
                       LocalDate targetDate) {

        this.goalId = goalId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.savedAmount = BigDecimal.ZERO;
        this.targetDate = targetDate;
    }

    // Getters
    public int getGoalId() {
        return goalId;
    }

    public String getGoalName() {
        return goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public BigDecimal getSavedAmount() {
        return savedAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    // Setters
    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }  

    public void setSavedAmount(BigDecimal savedAmount) {
        this.savedAmount = savedAmount;
    }
    
    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }


    public void addSavings(BigDecimal amount) {

        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Savings amount must be greater than zero"
            );
        }

        savedAmount = savedAmount.add(amount);

    }

    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(savedAmount);

    }

    public double getCompletionPercentage() {

        if(targetAmount.compareTo(BigDecimal.ZERO) == 0){
            return 0;
        }

        BigDecimal percentage =
                savedAmount
                .multiply(new BigDecimal("100"))
                .divide(
                        targetAmount,
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        return percentage.doubleValue();

    }

    public boolean isGoalCompleted() {
        return savedAmount.compareTo(targetAmount) >= 0;

    }


    public void displayGoalDetails() {


        System.out.println("\n========== SAVINGS GOAL ==========");


        System.out.println(
                "Goal Name : " + goalName
        );


        System.out.println(
                "Target    : ₹" + targetAmount
        );


        System.out.println(
                "Saved     : ₹" + savedAmount
        );


        System.out.println(
                "Remaining : ₹" + getRemainingAmount()
        );


        System.out.println(
                "Progress  : " + getCompletionPercentage() + "%"
        );


        System.out.println(
                "Completed : " + isGoalCompleted()
        );


        System.out.println(
                "=================================="
        );
    }



    @Override
    public String toString(){

        return "SavingsGoal{" +
                "goalId=" + goalId +
                ", goalName='" + goalName + '\'' +
                ", targetAmount=" + targetAmount +
                ", savedAmount=" + savedAmount +
                '}';
    }

}
