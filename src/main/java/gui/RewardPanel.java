package gui;

import model.Reward;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RewardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;

    private JLabel pointsLabel;
    private DefaultTableModel tableModel;

    public RewardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 18));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initUI();
    }

    private void initUI() {
        // Top Banner Card
        JPanel topCard = UIHelper.createCardPanel("Gamified Financial Milestones & Rewards");
        JPanel topContent = new JPanel(new BorderLayout(10, 10));
        topContent.setOpaque(false);

        pointsLabel = new JLabel("Total Balance: 0 Pts");
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pointsLabel.setForeground(Theme.WARNING_COLOR);

        JLabel infoLabel = new JLabel("<html>Earn <font color='#F59E0B'><b>+100 Reward Points</b></font> automatically upon reaching savings goals, maintaining positive balances, and staying within configured category budgets.</html>");
        infoLabel.setFont(Theme.BODY_FONT);
        infoLabel.setForeground(Theme.TEXT_MUTED);

        topContent.add(pointsLabel, BorderLayout.WEST);
        topContent.add(infoLabel, BorderLayout.EAST);
        topCard.add(topContent, BorderLayout.CENTER);

        add(topCard, BorderLayout.NORTH);

        // Rewards Table Card
        JPanel tableCard = UIHelper.createCardPanel("Earned Rewards & Achievements Ledger");
        tableModel = new DefaultTableModel(new Object[]{"ID", "Achievement Name", "Points", "Description", "Earned Date"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        bottomBtnPanel.setOpaque(false);
        JButton deleteRewardBtn = new JButton("Delete Selected");
        deleteRewardBtn.setFont(Theme.BODY_BOLD);
        deleteRewardBtn.setBackground(new Color(38, 44, 54));
        deleteRewardBtn.setForeground(Theme.TEXT_PRIMARY);
        deleteRewardBtn.setFocusPainted(false);
        deleteRewardBtn.addActionListener(e -> handleDeleteReward(table));
        bottomBtnPanel.add(deleteRewardBtn);

        tableCard.add(bottomBtnPanel, BorderLayout.SOUTH);
        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshData() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        int totalPts = mainFrame.getRewardService().getTotalPoints(user.getUserId());
        pointsLabel.setText("⭐ Total Rewards: " + totalPts + " Pts");

        List<Reward> rewards = mainFrame.getRewardService().getUserRewards(user.getUserId());
        tableModel.setRowCount(0);
        for (Reward r : rewards) {
            tableModel.addRow(new Object[]{
                    r.getRewardId(),
                    r.getRewardName(),
                    "+" + r.getPoints() + " Pts",
                    r.getDescription(),
                    r.getEarnedDate().toString()
            });
        }
    }

    private void handleDeleteReward(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select a reward record from the table.");
            return;
        }
        int rewardId = (int) table.getValueAt(selectedRow, 0);
        try {
            mainFrame.getRewardService().deleteReward(rewardId);
            mainFrame.refreshAllPanels();
        } catch (Exception ex) {
            UIHelper.showError(this, "Error deleting reward: " + ex.getMessage());
        }
    }
}
