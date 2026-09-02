package gui;

import model.User;
import model.Wallet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private LoginRegisterFrame parentFrame;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(LoginRegisterFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(25, 25, 25, 25));
        initUI();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 12, 14, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(24, 12, 14, 12);
        JButton loginBtn = UIHelper.createBlueButton("Login to Dashboard");
        loginBtn.setPreferredSize(new Dimension(200, 42));
        loginBtn.addActionListener(e -> handleLogin());
        add(loginBtn, gbc);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            UIHelper.showError(this, "Please enter both email and password.");
            return;
        }

        try {
            User user = parentFrame.getUserService().authenticate(email, password);
            if (user == null) {
                UIHelper.showError(this, "Invalid email or password.");
                return;
            }

            Wallet wallet = parentFrame.getWalletService().getWalletByUserId(user.getUserId());
            user.setWallet(wallet);

            if (wallet == null) {
                wallet = UIHelper.promptCreateWallet(this, parentFrame, user);
                if (wallet == null) {
                    return; // User cancelled wallet creation
                }
            }

            passwordField.setText("");
            parentFrame.openMainFrame(user, wallet);

        } catch (Exception ex) {
            UIHelper.showError(this, "Login Database Error: " + ex.getMessage());
        }
    }
}
