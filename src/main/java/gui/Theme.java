package gui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Theme {
    public static final Color PRIMARY_BLUE = new Color(41, 128, 185);
    public static final Color PRIMARY_ACCENT = new Color(52, 152, 219);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color DANGER_COLOR = new Color(231, 76, 60);
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    public static final Color BG_DARK = new Color(24, 24, 24);
    public static final Color CARD_BG = new Color(34, 34, 34);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 13);

    public static void setupTheme() {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);

            // Configure solid Blue buttons globally
            UIManager.put("Button.background", PRIMARY_BLUE);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.focusedBackground", PRIMARY_ACCENT);
            UIManager.put("Button.hoverBackground", PRIMARY_ACCENT);
            UIManager.put("Button.default.background", PRIMARY_BLUE);
            UIManager.put("Button.default.foreground", Color.WHITE);
            UIManager.put("Button.focusWidth", 0);
        } catch (Throwable t) {
            System.err.println("FlatDarkLaf initialization notice: " + t.getMessage());
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
