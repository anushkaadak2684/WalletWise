package gui;

import model.Budget;
import model.enums.ExpenseCategory;
import model.enums.NotificationType;
import model.Wallet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BudgetPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;

    private JComboBox<ExpenseCategory> categoryCombo;
    private JTextField limitField;
    private JTextField startDateField;
    private JTextField endDateField;

    private DefaultTableModel tableModel;

    public BudgetPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Form Panel
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder("Set Category Budget"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; categoryCombo = new JComboBox<>(ExpenseCategory.values()); formCard.add(categoryCombo, gbc);

        gbc.gridx = 2; formCard.add(new JLabel("Limit Amount (₹):"), gbc);
        gbc.gridx = 3; limitField = new JTextField(12); formCard.add(limitField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1; startDateField = new JTextField(LocalDate.now().toString(), 12); formCard.add(startDateField, gbc);

        gbc.gridx = 2; formCard.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 3; endDateField = new JTextField(LocalDate.now().plusMonths(1).toString(), 12); formCard.add(endDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        JButton addBudgetBtn = UIHelper.createBlueButton("Create Budget");
        addBudgetBtn.addActionListener(e -> handleCreateBudget());
        formCard.add(addBudgetBtn, gbc);

        add(formCard, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Category", "Limit", "Spent", "Usage %", "Status", "Start Date", "End Date"}, 0);
        JTable table = new JTable(tableModel);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteBudgetBtn = UIHelper.createBlueButton("Delete Selected Budget");
        deleteBudgetBtn.addActionListener(e -> handleDeleteBudget(table));
        bottomBtnPanel.add(deleteBudgetBtn);

        centerPanel.add(bottomBtnPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        List<Budget> budgets = mainFrame.getBudgetService().getBudgetsByWallet(wallet.getWalletId());
        tableModel.setRowCount(0);
        for (Budget b : budgets) {
            boolean exceeded = mainFrame.getBudgetService().isBudgetExceeded(b);
            double pct = mainFrame.getBudgetService().getBudgetUsagePercentage(b);
            String status = exceeded ? "EXCEEDED" : "WITHIN LIMIT";
            tableModel.addRow(new Object[]{b.getBudgetId(), b.getCategory(), "₹" + b.getLimitAmount().toPlainString(), "₹" + b.getSpentAmount().toPlainString(), String.format("%.1f%%", pct), status, b.getStartDate(), b.getEndDate()});
        }
    }

    private void handleCreateBudget() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        try {
            ExpenseCategory cat = (ExpenseCategory) categoryCombo.getSelectedItem();
            BigDecimal limit = new BigDecimal(limitField.getText().trim());
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
            LocalDate endDate = LocalDate.parse(endDateField.getText().trim());

            mainFrame.getBudgetService().createBudget(wallet.getWalletId(), cat, limit, startDate, endDate);

            // Automatically log System Notification
            mainFrame.getNotificationService().createNotification(
                    mainFrame.getCurrentUser().getUserId(),
                    "Category Budget Set: " + cat + " (Limit: ₹" + limit + ")",
                    NotificationType.BUDGET_ALERT
            );

            UIHelper.showSuccess(this, "Budget created successfully!");
            limitField.setText("");
            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            UIHelper.showError(this, "Budget Error: " + ex.getMessage());
        }
    }

    private void handleDeleteBudget(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select a budget from the table.");
            return;
        }
        int budgetId = (int) table.getValueAt(selectedRow, 0);
        try {
            mainFrame.getBudgetService().deleteBudget(budgetId);
            UIHelper.showSuccess(this, "Budget deleted successfully.");
            mainFrame.refreshAllPanels();
        } catch (Exception ex) {
            UIHelper.showError(this, "Delete Error: " + ex.getMessage());
        }
    }
}
