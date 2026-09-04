package gui;

import model.SavingsGoal;
import model.Wallet;
import model.enums.NotificationType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SavingsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;

    private JTextField nameField;
    private JTextField targetField;
    private JTextField dateField;

    private DefaultTableModel tableModel;

    public SavingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 18));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initUI();
    }

    private void initUI() {
        // Create Goal Card
        JPanel goalCard = UIHelper.createCardPanel("Define New Savings Goal");
        JPanel goalGrid = new JPanel(new GridBagLayout());
        goalGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLbl = new JLabel("Goal Name:");
        nameLbl.setForeground(Theme.TEXT_MUTED);
        goalGrid.add(nameLbl, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        goalGrid.add(nameField, gbc);

        gbc.gridx = 2;
        JLabel targetLbl = new JLabel("Target Amount (₹):");
        targetLbl.setForeground(Theme.TEXT_MUTED);
        goalGrid.add(targetLbl, gbc);

        gbc.gridx = 3;
        targetField = new JTextField(15);
        goalGrid.add(targetField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel dateLbl = new JLabel("Target Date:");
        dateLbl.setForeground(Theme.TEXT_MUTED);
        goalGrid.add(dateLbl, gbc);

        gbc.gridx = 1;
        dateField = new JTextField(LocalDate.now().plusYears(1).toString(), 15);
        goalGrid.add(dateField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 6, 10);
        JButton createGoalBtn = UIHelper.createBlueButton("Create Savings Goal");
        createGoalBtn.addActionListener(e -> handleCreateGoal());
        goalGrid.add(createGoalBtn, gbc);

        goalCard.add(goalGrid, BorderLayout.CENTER);
        add(goalCard, BorderLayout.NORTH);

        // Savings Table Card
        JPanel tableCard = UIHelper.createCardPanel("Active Savings Goals & Milestones");
        tableModel = new DefaultTableModel(new Object[]{"ID", "Goal Name", "Target Amount", "Saved Amount", "Remaining", "Progress %", "Target Date", "Status"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBtnPanel.setOpaque(false);

        JButton depositGoalBtn = UIHelper.createBlueButton("Add Funds to Goal");
        depositGoalBtn.addActionListener(e -> handleDepositToGoal(table));
        bottomBtnPanel.add(depositGoalBtn);

        JButton deleteGoalBtn = new JButton("Delete Selected");
        deleteGoalBtn.setFont(Theme.BODY_BOLD);
        deleteGoalBtn.setBackground(new Color(38, 44, 54));
        deleteGoalBtn.setForeground(Theme.TEXT_PRIMARY);
        deleteGoalBtn.setFocusPainted(false);
        deleteGoalBtn.addActionListener(e -> handleDeleteGoal(table));
        bottomBtnPanel.add(deleteGoalBtn);

        tableCard.add(bottomBtnPanel, BorderLayout.SOUTH);
        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshData() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        List<SavingsGoal> goals = mainFrame.getSavingsGoalService().getGoalsByWallet(wallet.getWalletId());
        tableModel.setRowCount(0);

        for (SavingsGoal g : goals) {
            boolean completed = mainFrame.getSavingsGoalService().isGoalCompleted(g);
            BigDecimal remaining = mainFrame.getSavingsGoalService().getRemainingAmount(g);
            double pct = mainFrame.getSavingsGoalService().getCompletionPercentage(g);
            String status = completed ? "COMPLETED" : "IN PROGRESS";

            tableModel.addRow(new Object[]{
                    g.getGoalId(),
                    g.getGoalName(),
                    "₹" + g.getTargetAmount().toPlainString(),
                    "₹" + g.getSavedAmount().toPlainString(),
                    "₹" + (remaining.compareTo(BigDecimal.ZERO) < 0 ? "0.00" : remaining.toPlainString()),
                    String.format("%.1f%%", pct),
                    g.getTargetDate(),
                    status
            });
        }
    }

    private void handleCreateGoal() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        try {
            String name = nameField.getText().trim();
            BigDecimal target = new BigDecimal(targetField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());

            mainFrame.getSavingsGoalService().createSavingsGoal(wallet.getWalletId(), name, target, date);

            // Trigger System Notification
            mainFrame.getNotificationService().createNotification(
                    mainFrame.getCurrentUser().getUserId(),
                    "New Savings Goal Defined: " + name + " (Target: ₹" + target + ")",
                    NotificationType.SAVINGS_ALERT
            );

            UIHelper.showSuccess(this, "Savings Goal created successfully!");
            nameField.setText("");
            targetField.setText("");
            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            UIHelper.showError(this, "Goal Creation Error: " + ex.getMessage());
        }
    }

    private void handleDepositToGoal(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select a savings goal from the table.");
            return;
        }
        int goalId = (int) table.getValueAt(selectedRow, 0);
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        String input = JOptionPane.showInputDialog(this, "Enter amount to add to savings goal (₹):", "Add Funds to Goal", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        try {
            BigDecimal amount = new BigDecimal(input.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                UIHelper.showError(this, "Deposit amount must be greater than zero.");
                return;
            }

            SavingsGoal goal = mainFrame.getSavingsGoalService().getGoalById(goalId);
            if (goal == null) {
                UIHelper.showError(this, "Goal not found.");
                return;
            }

            boolean wasCompletedBefore = mainFrame.getSavingsGoalService().isGoalCompleted(goal);

            // Atomically contribute to goal via service (deducts from wallet, records transaction, updates goal)
            mainFrame.getSavingsGoalService().contributeToSavingsGoal(mainFrame.getCurrentUser(), wallet, goal, amount);

            boolean isCompletedNow = mainFrame.getSavingsGoalService().isGoalCompleted(goal);

            if (!wasCompletedBefore && isCompletedNow) {
                UIHelper.showSuccess(this, "Deposited ₹" + amount + "! CONGRATULATIONS! Savings Goal Achieved (+100 Reward Points)!");
            } else {
                UIHelper.showSuccess(this, "Deposited ₹" + amount + " to savings goal successfully!");
            }

            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            UIHelper.showError(this, "Deposit to Goal Error: " + ex.getMessage());
        }
    }

    private void handleDeleteGoal(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select a savings goal from the table.");
            return;
        }
        int goalId = (int) table.getValueAt(selectedRow, 0);
        try {
            mainFrame.getSavingsGoalService().deleteGoal(goalId);
            UIHelper.showSuccess(this, "Savings goal deleted.");
            mainFrame.refreshAllPanels();
        } catch (Exception ex) {
            UIHelper.showError(this, "Delete Error: " + ex.getMessage());
        }
    }
}
