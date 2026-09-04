package gui;

import model.Budget;
import model.Notification;
import model.Transaction;
import model.User;
import model.Wallet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;

    private JLabel ovBalanceLabel;
    private JLabel ovExpenseLabel;
    private JLabel ovPointsLabel;
    private JLabel ovNotificationsLabel;

    private DefaultTableModel ovTransTableModel;
    private DefaultTableModel ovBudgetsTableModel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 18));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initUI();
    }

    private void initUI() {
        // Metric Cards Panel
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsPanel.setOpaque(false);

        ovBalanceLabel = UIHelper.createMetricCard(metricsPanel, "Available Balance", "₹0.00", Theme.SUCCESS_COLOR);
        ovExpenseLabel = UIHelper.createMetricCard(metricsPanel, "Total Expenses", "₹0.00", Theme.DANGER_COLOR);
        ovPointsLabel = UIHelper.createMetricCard(metricsPanel, "Reward Points", "0 Pts", Theme.WARNING_COLOR);
        ovNotificationsLabel = UIHelper.createMetricCard(metricsPanel, "Unread Alerts", "0 New", Theme.CYAN_ACCENT);

        add(metricsPanel, BorderLayout.NORTH);

        // Sub Tables Grid
        JPanel tablesGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        tablesGrid.setOpaque(false);

        // Recent Transactions Card
        JPanel transCard = UIHelper.createCardPanel("Recent Transactions");
        ovTransTableModel = new DefaultTableModel(new Object[]{"Type", "Amount", "Date", "Description"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable ovTransTable = new JTable(ovTransTableModel);
        ovTransTable.setRowHeight(32);
        transCard.add(new JScrollPane(ovTransTable), BorderLayout.CENTER);

        // Active Budgets Card
        JPanel budgetCard = UIHelper.createCardPanel("Budget Usage Summary");
        ovBudgetsTableModel = new DefaultTableModel(new Object[]{"Category", "Spent", "Limit", "Usage %", "Status"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable ovBudgetTable = new JTable(ovBudgetsTableModel);
        ovBudgetTable.setRowHeight(32);
        budgetCard.add(new JScrollPane(ovBudgetTable), BorderLayout.CENTER);

        tablesGrid.add(transCard);
        tablesGrid.add(budgetCard);

        add(tablesGrid, BorderLayout.CENTER);
    }

    public void refreshData() {
        User user = mainFrame.getCurrentUser();
        Wallet wallet = mainFrame.getCurrentWallet();

        if (user == null || wallet == null) return;

        try {
            int walletId = wallet.getWalletId();
            int userId = user.getUserId();

            ovBalanceLabel.setText("₹" + wallet.getBalance().toPlainString());

            BigDecimal totalExp = mainFrame.getExpenseService().calculateTotalExpense(walletId);
            ovExpenseLabel.setText("₹" + totalExp.toPlainString());

            int points = mainFrame.getRewardService().getTotalPoints(userId);
            ovPointsLabel.setText(points + " Pts");

            List<Notification> unread = mainFrame.getNotificationService().getUnreadNotifications(userId);
            ovNotificationsLabel.setText(unread.size() + " New");

            // Transactions Table
            List<Transaction> transactions = mainFrame.getWalletService().getTransactionsByWallet(walletId);
            ovTransTableModel.setRowCount(0);
            for (Transaction t : transactions) {
                ovTransTableModel.addRow(new Object[]{t.getTransactionType(), "₹" + t.getAmount().toPlainString(), t.getTransactionDate().toLocalDate().toString(), t.getDescription()});
            }

            // Budgets Table
            List<Budget> budgets = mainFrame.getBudgetService().getBudgetsByWallet(walletId);
            ovBudgetsTableModel.setRowCount(0);
            for (Budget b : budgets) {
                boolean exceeded = mainFrame.getBudgetService().isBudgetExceeded(b);
                double pct = mainFrame.getBudgetService().getBudgetUsagePercentage(b);
                String status = exceeded ? "EXCEEDED" : "WITHIN LIMIT";
                ovBudgetsTableModel.addRow(new Object[]{b.getCategory(), "₹" + b.getSpentAmount().toPlainString(), "₹" + b.getLimitAmount().toPlainString(), String.format("%.1f%%", pct), status});
            }

        } catch (Exception ex) {
            System.err.println("Dashboard refresh error: " + ex.getMessage());
        }
    }
}
