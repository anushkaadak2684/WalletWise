package observer;

import model.SavingsGoal;
import model.Transaction;
import model.User;
import model.enums.NotificationType;
import model.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.NotificationService;
import service.RewardService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletObserverTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private RewardService rewardService;

    @Test
    @DisplayName("NotificationObserver generates notification upon transaction creation")
    void testNotificationObserverOnTransaction() {
        NotificationObserver observer = new NotificationObserver(notificationService);
        User user = new User(1, "Alice", "alice@example.com", "hash", "123");
        Transaction tx = new Transaction(1, TransactionType.DEPOSIT, new BigDecimal("500.00"), "Salary");

        observer.onTransactionCreated(user, tx);

        verify(notificationService, times(1)).createNotification(
                eq(1),
                contains("DEPOSIT Completed: +₹500.00"),
                eq(NotificationType.TRANSACTION_ALERT)
        );
    }

    @Test
    @DisplayName("RewardObserver grants reward points upon savings goal achievement")
    void testRewardObserverOnGoalAchieved() {
        RewardObserver observer = new RewardObserver(rewardService);
        User user = new User(2, "Bob", "bob@example.com", "hash", "456");
        SavingsGoal goal = new SavingsGoal(1, "New Laptop", new BigDecimal("50000.00"), LocalDate.now().plusMonths(6));
        goal.setSavedAmount(new BigDecimal("50000.00"));

        observer.onSavingsGoalAchieved(user, goal);

        verify(rewardService, times(1)).addReward(
                eq(2),
                eq("Goal Achieved Reward"),
                eq(100),
                contains("New Laptop")
        );
    }
}
