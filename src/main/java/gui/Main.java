package gui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.setupTheme();
            LoginRegisterFrame frame = new LoginRegisterFrame();
            frame.setVisible(true);
        });
    }
}
