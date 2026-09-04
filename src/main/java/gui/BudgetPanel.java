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
        setLayout(new BorderLayout(0, 18));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initUI();
    }

    private void initUI() {
        // Form Panel
        JPanel formCard = UIHelper.createCardPanel("Set Category Budget");
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel catLbl = new JLabel("Category:");
        catLbl.setForeground(Theme.TEXT_MUTED);
        formGrid.add(catLbl, gbc);

        gbc.gridx = 1;
        categoryCombo = new JComboBox<>(ExpenseCategory.values());
        formGrid.add(categoryCombo, gbc);

        gbc.gridx = 2;
        JLabel limLbl = new JLabel("Limit Amount (₹):");
        limLbl.setForeground(Theme.TEXT_MUTED);
        formGrid.add(limLbl, gbc);

        gbc.gridx = 3;
        limitField = new JTextField(12);
        formGrid.add(limitField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel startLbl = new JLabel("Start Date:");
        startLbl.setForeground(Theme.TEXT_MUTED);
        formGrid.add(startLbl, gbc);

        gbc.gridx = 1;
        startDateField = new JTextField(LocalDate.now().toString(), 12);
        formGrid.add(startDateField, gbc);

        gbc.gridx = 2;
        JLabel endLbl = new JLabel("End Date:");
        endLbl.setForeground(Theme.TEXT_MUTED);
        formGrid.add(endLbl, gbc);

        gbc.gridx = 3;
        endDateField = new JTextField(LocalDate.now().plusMonths(1).toString(), 12);
        formGrid.add(endDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 10, 6, 10);
        JButton addBudgetBtn = UIHelper.createBlueButton("Create Category Budget");
        addBudgetBtn.addActionListener(e -> handleCreateBudget());
        formGrid.add(addBudgetBtn, gbc);

        formCard.add(formGrid, BorderLayout.CENTER);
        add(formCard, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UIHelper.createCardPanel("Category Budgets & Progress");
        tableModel = new DefaultTableModel(new Object[]{"ID", "Category", "Limit", "Spent", "Usage %", "Status", "Start Date", "End Date"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        bottomBtnPanel.setOpaque(false);
        JButton deleteBudgetBtn = new JButton("Delete Selected");
        deleteBudgetBtn.setFont(Theme.BODY_BOLD);
        deleteBudgetBtn.setBackground(new Color(38, 44, 54));
        deleteBudgetBtn.setForeground(Theme.TEXT_PRIMARY);
        deleteBudgetBtn.setFocusPainted(false);
        deleteBudgetBtn.addActionListener(e -> handleDeleteBudget(table));
        bottomBtnPanel.add(deleteBudgetBtn);

        tableCard.add(bottomBtnPanel, BorderLayout.SOUTH);
        add(tableCard, BorderLayout.CENTER);
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
