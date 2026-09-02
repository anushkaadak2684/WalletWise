package observer;

import model.enums.NotificationType;
import model.SavingsGoal;
import model.Transaction;
import model.User;
import service.NotificationService;

public class NotificationObserver implements WalletEventListener {
    private NotificationService notificationService;

    public NotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onTransactionCreated(User user, Transaction transaction) {
        if (user == null || notificationService == null) return;
        String typeStr = transaction.getTransactionType().name();
        String sign = transaction.getTransactionType().name().equalsIgnoreCase("DEPOSIT") ? "+" : "-";
        String msg = typeStr + " Completed: " + sign + "₹" + transaction.getAmount() + " (" + transaction.getDescription() + ")";
        try {
            notificationService.createNotification(user.getUserId(), msg, NotificationType.TRANSACTION_ALERT);
        } catch (Exception ex) {
            System.err.println("NotificationObserver Error: " + ex.getMessage());
        }
    }

    @Override
    public void onSavingsGoalAchieved(User user, SavingsGoal goal) {
        if (user == null || notificationService == null) return;
        String msg = "Savings Goal Achieved: " + goal.getGoalName() + " (Target: ₹" + goal.getTargetAmount() + ")";
        try {
            notificationService.createNotification(user.getUserId(), msg, NotificationType.SAVINGS_ALERT);
        } catch (Exception ex) {
            System.err.println("NotificationObserver Error: " + ex.getMessage());
        }
    }
}
