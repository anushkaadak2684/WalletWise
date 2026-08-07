package test;

import model.Notification;
import model.enums.NotificationType;
import repository.NotificationRepository;
import service.NotificationService;

public class NotificationTest {

    public static void main(String[] args) {
        NotificationRepository repository = new NotificationRepository();
        NotificationService service = new NotificationService(repository);

        int userId = 1;

        // Create notification
        Notification notification =
                service.createNotification(
                        userId,
                        "Your budget limit is near",
                        NotificationType.BUDGET_ALERT
                );

        System.out.println(
                "Notification Created Successfully"
        );

        System.out.println(
                "Notification ID: "
                + notification.getNotificationId()
        );

        // Fetch notifications
        System.out.println(
                "\nFetching Notifications:"
        );

        service.showNotifications(userId);

        // Mark as read
        service.markAsRead(notification);

        System.out.println(
                "\nAfter Marking Read:"
        );

        service.showNotifications(userId);

    }
}
