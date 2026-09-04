package gui;

import model.BusinessWallet;
import model.PersonalWallet;
import model.User;
import model.Wallet;
import model.enums.WalletType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class UIHelper {

    public static JButton createBlueButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Theme.PRIMARY_BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(Theme.BODY_BOLD);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static JPanel createCardPanel(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        if (title != null && !title.trim().isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(Theme.HEADER_FONT);
            titleLabel.setForeground(Theme.TEXT_PRIMARY);
            titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
            card.add(titleLabel, BorderLayout.NORTH);
        }
        return card;
    }

    public static JLabel createMetricCard(JPanel container, String title, String initialVal, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Theme.BODY_FONT);
        titleLbl.setForeground(Theme.TEXT_MUTED);

        JLabel valLbl = new JLabel(initialVal);
        valLbl.setFont(Theme.METRIC_VAL_FONT);
        valLbl.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);
        container.add(card);

        return valLbl;
    }

    public static Wallet promptCreateWallet(Component parentOwner, LoginRegisterFrame frame, User user) {
        Window parentWindow = SwingUtilities.getWindowAncestor(parentOwner);
        JDialog dialog = new JDialog(parentWindow, "Set Up Initial Wallet", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());

        JPanel rootContainer = new JPanel(new BorderLayout());
        rootContainer.setBackground(Theme.BG_DARK);
        rootContainer.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER, 1));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(Theme.HEADER_BG);
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("Create Initial Wallet");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Configure your initial balance & spending limit for " + user.getFullName());
        subLabel.setFont(Theme.BODY_FONT);
        subLabel.setForeground(Theme.TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.BG_DARK);
        formPanel.setBorder(new EmptyBorder(25, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel typeLbl = new JLabel("Wallet Type:");
        typeLbl.setFont(Theme.BODY_BOLD);
        typeLbl.setForeground(Theme.TEXT_PRIMARY);

        JComboBox<WalletType> typeCombo = new JComboBox<>(WalletType.values());
        typeCombo.setFont(Theme.BODY_FONT);
        typeCombo.setPreferredSize(new Dimension(230, 38));

        JLabel balLbl = new JLabel("Initial Balance (₹):");
        balLbl.setFont(Theme.BODY_BOLD);
        balLbl.setForeground(Theme.TEXT_PRIMARY);

        JTextField balanceField = new JTextField("0.00");
        balanceField.setFont(Theme.BODY_FONT);
        balanceField.setPreferredSize(new Dimension(230, 38));

        JLabel limitLbl = new JLabel("Spending Limit (₹):");
        limitLbl.setFont(Theme.BODY_BOLD);
        limitLbl.setForeground(Theme.TEXT_PRIMARY);

        JTextField limitField = new JTextField("50000.00");
        limitField.setFont(Theme.BODY_FONT);
        limitField.setPreferredSize(new Dimension(230, 38));

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(typeLbl, gbc);
        gbc.gridx = 1; formPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(balLbl, gbc);
        gbc.gridx = 1; formPanel.add(balanceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(limitLbl, gbc);
        gbc.gridx = 1; formPanel.add(limitField, gbc);

        typeCombo.addActionListener(e -> {
            if (typeCombo.getSelectedItem() == WalletType.PERSONAL) {
                limitField.setText("50000.00");
            } else {
                limitField.setText("500000.00");
            }
        });

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Theme.BG_DARK);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 15, 20));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(Theme.BODY_BOLD);
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.setFocusPainted(false);

        JButton createBtn = UIHelper.createBlueButton("Create Wallet");
        createBtn.setPreferredSize(new Dimension(150, 40));

        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);

        rootContainer.add(headerPanel, BorderLayout.NORTH);
        rootContainer.add(formPanel, BorderLayout.CENTER);
        rootContainer.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(rootContainer);

        final Wallet[] createdWallet = new Wallet[1];

        createBtn.addActionListener(e -> {
            try {
                BigDecimal balance = new BigDecimal(balanceField.getText().trim());
                BigDecimal limit = new BigDecimal(limitField.getText().trim());
                if (balance.compareTo(BigDecimal.ZERO) < 0 || limit.compareTo(BigDecimal.ZERO) <= 0) {
                    showError(dialog, "Balance cannot be negative and limit must be greater than zero.");
                    return;
                }

                WalletType type = (WalletType) typeCombo.getSelectedItem();
                Wallet wallet = (type == WalletType.PERSONAL)
                        ? new PersonalWallet(0, balance, limit)
                        : new BusinessWallet(0, balance, limit);

                frame.getWalletService().createWallet(wallet, user.getUserId());
                frame.getUserService().assignWallet(user, wallet);
                createdWallet[0] = wallet;
                dialog.dispose();

            } catch (Exception ex) {
                showError(dialog, "Error creating wallet: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setMinimumSize(new Dimension(490, 400));
        dialog.setSize(490, 400);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parentOwner);
        dialog.setVisible(true);

        return createdWallet[0];
    }
}
