package gui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Theme {
    // Deep Modern Dark Palette (Obsidian / Slate / Indigo)
    public static final Color BG_DARK = new Color(13, 17, 23);         // #0D1117 Deep Navy Canvas
    public static final Color CARD_BG = new Color(22, 27, 34);         // #161B22 Surface Card
    public static final Color CARD_BG_HOVER = new Color(30, 36, 46);   // #1E242E
    public static final Color CARD_BORDER = new Color(48, 54, 61);     // #30363D Subtle Border
    public static final Color HEADER_BG = new Color(18, 22, 30);       // #12161E Topbar

    // Accent Colors
    public static final Color PRIMARY_BLUE = new Color(99, 102, 241);   // #6366F1 Modern Indigo
    public static final Color PRIMARY_ACCENT = new Color(129, 140, 248);// #818CF8 Indigo Hover
    public static final Color CYAN_ACCENT = new Color(56, 189, 248);   // #38BDF8 Neon Cyan
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129); // #10B981 Emerald Green
    public static final Color DANGER_COLOR = new Color(239, 68, 68);    // #EF4444 Rose Red
    public static final Color WARNING_COLOR = new Color(245, 158, 11);  // #F59E0B Amber
    public static final Color PURPLE_ACCENT = new Color(168, 85, 247); // #A855F7 Purple

    // Text Tokens
    public static final Color TEXT_PRIMARY = new Color(240, 246, 252); // Crisp White
    public static final Color TEXT_MUTED = new Color(139, 148, 158);   // Soft Grey

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font METRIC_VAL_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public static void setupTheme() {
        try {
            FlatDarkLaf.setup();
            
            // Global Geometry
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 8);
            UIManager.put("ScrollBar.width", 10);

            // Canvas & Panels
            UIManager.put("Panel.background", BG_DARK);
            UIManager.put("RootPane.background", BG_DARK);
            UIManager.put("ScrollPane.background", BG_DARK);

            // Inputs
            UIManager.put("TextField.background", new Color(18, 22, 30));
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.caretColor", CYAN_ACCENT);
            UIManager.put("ComboBox.background", new Color(18, 22, 30));
            UIManager.put("ComboBox.foreground", TEXT_PRIMARY);

            // Buttons
            UIManager.put("Button.background", PRIMARY_BLUE);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.focusedBackground", PRIMARY_ACCENT);
            UIManager.put("Button.hoverBackground", PRIMARY_ACCENT);
            UIManager.put("Button.focusWidth", 0);

            // Tables
            UIManager.put("Table.background", CARD_BG);
            UIManager.put("Table.foreground", TEXT_PRIMARY);
            UIManager.put("Table.selectionBackground", new Color(40, 50, 75));
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.gridColor", new Color(38, 44, 54));
            UIManager.put("Table.rowHeight", 34);
            UIManager.put("TableHeader.background", new Color(18, 22, 30));
            UIManager.put("TableHeader.foreground", TEXT_MUTED);
            UIManager.put("TableHeader.font", HEADER_FONT);

            // Tabs
            UIManager.put("TabbedPane.background", BG_DARK);
            UIManager.put("TabbedPane.foreground", TEXT_MUTED);
            UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
            UIManager.put("TabbedPane.selectedBackground", CARD_BG);
            UIManager.put("TabbedPane.underlineColor", PRIMARY_BLUE);
            UIManager.put("TabbedPane.tabInsets", new Insets(10, 24, 10, 24));
            UIManager.put("TabbedPane.font", HEADER_FONT);

        } catch (Throwable t) {
            System.err.println("FlatDarkLaf initialization notice: " + t.getMessage());
        }
    }
}
