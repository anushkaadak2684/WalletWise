package repository;

import repository.interfaces.INotificationRepository;
import model.Notification;
import model.enums.NotificationType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository implements INotificationRepository {

    // Save Notification
    public void save(Notification notification, int userId) {

        String sql = "INSERT INTO notifications " +
                "(user_id, message, notification_type, read_status, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userId);
            statement.setString(2, notification.getMessage());
            statement.setString(3, notification.getNotificationType().name());
            statement.setBoolean(4, notification.isReadStatus());
            statement.setTimestamp(5, Timestamp.valueOf(notification.getCreatedAt()));

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()) {
                notification.setNotificationId(rs.getInt(1));
            }

        } catch(SQLException e) {
            throw new RuntimeException(
                    "Error saving notification", e
            );
        }
    }


    // Find Notification By ID
    public Notification findById(int notificationId) {

        String sql = "SELECT * FROM notifications WHERE notification_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, notificationId);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return mapNotification(rs);
            }

        } catch(SQLException e) {
            throw new RuntimeException(
                    "Error finding notification", e
            );
        }
        return null;
    }


    // Find All Notifications Of User
    public List<Notification> findByUserId(int userId) {

        List<Notification> notifications = new ArrayList<>();

        String sql = "SELECT * FROM notifications WHERE user_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                notifications.add(mapNotification(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException(
                    "Error fetching notifications", e
            );
        }
        return notifications;
    }


    // Find Unread Notifications
    public List<Notification> findUnreadNotifications(int userId) {

        List<Notification> notifications = new ArrayList<>();

        String sql =
                "SELECT * FROM notifications " +
                "WHERE user_id=? AND read_status=false";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                notifications.add(mapNotification(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException(
                    "Error fetching unread notifications", e
            );
        }
        return notifications;
    }


    // Mark Notification As Read
    public void updateStatus(int notificationId) {

        String sql =
                "UPDATE notifications " +
                "SET read_status=true " +
                "WHERE notification_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, notificationId);

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException(
                    "Error updating notification status", e
            );
        }
    }

    @Override
    public void update(Notification notification) {
        if (notification != null) {
            updateStatus(notification.getNotificationId());
        }
    }


    @Override
    public List<Notification> findAll() {
        return new ArrayList<>();
    }

    // Delete Notification
    public void delete(int notificationId) {

        String sql = "DELETE FROM notifications WHERE notification_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, notificationId);

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException(
                    "Error deleting notification", e
            );
        }
    }


    // ResultSet -> Notification Object
    private Notification mapNotification(ResultSet rs)
            throws SQLException {

        Notification notification =
                new Notification(
                        rs.getInt("notification_id"),
                        rs.getString("message"),
                        NotificationType.valueOf(rs.getString("notification_type"))
                );

        notification.setReadStatus(rs.getBoolean("read_status"));

        notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return notification;
    }
}
