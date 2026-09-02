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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));

        JPanel badgeCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        badgeCard.setBorder(BorderFactory.createTitledBorder("System Rewards & Points Summary"));
        pointsLabel = new JLabel("Total Reward Points: 0 Pts");
        pointsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        pointsLabel.setForeground(Theme.WARNING_COLOR);
        badgeCard.add(pointsLabel);

        JPanel infoCard = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel("<html>Rewards are automatically granted by the system upon achieving financial milestones, setting savings goals, and maintaining healthy budgets.</html>");
        infoLabel.setFont(Theme.BODY_FONT);
        infoLabel.setBorder(new EmptyBorder(8, 10, 8, 10));
        infoCard.add(infoLabel, BorderLayout.CENTER);

        topPanel.add(badgeCard, BorderLayout.NORTH);
        topPanel.add(infoCard, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Reward Name", "Points Earned", "System Description", "Earned Date"}, 0);
        JTable table = new JTable(tableModel);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteRewardBtn = UIHelper.createBlueButton("Delete Selected Reward Record");
        deleteRewardBtn.addActionListener(e -> handleDeleteReward(table));
        bottomBtnPanel.add(deleteRewardBtn);

        centerPanel.add(bottomBtnPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        int totalPts = mainFrame.getRewardService().getTotalPoints(user.getUserId());
        pointsLabel.setText("Total Reward Points: " + totalPts + " Pts");

        List<Reward> rewards = mainFrame.getRewardService().getUserRewards(user.getUserId());
        tableModel.setRowCount(0);
        for (Reward r : rewards) {
            tableModel.addRow(new Object[]{
                    r.getRewardId(),
                    r.getRewardName(),
                    r.getPoints(),
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
