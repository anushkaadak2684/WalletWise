package gui;

import model.Budget;
import model.Expense;
import model.FixedExpense;
import model.VariableExpense;
import model.Wallet;
import model.enums.ExpenseCategory;
import model.enums.NotificationType;
import model.enums.RecurringFrequency;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExpensePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;

    private JComboBox<ExpenseCategory> catCombo;
    private JComboBox<String> typeCombo;
    private JTextField amountField;
    private JTextField dateField;
    private JTextField descField;
    private JComboBox<RecurringFrequency> freqCombo;
    private JTextField maxExpectedField;
    private JLabel freqLabel;
    private JLabel maxLabel;

    private DefaultTableModel tableModel;

    public ExpensePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Form Panel
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder("Log New Expense"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; catCombo = new JComboBox<>(ExpenseCategory.values()); formCard.add(catCombo, gbc);

        gbc.gridx = 2; formCard.add(new JLabel("Expense Type:"), gbc);
        gbc.gridx = 3; typeCombo = new JComboBox<>(new String[]{"FIXED", "VARIABLE"});
        typeCombo.addActionListener(e -> updateTypeFields());
        formCard.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formCard.add(new JLabel("Amount (₹):"), gbc);
        gbc.gridx = 1; amountField = new JTextField(12); formCard.add(amountField, gbc);

        gbc.gridx = 2; formCard.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3; dateField = new JTextField(LocalDate.now().toString(), 12); formCard.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formCard.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; descField = new JTextField(30); formCard.add(descField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; freqLabel = new JLabel("Recurring Freq:"); formCard.add(freqLabel, gbc);
        gbc.gridx = 1; freqCombo = new JComboBox<>(RecurringFrequency.values()); formCard.add(freqCombo, gbc);

        gbc.gridx = 2; maxLabel = new JLabel("Max Expected Amount:"); formCard.add(maxLabel, gbc);
        gbc.gridx = 3; maxExpectedField = new JTextField("0.00", 12); formCard.add(maxExpectedField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        JButton addExpBtn = UIHelper.createBlueButton("Add Expense");
        addExpBtn.addActionListener(e -> handleAddExpense());
        formCard.add(addExpBtn, gbc);

        add(formCard, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Category", "Amount", "Date", "Description", "Details"}, 0);
        JTable table = new JTable(tableModel);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteExpBtn = UIHelper.createBlueButton("Delete Selected Expense");
        deleteExpBtn.addActionListener(e -> handleDeleteExpense(table));
        bottomBtnPanel.add(deleteExpBtn);

        centerPanel.add(bottomBtnPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        updateTypeFields();
    }

    private void updateTypeFields() {
        boolean isFixed = "FIXED".equals(typeCombo.getSelectedItem());
        freqLabel.setEnabled(isFixed);
        freqCombo.setEnabled(isFixed);
        maxLabel.setEnabled(!isFixed);
        maxExpectedField.setEnabled(!isFixed);
    }

    public void refreshData() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        List<Expense> expenses = mainFrame.getExpenseService().getExpensesByWallet(wallet.getWalletId());
        tableModel.setRowCount(0);
        for (Expense e : expenses) {
            String details = (e instanceof FixedExpense) ? "Freq: " + ((FixedExpense) e).getRecurringFrequency() : "Max Expected: ₹" + ((VariableExpense) e).getMaximumExpectedAmount();
            tableModel.addRow(new Object[]{e.getExpenseId(), e.getExpenseType(), e.getCategory(), "₹" + e.getAmount().toPlainString(), e.getDate().toString(), e.getDescription(), details});
        }
    }

    private void handleAddExpense() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        try {
            ExpenseCategory cat = (ExpenseCategory) catCombo.getSelectedItem();
            String typeStr = (String) typeCombo.getSelectedItem();
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String desc = descField.getText().trim();

            RecurringFrequency freq = (freqCombo.getSelectedItem() instanceof RecurringFrequency) ? (RecurringFrequency) freqCombo.getSelectedItem() : RecurringFrequency.MONTHLY;
            BigDecimal maxExp = maxExpectedField.getText().trim().isEmpty() ? amount : new BigDecimal(maxExpectedField.getText().trim());

            Expense expense = factory.ExpenseFactory.createExpense(cat, amount, date, desc, typeStr, freq, maxExp);

            mainFrame.getExpenseService().addExpense(mainFrame.getCurrentUser(), wallet, expense);

            // System automatically logs notification
            mainFrame.getNotificationService().createNotification(
                    mainFrame.getCurrentUser().getUserId(),
                    "Expense Recorded: -₹" + amount + " (" + cat + " - " + desc + ")",
                    NotificationType.TRANSACTION_ALERT
            );

            // Update Budget spent amount & check exceeded
            List<Budget> budgets = mainFrame.getBudgetService().getBudgetsByWallet(wallet.getWalletId());
            for (Budget b : budgets) {
                if (b.getCategory() == cat) {
                    mainFrame.getBudgetService().updateBudget(b, amount);
                    if (mainFrame.getBudgetService().isBudgetExceeded(b)) {
                        mainFrame.getNotificationService().createNotification(
                                mainFrame.getCurrentUser().getUserId(),
                                "WARNING: Budget exceeded for category " + cat + " (Limit: ₹" + b.getLimitAmount() + ")",
                                NotificationType.BUDGET_ALERT
                        );
                    }
                }
            }

            UIHelper.showSuccess(this, "Expense recorded successfully!");
            amountField.setText("");
            descField.setText("");
            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            UIHelper.showError(this, "Expense Error: " + ex.getMessage());
        }
    }

    private void handleDeleteExpense(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select an expense from the table.");
            return;
        }
        int expenseId = (int) table.getValueAt(selectedRow, 0);
        try {
            mainFrame.getExpenseService().deleteExpense(expenseId);
            UIHelper.showSuccess(this, "Expense deleted successfully.");
            mainFrame.refreshAllPanels();
        } catch (Exception ex) {
            UIHelper.showError(this, "Delete Error: " + ex.getMessage());
        }
    }
}
