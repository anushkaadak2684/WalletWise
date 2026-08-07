package model;

import model.enums.NotificationType;

import java.time.LocalDateTime;

public class Notification {
    private int notificationId;
    private String message;
    private NotificationType notificationType;
    private boolean readStatus;
    private LocalDateTime createdAt;

    public Notification() {
        this.readStatus = false;

    }

    public Notification(int notificationId,
                        String message,
                        NotificationType notificationType) {


        this.notificationId = notificationId;
        this.message = message;
        this.notificationType = notificationType;
        this.readStatus = false;
        this.createdAt = LocalDateTime.now();

    }

    // Getters
    public int getNotificationId() {
        return notificationId;
    }


    public String getMessage() {
        return message;
    }


    public NotificationType getNotificationType() {
        return notificationType;
    }


    public boolean isReadStatus() {
        return readStatus;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    // Setters
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void markAsRead() {
        this.readStatus = true;
    }



    public void displayNotification() {

        System.out.println("\n========== NOTIFICATION ==========");


        System.out.println(
                "Type    : " + notificationType
        );


        System.out.println(
                "Message : " + message
        );


        System.out.println(
                "Status  : " +
                (readStatus ? "Read" : "Unread")
        );


        System.out.println(
                "Created : " + createdAt
        );


        System.out.println(
                "=================================="
        );

    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" +
                notificationId +
                ", message='" +
                message + '\'' +
                ", notificationType='" +
                notificationType + '\'' +
                ", readStatus=" +
                readStatus +
                '}';

    }

}
