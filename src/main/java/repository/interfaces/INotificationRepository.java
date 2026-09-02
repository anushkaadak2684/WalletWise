package repository.interfaces;

import model.Notification;
import java.util.List;

public interface INotificationRepository {
    void save(Notification notification, int userId);
    Notification findById(int notificationId);
    List<Notification> findByUserId(int userId);
    List<Notification> findUnreadNotifications(int userId);
    List<Notification> findAll();
    void update(Notification notification);
    void updateStatus(int notificationId);
    void delete(int notificationId);
}
