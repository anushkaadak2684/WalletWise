package gui;

import model.User;
import model.Wallet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private LoginRegisterFrame parentFrame;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneField;

    public RegisterPanel(LoginRegisterFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        setBackground(Theme.CARD_BG);
        setBorder(new EmptyBorder(25, 20, 25, 20));
        initUI();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLbl = new JLabel("Full Name:");
        nameLbl.setForeground(Theme.TEXT_MUTED);
        nameLbl.setFont(Theme.BODY_BOLD);
        add(nameLbl, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(18);
        nameField.setPreferredSize(new Dimension(220, 36));
        add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel emailLbl = new JLabel("Email Address:");
        emailLbl.setForeground(Theme.TEXT_MUTED);
        emailLbl.setFont(Theme.BODY_BOLD);
        add(emailLbl, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(18);
        emailField.setPreferredSize(new Dimension(220, 36));
        add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setForeground(Theme.TEXT_MUTED);
        passLbl.setFont(Theme.BODY_BOLD);
        add(passLbl, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        passwordField.setPreferredSize(new Dimension(220, 36));
        add(passwordField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel phoneLbl = new JLabel("Phone Number:");
        phoneLbl.setForeground(Theme.TEXT_MUTED);
        phoneLbl.setFont(Theme.BODY_BOLD);
        add(phoneLbl, gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(18);
        phoneField.setPreferredSize(new Dimension(220, 36));
        add(phoneField, gbc);

        // Register Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 8, 10);
        JButton regBtn = UIHelper.createBlueButton("Register & Configure Wallet");
        regBtn.setPreferredSize(new Dimension(220, 42));
        regBtn.addActionListener(e -> handleRegister());
        add(regBtn, gbc);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIHelper.showError(this, "Name, email, and password are required.");
            return;
        }

        try {
            User existing = parentFrame.getUserService().getUserByEmail(email);
            if (existing != null) {
                UIHelper.showError(this, "An account with this email already exists.");
                return;
            }

            User user = parentFrame.getUserService().createUser(name, email, password, phone, null);

            UIHelper.showSuccess(this, "Registration successful! Please configure your initial wallet.");

            Wallet wallet = UIHelper.promptCreateWallet(this, parentFrame, user);

            if (wallet != null) {
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                phoneField.setText("");

                parentFrame.openMainFrame(user, wallet);
            }

        } catch (Exception ex) {
            UIHelper.showError(this, "Registration Error: " + ex.getMessage());
        }
    }
}
