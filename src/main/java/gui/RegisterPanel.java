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
        setBorder(new EmptyBorder(25, 25, 25, 25));
        initUI();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        add(phoneField, gbc);

        // Register Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton regBtn = UIHelper.createBlueButton("Register & Set Up Wallet");
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

            UIHelper.showSuccess(this, "Registration successful! Please set up your wallet.");

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
