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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Create Goal Card
        JPanel goalCard = new JPanel(new GridBagLayout());
        goalCard.setBorder(BorderFactory.createTitledBorder("Define New Savings Goal"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; goalCard.add(new JLabel("Goal Name:"), gbc);
        gbc.gridx = 1; nameField = new JTextField(15); goalCard.add(nameField, gbc);

        gbc.gridx = 2; goalCard.add(new JLabel("Target Amount (₹):"), gbc);
        gbc.gridx = 3; targetField = new JTextField(15); goalCard.add(targetField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; goalCard.add(new JLabel("Target Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; dateField = new JTextField(LocalDate.now().plusYears(1).toString(), 15); goalCard.add(dateField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton createGoalBtn = UIHelper.createBlueButton("Create Savings Goal");
        createGoalBtn.setPreferredSize(new Dimension(200, 38));
        createGoalBtn.addActionListener(e -> handleCreateGoal());
        goalCard.add(createGoalBtn, gbc);

        add(goalCard, BorderLayout.NORTH);

        // Savings Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Goal Name", "Target Amount", "Saved Amount", "Remaining Amount", "Progress %", "Target Date", "Status"}, 0);
        JTable table = new JTable(tableModel);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton depositGoalBtn = UIHelper.createBlueButton("Add Funds to Selected Goal");
        depositGoalBtn.addActionListener(e -> handleDepositToGoal(table));
        bottomBtnPanel.add(depositGoalBtn);

        JButton deleteGoalBtn = UIHelper.createBlueButton("Delete Selected Goal");
        deleteGoalBtn.addActionListener(e -> handleDeleteGoal(table));
        bottomBtnPanel.add(deleteGoalBtn);

        centerPanel.add(bottomBtnPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
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

    public static class SavingsGoalItem {
        private SavingsGoal goal;
        public SavingsGoalItem(SavingsGoal goal) { this.goal = goal; }
        public SavingsGoal getGoal() { return goal; }
        @Override
        public String toString() { return goal.getGoalName() + " (ID: " + goal.getGoalId() + ")"; }
    }
}
