package service;

import model.SavingsGoal;
import model.Transaction;
import model.User;
import model.Wallet;
import model.enums.TransactionType;
import observer.WalletEventListener;
import repository.interfaces.ISavingsGoalRepository;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SavingsGoalService {

    private ISavingsGoalRepository goalRepository;
    private IWalletRepository walletRepository;
    private ITransactionRepository transactionRepository;
    private List<WalletEventListener> observers = new ArrayList<>();

    public SavingsGoalService(ISavingsGoalRepository goalRepository) {
        this(goalRepository, null, null);
    }

    public SavingsGoalService(ISavingsGoalRepository goalRepository,
                              IWalletRepository walletRepository,
                              ITransactionRepository transactionRepository) {
        this.goalRepository = goalRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
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

        if (goalName == null || goalName.isBlank()) {
            throw new IllegalArgumentException("Goal name cannot be empty");
        }
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target amount must be greater than zero");
        }

        SavingsGoal goal = new SavingsGoal(
                0,
                goalName.trim(),
                targetAmount,
                targetDate
        );

        goalRepository.save(goal, walletId);
        return goal;
    }

    public SavingsGoal getGoalById(int goalId) {
        return goalRepository.findById(goalId);
    }

    public List<SavingsGoal> getGoalsByWallet(int walletId) {
        return goalRepository.findByWalletId(walletId);
    }

    public void deleteGoal(int goalId) {
        goalRepository.delete(goalId);
    }

    public void addSavings(SavingsGoal goal, BigDecimal amount) {
        addSavings(null, goal, amount);
    }

    public void addSavings(User user, SavingsGoal goal, BigDecimal amount) {
        if (goal == null) {
            throw new IllegalArgumentException("Savings goal cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Savings amount must be greater than zero");
        }

        boolean previouslyCompleted = goal.isGoalCompleted();
        goal.addSavings(amount);
        goalRepository.update(goal);

        if (!previouslyCompleted && goal.isGoalCompleted()) {
            notifyGoalAchieved(user, goal);
        }
    }

    /**
     * Atomically contributes money from the user's wallet to a savings goal.
     * Deducts wallet balance, records a withdrawal transaction, and credits the goal balance in one atomic transaction.
     */
    public void contributeToSavingsGoal(User user, Wallet wallet, SavingsGoal goal, BigDecimal amount) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        if (goal == null) {
            throw new IllegalArgumentException("Savings goal cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Contribution amount must be greater than zero");
        }

        boolean previouslyCompleted = goal.isGoalCompleted();
        boolean goalCompletedNow = false;

        Transaction transaction = new Transaction(
                0,
                TransactionType.WITHDRAWAL,
                amount,
                "Contribution to Savings Goal: " + goal.getGoalName()
        );

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Deduct from wallet
                wallet.withdraw(amount);
                if (walletRepository != null) {
                    walletRepository.update(connection, wallet);
                }

                // Record transaction
                if (transactionRepository != null) {
                    transactionRepository.save(connection, transaction, wallet.getWalletId());
                }

                // Update goal saved amount
                goal.addSavings(amount);
                goalRepository.update(connection, goal);

                connection.commit();
                goalCompletedNow = goal.isGoalCompleted();
            } catch (Exception ex) {
                connection.rollback();
                throw new RuntimeException("Goal contribution transaction failed, rolled back: " + ex.getMessage(), ex);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error contributing to goal: " + e.getMessage(), e);
        }

        // Fire observer event upon milestone completion
        if (!previouslyCompleted && goalCompletedNow) {
            notifyGoalAchieved(user, goal);
        }
    }

    public boolean isGoalCompleted(SavingsGoal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Savings goal cannot be null");
        }
        return goal.isGoalCompleted();
    }

    public BigDecimal getRemainingAmount(SavingsGoal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Savings goal cannot be null");
        }
        return goal.getRemainingAmount();
    }

    public double getCompletionPercentage(SavingsGoal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Savings goal cannot be null");
        }
        return goal.getCompletionPercentage();
    }

    public void showSavingsGoal(SavingsGoal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Savings goal cannot be null");
        }
        goal.displayGoalDetails();
    }
}