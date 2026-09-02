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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 15, 0));

        // Deposit Panel
        JPanel depositCard = new JPanel(new GridBagLayout());
        depositCard.setBorder(BorderFactory.createTitledBorder("Deposit Funds"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; depositCard.add(new JLabel("Amount (₹):"), gbc);
        gbc.gridx = 1; depositAmountField = new JTextField(12); depositCard.add(depositAmountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; depositCard.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; depositDescField = new JTextField("Salary / Deposit", 12); depositCard.add(depositDescField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton depositBtn = UIHelper.createBlueButton("Deposit Money");
        depositBtn.addActionListener(e -> handleDeposit());
        depositCard.add(depositBtn, gbc);

        // Withdraw Panel
        JPanel withdrawCard = new JPanel(new GridBagLayout());
        withdrawCard.setBorder(BorderFactory.createTitledBorder("Withdraw Funds"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; withdrawCard.add(new JLabel("Amount (₹):"), gbc);
        gbc.gridx = 1; withdrawAmountField = new JTextField(12); withdrawCard.add(withdrawAmountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; withdrawCard.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; withdrawDescField = new JTextField("Cash / Withdrawal", 12); withdrawCard.add(withdrawDescField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton withdrawBtn = UIHelper.createBlueButton("Withdraw Money");
        withdrawBtn.addActionListener(e -> handleWithdraw());
        withdrawCard.add(withdrawBtn, gbc);

        actionsPanel.add(depositCard);
        actionsPanel.add(withdrawCard);

        add(actionsPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Amount", "Date", "Description"}, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
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

            UIHelper.showSuccess(this, "Withdrew ₹" + amt + " successfully!");
            withdrawAmountField.setText("");
            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            UIHelper.showError(this, "Withdrawal Error: " + ex.getMessage());
        }
    }
}
