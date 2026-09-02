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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Metric Cards Panel
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 15, 0));

        ovBalanceLabel = UIHelper.createMetricCard(metricsPanel, "Current Balance", "₹0.00", Theme.SUCCESS_COLOR);
        ovExpenseLabel = UIHelper.createMetricCard(metricsPanel, "Total Expenses", "₹0.00", Theme.DANGER_COLOR);
        ovPointsLabel = UIHelper.createMetricCard(metricsPanel, "Reward Points", "0 Pts", Theme.WARNING_COLOR);
        ovNotificationsLabel = UIHelper.createMetricCard(metricsPanel, "Unread Alerts", "0 New", Theme.PRIMARY_ACCENT);

        add(metricsPanel, BorderLayout.NORTH);

        // Sub Tables Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Recent Transactions
        JPanel transPanel = new JPanel(new BorderLayout());
        transPanel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        ovTransTableModel = new DefaultTableModel(new Object[]{"Type", "Amount", "Date", "Description"}, 0);
        JTable ovTransTable = new JTable(ovTransTableModel);
        transPanel.add(new JScrollPane(ovTransTable), BorderLayout.CENTER);

        // Active Budgets
        JPanel budgetPanel = new JPanel(new BorderLayout());
        budgetPanel.setBorder(BorderFactory.createTitledBorder("Budget Usage Summary"));
        ovBudgetsTableModel = new DefaultTableModel(new Object[]{"Category", "Spent", "Limit", "Usage %", "Status"}, 0);
        JTable ovBudgetTable = new JTable(ovBudgetsTableModel);
        budgetPanel.add(new JScrollPane(ovBudgetTable), BorderLayout.CENTER);

        splitPane.setLeftComponent(transPanel);
        splitPane.setRightComponent(budgetPanel);

        add(splitPane, BorderLayout.CENTER);
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
