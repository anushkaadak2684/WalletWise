package gui;

import model.Transaction;
import model.Wallet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class TransactionPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;

    private JTextField depositAmountField;
    private JTextField depositDescField;
    private JTextField withdrawAmountField;
    private JTextField withdrawDescField;

    private DefaultTableModel tableModel;

    public TransactionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        // TOP PANEL: Quick Action Cards (Deposit & Withdraw)
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 15, 0));

        // Deposit Card
        JPanel depositCard = UIHelper.createCardPanel("Quick Deposit");
        JPanel depGrid = new JPanel(new GridLayout(3, 2, 10, 10));
        depGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        depGrid.add(new JLabel("Amount (₹):"));
        depositAmountField = new JTextField();
        depGrid.add(depositAmountField);
        depGrid.add(new JLabel("Description:"));
        depositDescField = new JTextField();
        depGrid.add(depositDescField);
        depGrid.add(new JLabel(""));
        JButton depBtn = UIHelper.createBlueButton("Deposit Funds");
        depBtn.addActionListener(e -> handleDeposit());
        depGrid.add(depBtn);
        depositCard.add(depGrid, BorderLayout.CENTER);

        // Withdraw Card
        JPanel withdrawCard = UIHelper.createCardPanel("Quick Withdrawal");
        JPanel withGrid = new JPanel(new GridLayout(3, 2, 10, 10));
        withGrid.setBorder(new EmptyBorder(10, 10, 10, 10));
        withGrid.add(new JLabel("Amount (₹):"));
        withdrawAmountField = new JTextField();
        withGrid.add(withdrawAmountField);
        withGrid.add(new JLabel("Description:"));
        withdrawDescField = new JTextField();
        withGrid.add(withdrawDescField);
        withGrid.add(new JLabel(""));
        JButton withBtn = new JButton("Withdraw Funds");
        withBtn.setFont(Theme.BODY_BOLD);
        withBtn.addActionListener(e -> handleWithdraw());
        withGrid.add(withBtn);
        withdrawCard.add(withGrid, BorderLayout.CENTER);

        topPanel.add(depositCard);
        topPanel.add(withdrawCard);

        add(topPanel, BorderLayout.NORTH);

        // CENTER: Transaction Table Card
        JPanel tableCard = UIHelper.createCardPanel("Transaction Ledger");
        String[] cols = {"ID", "Type", "Amount", "Date", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshData() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        List<Transaction> transactions = mainFrame.getWalletService().getTransactionsByWallet(wallet.getWalletId());
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                    t.getTransactionId(),
                    t.getTransactionType(),
                    "₹" + t.getAmount().toPlainString(),
                    t.getTransactionDate().toString(),
                    t.getDescription() != null ? t.getDescription() : "-"
            });
        }
    }

    private void handleDeposit() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        try {
            BigDecimal amt = new BigDecimal(depositAmountField.getText().trim());
            String desc = depositDescField.getText().trim();

            mainFrame.getWalletService().depositMoney(mainFrame.getCurrentUser(), wallet, amt, desc);

            UIHelper.showSuccess(this, "Deposited ₹" + amt + " successfully!");
            depositAmountField.setText("");
            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            UIHelper.showError(this, "Deposit Error: " + ex.getMessage());
        }
    }

    private void handleWithdraw() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        try {
            BigDecimal amt = new BigDecimal(withdrawAmountField.getText().trim());
            String desc = withdrawDescField.getText().trim();

            mainFrame.getWalletService().withdrawMoney(mainFrame.getCurrentUser(), wallet, amt, desc);

            if (wallet.isLimitExceeded(amt)) {
                UIHelper.showWarning(this, "[WITHDRAWAL LIMIT WARNING]\n" + wallet.getLimitWarningMessage() + "\nHigh withdrawals may impact your upcoming budget and savings goals.");
            }

            UIHelper.showSuccess(this, "Withdrew ₹" + amt + " successfully!");
            withdrawAmountField.setText("");
            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            UIHelper.showError(this, "Withdrawal Error: " + ex.getMessage());
        }
    }
}
