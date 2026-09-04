package gui;

import model.Budget;
import model.Expense;
import model.enums.ExpenseCategory;
import model.enums.NotificationType;
import model.enums.RecurringFrequency;
import model.Wallet;

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

    private DefaultTableModel tableModel;

    public ExpensePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        // TOP: Add Expense Form
        JPanel formCard = UIHelper.createCardPanel("Log New Expense");
        JPanel formGrid = new JPanel(new GridLayout(4, 4, 12, 12));
        formGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        formGrid.add(new JLabel("Category:"));
        catCombo = new JComboBox<>(ExpenseCategory.values());
        formGrid.add(catCombo);

        formGrid.add(new JLabel("Expense Type:"));
        typeCombo = new JComboBox<>(new String[]{"VARIABLE", "FIXED"});
        formGrid.add(typeCombo);

        formGrid.add(new JLabel("Amount (₹):"));
        amountField = new JTextField();
        formGrid.add(amountField);

        formGrid.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString());
        formGrid.add(dateField);

        formGrid.add(new JLabel("Description:"));
        descField = new JTextField();
        formGrid.add(descField);

        formGrid.add(new JLabel("Frequency (Fixed):"));
        freqCombo = new JComboBox<>(RecurringFrequency.values());
        formGrid.add(freqCombo);

        formGrid.add(new JLabel("Max Expected (₹):"));
        maxExpectedField = new JTextField();
        formGrid.add(maxExpectedField);

        JButton addBtn = UIHelper.createBlueButton("Record Expense");
        addBtn.addActionListener(e -> handleAddExpense());
        formGrid.add(addBtn);

        formCard.add(formGrid, BorderLayout.CENTER);
        add(formCard, BorderLayout.NORTH);

        // CENTER: Expense Table
        JPanel tableCard = UIHelper.createCardPanel("Expense History");
        String[] cols = {"ID", "Category", "Amount", "Date", "Description", "Type", "Details"};
        tableModel = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> handleDeleteExpense(table));
        actionPanel.add(deleteBtn);
        tableCard.add(actionPanel, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshData() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        List<Expense> expenses = mainFrame.getExpenseService().getExpensesByWallet(wallet.getWalletId());
        tableModel.setRowCount(0);
        for (Expense exp : expenses) {
            String details = "-";
            if (exp instanceof model.FixedExpense) {
                details = "Freq: " + ((model.FixedExpense) exp).getRecurringFrequency();
            } else if (exp instanceof model.VariableExpense) {
                details = "Max: ₹" + ((model.VariableExpense) exp).getMaximumExpectedAmount();
            }

            tableModel.addRow(new Object[]{
                    exp.getExpenseId(),
                    exp.getCategory(),
                    "₹" + exp.getAmount().toPlainString(),
                    exp.getDate().toString(),
                    exp.getDescription() != null ? exp.getDescription() : "-",
                    exp.getClass().getSimpleName().replace("Expense", ""),
                    details
            });
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

            Expense expense = "FIXED".equalsIgnoreCase(typeStr)
                    ? new model.FixedExpense(0, cat, amount, date, desc, freq)
                    : new model.VariableExpense(0, cat, amount, date, desc, maxExp);

            mainFrame.getExpenseService().addExpense(mainFrame.getCurrentUser(), wallet, expense);

            // System automatically logs notification
            mainFrame.getNotificationService().createNotification(
                    mainFrame.getCurrentUser().getUserId(),
                    "Expense Recorded: -₹" + amount + " (" + cat + " - " + desc + ")",
                    NotificationType.TRANSACTION_ALERT
            );

            // Proactive Wallet Limit Check
            if (wallet.isLimitExceeded(amount)) {
                UIHelper.showWarning(this, "[SPENDING LIMIT WARNING]\n" + wallet.getLimitWarningMessage() + "\nPlease review your expenses to avoid jeopardizing your active savings and budgets.");
            }

            // Proactive Category Budget Check (80% Caution & 100% Exceeded)
            List<Budget> budgets = mainFrame.getBudgetService().getBudgetsByWallet(wallet.getWalletId());
            for (Budget b : budgets) {
                if (b.getCategory() == cat) {
                    mainFrame.getBudgetService().updateBudget(b, amount);
                    BigDecimal newSpent = b.getSpentAmount();
                    BigDecimal budgetLimit = b.getLimitAmount();

                    if (mainFrame.getBudgetService().isBudgetExceeded(b)) {
                        String alertMsg = "[BUDGET EXCEEDED] Category " + cat + " (Spent: ₹" + newSpent + " / Limit: ₹" + budgetLimit + "). High spending may compromise your savings goals!";
                        mainFrame.getNotificationService().createNotification(
                                mainFrame.getCurrentUser().getUserId(),
                                alertMsg,
                                NotificationType.BUDGET_ALERT
                        );
                        UIHelper.showWarning(this, "[CATEGORY BUDGET EXCEEDED]\nCategory: " + cat + "\nTotal Spent: ₹" + newSpent + " (Budget Limit: ₹" + budgetLimit + ")\nHigh spending will impact your savings milestones!");

                    } else if (budgetLimit.compareTo(BigDecimal.ZERO) > 0 && newSpent.compareTo(budgetLimit.multiply(new BigDecimal("0.80"))) >= 0) {
                        String alertMsg = "[BUDGET CAUTION] 80% of budget reached for " + cat + " (Spent: ₹" + newSpent + " / Limit: ₹" + budgetLimit + "). Slow down spending to protect your savings goals!";
                        mainFrame.getNotificationService().createNotification(
                                mainFrame.getCurrentUser().getUserId(),
                                alertMsg,
                                NotificationType.BUDGET_ALERT
                        );
                        UIHelper.showWarning(this, "[BUDGET CAUTION]\nYou have utilized 80%+ of your " + cat + " budget!\nSpent: ₹" + newSpent + " / Limit: ₹" + budgetLimit + "\nPlease slow down discretionary spending to stay on track for your savings goals.");
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
