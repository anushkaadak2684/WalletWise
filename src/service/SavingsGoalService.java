package service;

import model.SavingsGoal;
import model.User;
import observer.WalletEventListener;
import repository.interfaces.ISavingsGoalRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SavingsGoalService {

    private ISavingsGoalRepository goalRepository;
    private List<WalletEventListener> observers = new ArrayList<>();

    public SavingsGoalService(ISavingsGoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public void addObserver(WalletEventListener listener) {
        if (listener != null) {
            observers.add(listener);
        }
    }

    private void notifyGoalAchieved(User user, SavingsGoal goal) {
        for (WalletEventListener listener : observers) {
            listener.onSavingsGoalAchieved(user, goal);
        }
    }

    public SavingsGoal createSavingsGoal(
            int walletId,
            String goalName,
            BigDecimal targetAmount,
            LocalDate targetDate) {

        SavingsGoal goal = new SavingsGoal(
                0,
                goalName,
                targetAmount,
                targetDate
        );

        try {
            goalRepository.save(goal, walletId);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving goal: " + ex.getMessage(), ex);
        }
        return goal;
    }

    public SavingsGoal getGoalById(int goalId) {
        try {
            return goalRepository.findById(goalId);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching goal: " + ex.getMessage(), ex);
        }
    }

    public List<SavingsGoal> getGoalsByWallet(int walletId) {
        try {
            return goalRepository.findByWalletId(walletId);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching goals: " + ex.getMessage(), ex);
        }
    }

    public void addSavings(SavingsGoal goal,
                           BigDecimal amount) {
        addSavings(null, goal, amount);
    }

    public void addSavings(User user,
                           SavingsGoal goal,
                           BigDecimal amount) {

        if(goal == null) {
            throw new IllegalArgumentException(
                    "Savings goal cannot be null"
            );
        }

        boolean previouslyCompleted = goal.isGoalCompleted();
        goal.addSavings(amount);
        try {
            goalRepository.update(goal);
        } catch (Exception ex) {
            throw new RuntimeException("Error updating goal: " + ex.getMessage(), ex);
        }

        if (!previouslyCompleted && goal.isGoalCompleted()) {
            notifyGoalAchieved(user, goal);
        }
    }

    public boolean isGoalCompleted(SavingsGoal goal) {

        if(goal == null) {
            throw new IllegalArgumentException(
                    "Savings goal cannot be null"
            );
        }

        return goal.isGoalCompleted();
    }

    public BigDecimal getRemainingAmount(SavingsGoal goal) {

        if(goal == null) {
            throw new IllegalArgumentException(
                    "Savings goal cannot be null"
            );
        }

        return goal.getRemainingAmount();
    }

    public double getCompletionPercentage(SavingsGoal goal) {

        if(goal == null) {
            throw new IllegalArgumentException(
                    "Savings goal cannot be null"
            );
        }

        return goal.getCompletionPercentage();
    }

    public void showSavingsGoal(SavingsGoal goal) {

        if(goal == null) {
            throw new IllegalArgumentException(
                    "Savings goal cannot be null"
            );
        }

        goal.displayGoalDetails();
    }
}