package observer;

import model.SavingsGoal;
import model.Transaction;
import model.User;
import model.Wallet;

import java.math.BigDecimal;

public interface WalletEventListener {
    void onTransactionCreated(User user, Transaction transaction);
    void onSavingsGoalAchieved(User user, SavingsGoal goal);
    void onSpendingLimitExceeded(User user, Wallet wallet, BigDecimal amount);
}
