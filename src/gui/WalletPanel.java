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
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initUI();
    }

    private void initUI() {
        // Wallet Specifications Header Card
        JPanel infoCard = new JPanel(new GridBagLayout());
        infoCard.setBorder(BorderFactory.createTitledBorder("Wallet Specifications & Limits"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        infoCard.add(new JLabel("Wallet ID:"), gbc);
        gbc.gridx = 1;
        walletIdValLabel = new JLabel("-");
        walletIdValLabel.setFont(Theme.BODY_BOLD);
        infoCard.add(walletIdValLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoCard.add(new JLabel("Wallet Type:"), gbc);
        gbc.gridx = 1;
        walletTypeValLabel = new JLabel("-");
        walletTypeValLabel.setFont(Theme.BODY_BOLD);
        infoCard.add(walletTypeValLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoCard.add(new JLabel("Current Balance:"), gbc);
        gbc.gridx = 1;
        balanceValLabel = new JLabel("-");
        balanceValLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        balanceValLabel.setForeground(Theme.SUCCESS_COLOR);
        infoCard.add(balanceValLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        limitTitleLabel = new JLabel("Configured Limit:");
        infoCard.add(limitTitleLabel, gbc);
        gbc.gridx = 1;
        limitValLabel = new JLabel("-");
        limitValLabel.setFont(Theme.BODY_BOLD);
        infoCard.add(limitValLabel, gbc);

        // Update Limit Form
        JPanel updateCard = new JPanel(new GridBagLayout());
        updateCard.setBorder(BorderFactory.createTitledBorder("Modify Configured Limit"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        updateCard.add(new JLabel("New Limit (₹):"), gbc);
        gbc.gridx = 1;
        newLimitField = new JTextField(12);
        updateCard.add(newLimitField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton updateLimitBtn = UIHelper.createBlueButton("Update Limit");
        updateLimitBtn.setPreferredSize(new Dimension(160, 36));
        updateLimitBtn.addActionListener(e -> handleUpdateLimit());
        updateCard.add(updateLimitBtn, gbc);

        JPanel topGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        topGrid.add(infoCard);
        topGrid.add(updateCard);

        add(topGrid, BorderLayout.NORTH);

        // Visual Graphs & Pie Chart Section
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 15, 0));

        pieChartPanel = new PieChartPanel();
        limitProgressPanel = new LimitProgressPanel();

        chartsContainer.add(pieChartPanel);
        chartsContainer.add(limitProgressPanel);

        add(chartsContainer, BorderLayout.CENTER);
    }

    public void refreshData() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null)
            return;

        walletIdValLabel.setText(String.valueOf(wallet.getWalletId()));
        walletTypeValLabel.setText(wallet.getWalletType().name());
        balanceValLabel.setText("₹" + wallet.getBalance().toPlainString());

        BigDecimal limitVal = BigDecimal.ZERO;
        if (wallet instanceof PersonalWallet) {
            limitTitleLabel.setText("Monthly Spending Limit:");
            limitVal = ((PersonalWallet) wallet).getMonthlySpendingLimit();
            limitValLabel.setText("₹" + limitVal.toPlainString());
        } else if (wallet instanceof BusinessWallet) {
            limitTitleLabel.setText("Business Transaction Limit:");
            limitVal = ((BusinessWallet) wallet).getBusinessTransactionLimit();
            limitValLabel.setText("₹" + limitVal.toPlainString());
        }

        // Fetch Expense Categories for Pie Chart
        List<Expense> expenses = mainFrame.getExpenseRepository().findByWalletId(wallet.getWalletId());
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
        if (wallet == null)
            return;

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
                new Color(52, 152, 219), new Color(46, 204, 113), new Color(231, 76, 60),
                new Color(241, 196, 15), new Color(155, 89, 182), new Color(230, 126, 34), new Color(149, 165, 166)
        };

        public PieChartPanel() {
            setBorder(BorderFactory.createTitledBorder("Expense Category Breakdown (Pie Chart)"));
            setBackground(Theme.CARD_BG);
        }

        public void setData(Map<ExpenseCategory, BigDecimal> data) {
            this.categoryData = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            if (categoryData == null || categoryData.isEmpty()) {
                g2d.setColor(Color.GRAY);
                g2d.setFont(Theme.BODY_FONT);
                g2d.drawString("No expense data recorded yet to render Pie Chart.", width / 4, height / 2);
                g2d.dispose();
                return;
            }

            double total = 0;
            for (BigDecimal val : categoryData.values()) {
                total += val.doubleValue();
            }

            if (total == 0) {
                g2d.setColor(Color.GRAY);
                g2d.drawString("Total expenses: ₹0.00", width / 3, height / 2);
                g2d.dispose();
                return;
            }

            int size = Math.min(width / 2, height - 60);
            int x = 20;
            int y = (height - size) / 2;

            int startAngle = 0;
            int colorIdx = 0;

            int legendX = x + size + 20;
            int legendY = y + 10;

            for (Map.Entry<ExpenseCategory, BigDecimal> entry : categoryData.entrySet()) {
                double val = entry.getValue().doubleValue();
                int angle = (int) Math.round((val / total) * 360);

                Color col = COLORS[colorIdx % COLORS.length];
                g2d.setColor(col);
                g2d.fillArc(x, y, size, size, startAngle, angle);

                // Draw Legend
                g2d.fillRect(legendX, legendY, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
                double pct = (val / total) * 100;
                g2d.drawString(String.format("%s: ₹%.2f (%.1f%%)", entry.getKey().name(), val, pct), legendX + 18,
                        legendY + 11);

                legendY += 22;
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
            setBorder(BorderFactory.createTitledBorder("Limit Utilization & Status Gauge"));
            setBackground(Theme.CARD_BG);
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

            g2d.setColor(Color.WHITE);
            g2d.setFont(Theme.HEADER_FONT);
            g2d.drawString("Wallet Utilization Metrics", 20, 35);

            double spent = totalSpent.doubleValue();
            double limit = limitVal.doubleValue();
            double pct = limit > 0 ? (spent / limit) * 100 : 0;

            // Bar background
            int barX = 20;
            int barY = 60;
            int barW = width - 40;
            int barH = 28;

            g2d.setColor(new Color(50, 50, 50));
            g2d.fillRoundRect(barX, barY, barW, barH, 10, 10);

            // Fill Bar
            int fillW = limit > 0 ? (int) Math.min(barW, (spent / limit) * barW) : 0;
            Color barColor = pct >= 100 ? Theme.DANGER_COLOR : (pct >= 75 ? Theme.WARNING_COLOR : Theme.SUCCESS_COLOR);
            g2d.setColor(barColor);
            g2d.fillRoundRect(barX, barY, fillW, barH, 10, 10);

            // Label text over bar
            g2d.setColor(Color.WHITE);
            g2d.setFont(Theme.BODY_BOLD);
            g2d.drawString(String.format("Spent: ₹%.2f / Limit: ₹%.2f (%.1f%%)", spent, limit, pct), barX + 15,
                    barY + 19);

            // Status Indicator
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            if (pct >= 100) {
                g2d.setColor(Theme.DANGER_COLOR);
                g2d.drawString("STATUS: LIMIT EXCEEDED", 20, 120);
            } else {
                g2d.setColor(Theme.SUCCESS_COLOR);
                g2d.drawString("STATUS: SAFE (WITHIN CONFIGURED LIMIT)", 20, 120);
            }

            g2d.dispose();
        }
    }
}
