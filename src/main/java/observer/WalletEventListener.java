package observer;

import model.SavingsGoal;
import model.Transaction;
import model.User;

public interface WalletEventListener {
    void onTransactionCreated(User user, Transaction transaction);
    void onSavingsGoalAchieved(User user, SavingsGoal goal);
}
