package service;

import model.Notification;
import model.enums.NotificationType;
import repository.interfaces.INotificationRepository;

import java.util.List;

public class NotificationService {

    private INotificationRepository notificationRepository;

    public NotificationService(INotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Create Notification
    public Notification createNotification(int userId, String message, NotificationType type) {
        if(message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Notification message cannot be empty"
            );
        }

        if(type == null) {
            throw new IllegalArgumentException(
                    "Notification type cannot be null"
            );
        }

        Notification notification =
                new Notification(
                        0,
                        message,
                        type
                );

        notificationRepository.save(notification, userId);
        return notification;
    }


    // Get All Notifications Of User
    public List<Notification> getUserNotifications(int userId) {
        return notificationRepository.findByUserId(userId);
    }


    // Get Unread Notifications
    public List<Notification> getUnreadNotifications(int userId) {
        return notificationRepository.findUnreadNotifications(userId);
    }


    // Mark Notification As Read
    public void markAsRead(Notification notification) {
        if(notification == null) {
            throw new IllegalArgumentException(
                    "Notification cannot be null"
            );
        }

        notificationRepository.updateStatus(notification.getNotificationId());
        notification.markAsRead();
    }


    // Display Notifications
    public void showNotifications(int userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        if(notifications.isEmpty()) {
            System.out.println("No notifications available");
            return;
        }

        for(Notification notification : notifications) {
            notification.displayNotification();
        }
    }
}