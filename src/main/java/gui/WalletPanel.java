package gui;

import model.BusinessWallet;
import model.Expense;
import model.PersonalWallet;
import model.Wallet;
import model.enums.ExpenseCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;

    private JLabel walletIdValLabel;
    private JLabel walletTypeValLabel;
    private JLabel balanceValLabel;
    private JLabel limitTitleLabel;
    private JLabel limitValLabel;

    private JTextField newLimitField;

    private PieChartPanel pieChartPanel;
    private LimitProgressPanel limitProgressPanel;

    public WalletPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 18));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initUI();
    }

    private void initUI() {
        JPanel topGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        topGrid.setOpaque(false);

        // Wallet Specifications Header Card
        JPanel infoCard = UIHelper.createCardPanel("Wallet Specifications & Limits");
        JPanel infoGrid = new JPanel(new GridBagLayout());
        infoGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel wIdLbl = new JLabel("Wallet ID:");
        wIdLbl.setForeground(Theme.TEXT_MUTED);
        infoGrid.add(wIdLbl, gbc);
        gbc.gridx = 1;
        walletIdValLabel = new JLabel("-");
        walletIdValLabel.setFont(Theme.BODY_BOLD);
        walletIdValLabel.setForeground(Theme.TEXT_PRIMARY);
        infoGrid.add(walletIdValLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel wTypeLbl = new JLabel("Wallet Type:");
        wTypeLbl.setForeground(Theme.TEXT_MUTED);
        infoGrid.add(wTypeLbl, gbc);
        gbc.gridx = 1;
        walletTypeValLabel = new JLabel("-");
        walletTypeValLabel.setFont(Theme.BODY_BOLD);
        walletTypeValLabel.setForeground(Theme.CYAN_ACCENT);
        infoGrid.add(walletTypeValLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel cBalLbl = new JLabel("Current Balance:");
        cBalLbl.setForeground(Theme.TEXT_MUTED);
        infoGrid.add(cBalLbl, gbc);
        gbc.gridx = 1;
        balanceValLabel = new JLabel("-");
        balanceValLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceValLabel.setForeground(Theme.SUCCESS_COLOR);
        infoGrid.add(balanceValLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        limitTitleLabel = new JLabel("Configured Limit:");
        limitTitleLabel.setForeground(Theme.TEXT_MUTED);
        infoGrid.add(limitTitleLabel, gbc);
        gbc.gridx = 1;
        limitValLabel = new JLabel("-");
        limitValLabel.setFont(Theme.BODY_BOLD);
        limitValLabel.setForeground(Theme.TEXT_PRIMARY);
        infoGrid.add(limitValLabel, gbc);

        infoCard.add(infoGrid, BorderLayout.CENTER);

        // Update Limit Form Card
        JPanel updateCard = UIHelper.createCardPanel("Modify Spending Limit");
        JPanel updateGrid = new JPanel(new GridBagLayout());
        updateGrid.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nLimitLbl = new JLabel("New Limit (₹):");
        nLimitLbl.setForeground(Theme.TEXT_MUTED);
        updateGrid.add(nLimitLbl, gbc);
        gbc.gridx = 1;
        newLimitField = new JTextField(12);
        updateGrid.add(newLimitField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton updateLimitBtn = UIHelper.createBlueButton("Update Limit");
        updateLimitBtn.addActionListener(e -> handleUpdateLimit());
        updateGrid.add(updateLimitBtn, gbc);

        updateCard.add(updateGrid, BorderLayout.CENTER);

        topGrid.add(infoCard);
        topGrid.add(updateCard);

        add(topGrid, BorderLayout.NORTH);

        // Visual Graphs & Pie Chart Section
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 16, 0));
        chartsContainer.setOpaque(false);

        pieChartPanel = new PieChartPanel();
        limitProgressPanel = new LimitProgressPanel();

        chartsContainer.add(pieChartPanel);
        chartsContainer.add(limitProgressPanel);

        add(chartsContainer, BorderLayout.CENTER);
    }

    public void refreshData() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        walletIdValLabel.setText(String.valueOf(wallet.getWalletId()));
        walletTypeValLabel.setText(wallet.getWalletType().name());
        balanceValLabel.setText("₹" + wallet.getBalance().toPlainString());

        BigDecimal limitVal = wallet.calculateTransactionLimit();
        limitTitleLabel.setText(wallet.getWalletType() == model.enums.WalletType.PERSONAL ? "Monthly Spending Limit:" : "Business Transaction Limit:");
        limitValLabel.setText("₹" + limitVal.toPlainString());

        // Fetch Expense Categories for Pie Chart via ExpenseService
        List<Expense> expenses = mainFrame.getExpenseService().getExpensesByWallet(wallet.getWalletId());
        Map<ExpenseCategory, BigDecimal> categoryTotals = new HashMap<>();
        BigDecimal grandTotalExpenses = BigDecimal.ZERO;

        for (Expense e : expenses) {
            categoryTotals.put(e.getCategory(),
                    categoryTotals.getOrDefault(e.getCategory(), BigDecimal.ZERO).add(e.getAmount()));
            grandTotalExpenses = grandTotalExpenses.add(e.getAmount());
        }

        pieChartPanel.setData(categoryTotals);
        limitProgressPanel.setData(grandTotalExpenses, limitVal);
    }

    private void handleUpdateLimit() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        try {
            BigDecimal newLimit = new BigDecimal(newLimitField.getText().trim());
            if (newLimit.compareTo(BigDecimal.ZERO) <= 0) {
                UIHelper.showError(this, "Limit must be greater than zero.");
                return;
            }

            if (wallet instanceof PersonalWallet) {
                ((PersonalWallet) wallet).setMonthlySpendingLimit(newLimit);
            } else if (wallet instanceof BusinessWallet) {
                ((BusinessWallet) wallet).setBusinessTransactionLimit(newLimit);
            }

            mainFrame.getWalletService().updateWallet(wallet);
            UIHelper.showSuccess(this, "Wallet limit updated successfully!");
            newLimitField.setText("");
            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            UIHelper.showError(this, "Limit Update Error: " + ex.getMessage());
        }
    }

    // Custom Graphical Component: Category Expense Pie Chart
    private static class PieChartPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private Map<ExpenseCategory, BigDecimal> categoryData = new HashMap<>();
        private static final Color[] COLORS = {
                new Color(99, 102, 241),  // Indigo
                new Color(56, 189, 248),  // Cyan
                new Color(16, 185, 129),  // Emerald
                new Color(245, 158, 11),  // Amber
                new Color(239, 68, 68),   // Rose
                new Color(168, 85, 247),  // Purple
                new Color(236, 72, 153)   // Pink
        };

        public PieChartPanel() {
            setBackground(Theme.CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                    new EmptyBorder(16, 18, 16, 18)
            ));
        }

        public void setData(Map<ExpenseCategory, BigDecimal> data) {
            this.categoryData = data != null ? data : new HashMap<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            g2d.setColor(Theme.TEXT_PRIMARY);
            g2d.setFont(Theme.HEADER_FONT);
            g2d.drawString("Expense Breakdown by Category", 16, 26);

            if (categoryData == null || categoryData.isEmpty()) {
                g2d.setColor(Theme.TEXT_MUTED);
                g2d.setFont(Theme.BODY_FONT);
                g2d.drawString("No expense data recorded yet.", width / 3, height / 2);
                g2d.dispose();
                return;
            }

            double total = 0;
            for (BigDecimal val : categoryData.values()) {
                total += val.doubleValue();
            }

            if (total == 0) {
                g2d.setColor(Theme.TEXT_MUTED);
                g2d.drawString("Total expenses: ₹0.00", width / 3, height / 2);
                g2d.dispose();
                return;
            }

            int size = Math.min(width / 2, height - 70);
            int x = 20;
            int y = 50 + (height - 50 - size) / 2;

            int startAngle = 0;
            int colorIdx = 0;

            int legendX = x + size + 24;
            int legendY = 60;

            for (Map.Entry<ExpenseCategory, BigDecimal> entry : categoryData.entrySet()) {
                double val = entry.getValue().doubleValue();
                int angle = (int) Math.round((val / total) * 360);

                Color col = COLORS[colorIdx % COLORS.length];
                g2d.setColor(col);
                g2d.fillArc(x, y, size, size, startAngle, angle);

                // Draw Legend
                g2d.fillRoundRect(legendX, legendY, 12, 12, 4, 4);
                g2d.setColor(Theme.TEXT_PRIMARY);
                g2d.setFont(Theme.BODY_FONT);
                double pct = (val / total) * 100;
                g2d.drawString(String.format("%s: ₹%.2f (%.1f%%)", entry.getKey().name(), val, pct), legendX + 20, legendY + 11);

                legendY += 24;
                startAngle += angle;
                colorIdx++;
            }

            g2d.dispose();
        }
    }

    // Custom Graphical Component: Limit Progress Gauge Chart
    private static class LimitProgressPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private BigDecimal totalSpent = BigDecimal.ZERO;
        private BigDecimal limitVal = BigDecimal.ZERO;

        public LimitProgressPanel() {
            setBackground(Theme.CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                    new EmptyBorder(16, 18, 16, 18)
            ));
        }

        public void setData(BigDecimal spent, BigDecimal limit) {
            this.totalSpent = spent != null ? spent : BigDecimal.ZERO;
            this.limitVal = limit != null ? limit : BigDecimal.ZERO;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();

            g2d.setColor(Theme.TEXT_PRIMARY);
            g2d.setFont(Theme.HEADER_FONT);
            g2d.drawString("Spending Limit & Utilization Status", 16, 26);

            double spent = totalSpent.doubleValue();
            double limit = limitVal.doubleValue();
            double pct = limit > 0 ? (spent / limit) * 100 : 0;

            // Bar background
            int barX = 16;
            int barY = 65;
            int barW = width - 32;
            int barH = 32;

            g2d.setColor(new Color(30, 36, 46));
            g2d.fillRoundRect(barX, barY, barW, barH, 12, 12);

            // Fill Bar
            int fillW = limit > 0 ? (int) Math.min(barW, (spent / limit) * barW) : 0;
            Color barColor = pct >= 100 ? Theme.DANGER_COLOR : (pct >= 80 ? Theme.WARNING_COLOR : Theme.SUCCESS_COLOR);
            g2d.setColor(barColor);
            g2d.fillRoundRect(barX, barY, fillW, barH, 12, 12);

            // Label text over bar
            g2d.setColor(Color.WHITE);
            g2d.setFont(Theme.BODY_BOLD);
            g2d.drawString(String.format("Spent: ₹%.2f / Limit: ₹%.2f (%.1f%%)", spent, limit, pct), barX + 16, barY + 21);

            // Status Indicator Badge
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            if (pct >= 100) {
                g2d.setColor(Theme.DANGER_COLOR);
                g2d.drawString("● STATUS: LIMIT EXCEEDED", 16, 135);
            } else if (pct >= 80) {
                g2d.setColor(Theme.WARNING_COLOR);
                g2d.drawString("● STATUS: NEAR LIMIT (80%+ USED)", 16, 135);
            } else {
                g2d.setColor(Theme.SUCCESS_COLOR);
                g2d.drawString("● STATUS: SAFE (WITHIN CONFIGURED LIMIT)", 16, 135);
            }

            g2d.dispose();
        }
    }
}
