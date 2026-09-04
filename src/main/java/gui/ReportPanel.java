package gui;

import model.Budget;
import model.Expense;
import model.Report;
import model.SavingsGoal;
import model.Transaction;
import model.Wallet;
import model.enums.ReportType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame;

    private JComboBox<ReportType> reportTypeCombo;
    private JEditorPane reportDetailPane;

    private DefaultTableModel tableModel;
    private JTable table;

    public ReportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 18));
        setBackground(Theme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initUI();
    }

    private void initUI() {
        // Top Form Card
        JPanel formCard = UIHelper.createCardPanel("Generate Periodical Financial Report");
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel typeLbl = new JLabel("Report Period / Scope:");
        typeLbl.setForeground(Theme.TEXT_MUTED);
        formGrid.add(typeLbl, gbc);

        gbc.gridx = 1;
        reportTypeCombo = new JComboBox<>(ReportType.values());
        formGrid.add(reportTypeCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        JButton genBtn = UIHelper.createBlueButton("Generate Detailed Report");
        genBtn.addActionListener(e -> handleGenerateReport());
        formGrid.add(genBtn, gbc);

        formCard.add(formGrid, BorderLayout.CENTER);

        // Center Split Pane: Left = History Table, Right = Detailed Report HTML Viewer
        tableModel = new DefaultTableModel(new Object[]{"ID", "Report Type", "Generated Date", "Total Income", "Total Expense", "Total Savings"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedReportDetails();
            }
        });

        JPanel historyCard = UIHelper.createCardPanel("Historical Generated Reports");
        historyCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel historyBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        historyBtnPanel.setOpaque(false);
        JButton viewDetailsBtn = UIHelper.createBlueButton("View Full Breakdown");
        viewDetailsBtn.addActionListener(e -> handleViewSelectedReportDetails());
        historyBtnPanel.add(viewDetailsBtn);
        historyCard.add(historyBtnPanel, BorderLayout.SOUTH);

        // Right Detail Viewer Card
        JPanel detailCard = UIHelper.createCardPanel("Itemized Report Breakdown");
        reportDetailPane = new JEditorPane();
        reportDetailPane.setContentType("text/html");
        reportDetailPane.setEditable(false);
        reportDetailPane.setBackground(new Color(18, 22, 30));
        reportDetailPane.setText("<html><body style='font-family: sans-serif; color: #8B949E; text-align: center; padding-top: 60px;'>"
                + "<h3>Select a report from history or click 'Generate Detailed Report' above to view full itemized breakdown.</h3>"
                + "</body></html>");

        JScrollPane detailScrollPane = new JScrollPane(reportDetailPane);
        detailScrollPane.setPreferredSize(new Dimension(500, 380));
        detailCard.add(detailScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyCard, detailCard);
        splitPane.setResizeWeight(0.5);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        JPanel mainContent = new JPanel(new BorderLayout(0, 14));
        mainContent.setOpaque(false);
        mainContent.add(formCard, BorderLayout.NORTH);
        mainContent.add(splitPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    public void refreshData() {
        Wallet wallet = mainFrame.getCurrentWallet();
        if (wallet == null) return;

        try {
            int currentSelectedRow = table.getSelectedRow();
            int previousSelectedId = currentSelectedRow != -1 ? (int) table.getValueAt(currentSelectedRow, 0) : -1;

            List<Report> reports = mainFrame.getReportService().getReportsByWallet(wallet.getWalletId());
            tableModel.setRowCount(0);
            int restoreRowIndex = -1;

            for (int i = 0; i < reports.size(); i++) {
                Report rep = reports.get(i);
                tableModel.addRow(new Object[]{
                        rep.getReportId(),
                        rep.getReportType(),
                        rep.getGeneratedDate().toString(),
                        "₹" + rep.getTotalIncome().toPlainString(),
                        "₹" + rep.getTotalExpense().toPlainString(),
                        "₹" + rep.getTotalSavings().toPlainString()
                });
                if (rep.getReportId() == previousSelectedId) {
                    restoreRowIndex = i;
                }
            }

            if (restoreRowIndex != -1) {
                table.setRowSelectionInterval(restoreRowIndex, restoreRowIndex);
                updateSelectedReportDetails();
            } else if (!reports.isEmpty()) {
                table.setRowSelectionInterval(0, 0);
                updateSelectedReportDetails();
            } else {
                reportDetailPane.setText("<html><body style='font-family: sans-serif; color: #8B949E; text-align: center; padding-top: 60px;'>"
                        + "<h3>No reports found. Click 'Generate Detailed Report' above to create one.</h3>"
                        + "</body></html>");
            }
        } catch (Exception ex) {
            System.err.println("Report refresh error: " + ex.getMessage());
        }
    }

    private void handleGenerateReport() {
        Wallet wallet = mainFrame.getCurrentWallet();
        ReportType type = (ReportType) reportTypeCombo.getSelectedItem();

        if (wallet == null) {
            UIHelper.showWarning(this, "No active wallet found.");
            return;
        }

        try {
            List<Transaction> txs = mainFrame.getWalletService().getTransactionsByWallet(wallet.getWalletId());
            List<Expense> exps = mainFrame.getExpenseService().getExpensesByWallet(wallet.getWalletId());
            List<Budget> budgets = mainFrame.getBudgetService().getBudgetsByWallet(wallet.getWalletId());
            List<SavingsGoal> goals = mainFrame.getSavingsGoalService().getGoalsByWallet(wallet.getWalletId());

            wallet.getTransactions().clear();
            wallet.getExpenses().clear();

            for (Transaction t : txs) {
                wallet.addTransaction(t);
            }
            for (Expense e : exps) {
                wallet.addExpense(e);
            }

            Report report = mainFrame.getReportService().generateDetailedReport(wallet, budgets, goals, type);
            if (report.getSummaryDetails() != null) {
                reportDetailPane.setText(report.getSummaryDetails());
                reportDetailPane.setCaretPosition(0);
            }

            UIHelper.showSuccess(this, "Detailed financial report generated successfully!");
            mainFrame.refreshAllPanels();

        } catch (Exception ex) {
            ex.printStackTrace();
            UIHelper.showError(this, "Report Generation Error: " + ex.getMessage());
        }
    }

    private void handleViewSelectedReportDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select a report from the table first.");
            return;
        }
        updateSelectedReportDetails();
    }

    private void updateSelectedReportDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int reportId = (int) table.getValueAt(selectedRow, 0);
        try {
            Report report = mainFrame.getReportService().getReportById(reportId);
            if (report != null) {
                String htmlDetails = report.getOrGenerateSummaryDetails();
                reportDetailPane.setText(htmlDetails);
                reportDetailPane.setCaretPosition(0);
            }
        } catch (Exception ex) {
            System.err.println("Error rendering report details: " + ex.getMessage());
        }
    }
}
