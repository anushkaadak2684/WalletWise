package gui;

import model.Notification;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NotificationPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;
    private DefaultTableModel tableModel;

    public NotificationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 18));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initUI();
    }

    private void initUI() {
        // Info Banner Card
        JPanel infoCard = UIHelper.createCardPanel("System Notifications & Smart Alerts Center");
        JLabel infoLabel = new JLabel("<html>Notifications and proactive spending alerts are automatically generated when financial transactions occur, budget thresholds (80%/100%) are reached, or savings milestones are achieved.</html>");
        infoLabel.setFont(Theme.BODY_FONT);
        infoLabel.setForeground(Theme.TEXT_MUTED);
        infoLabel.setBorder(new EmptyBorder(4, 0, 4, 0));
        infoCard.add(infoLabel, BorderLayout.CENTER);

        add(infoCard, BorderLayout.NORTH);

        // Notifications Table Card
        JPanel tableCard = UIHelper.createCardPanel("Activity & Alert Inbox");
        tableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Alert Message", "Status", "Timestamp"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBtnPanel.setOpaque(false);

        JButton readBtn = UIHelper.createBlueButton("Mark as Read");
        readBtn.addActionListener(e -> handleMarkRead(table));

        JButton deleteNotifBtn = new JButton("Delete Selected");
        deleteNotifBtn.setFont(Theme.BODY_BOLD);
        deleteNotifBtn.setBackground(new Color(38, 44, 54));
        deleteNotifBtn.setForeground(Theme.TEXT_PRIMARY);
        deleteNotifBtn.setFocusPainted(false);
        deleteNotifBtn.addActionListener(e -> handleDeleteNotification(table));

        bottomBtnPanel.add(readBtn);
        bottomBtnPanel.add(deleteNotifBtn);

        tableCard.add(bottomBtnPanel, BorderLayout.SOUTH);
        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshData() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        List<Notification> notifications = mainFrame.getNotificationService().getUserNotifications(user.getUserId());
        tableModel.setRowCount(0);
        for (Notification n : notifications) {
            tableModel.addRow(new Object[]{
                    n.getNotificationId(),
                    n.getNotificationType(),
                    n.getMessage(),
                    n.isReadStatus() ? "READ" : "UNREAD",
                    n.getCreatedAt().toString()
            });
        }
    }

    private void handleMarkRead(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select a notification from the table.");
            return;
        }
        int notifId = (int) table.getValueAt(selectedRow, 0);
        try {
            Notification n = mainFrame.getNotificationService().getNotificationById(notifId);
            if (n != null) {
                mainFrame.getNotificationService().markAsRead(n);
                mainFrame.refreshAllPanels();
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Error marking notification read: " + ex.getMessage());
        }
    }

    private void handleDeleteNotification(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select a notification from the table.");
            return;
        }
        int notifId = (int) table.getValueAt(selectedRow, 0);
        try {
            mainFrame.getNotificationService().deleteNotification(notifId);
            mainFrame.refreshAllPanels();
        } catch (Exception ex) {
            UIHelper.showError(this, "Error deleting notification: " + ex.getMessage());
        }
    }
}
