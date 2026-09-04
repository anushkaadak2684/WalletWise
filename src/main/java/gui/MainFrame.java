package gui;

import model.User;
import model.Wallet;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private User currentUser;
    private Wallet currentWallet;

    // Services
    private UserService userService;
    private WalletService walletService;
    private ExpenseService expenseService;
    private BudgetService budgetService;
    private SavingsGoalService savingsGoalService;
    private NotificationService notificationService;
    private RewardService rewardService;
    private ReportService reportService;

    // Header Labels
    private JLabel greetingLabel;
    private JLabel walletInfoLabel;
    private JLabel balanceLabel;

    // Panels
    private DashboardPanel dashboardPanel;
    private WalletPanel walletPanel;
    private TransactionPanel transactionPanel;
    private ExpensePanel expensePanel;
    private BudgetPanel budgetPanel;
    private SavingsPanel savingsPanel;
    private NotificationPanel notificationPanel;
    private RewardPanel rewardPanel;
    private ReportPanel reportPanel;

    public MainFrame(User currentUser, Wallet currentWallet,
                     UserService userService, WalletService walletService, ExpenseService expenseService,
                     BudgetService budgetService, SavingsGoalService savingsGoalService, NotificationService notificationService,
                     RewardService rewardService, ReportService reportService) {

        this.currentUser = currentUser;
        this.currentWallet = currentWallet;
        this.userService = userService;
        this.walletService = walletService;
        this.expenseService = expenseService;
        this.budgetService = budgetService;
        this.savingsGoalService = savingsGoalService;
        this.notificationService = notificationService;
        this.rewardService = rewardService;
        this.reportService = reportService;

        // Register Observers (Observer Pattern - Synchronous In-Process Notification)
        observer.NotificationObserver notifObs = new observer.NotificationObserver(notificationService);
        observer.RewardObserver rewardObs = new observer.RewardObserver(rewardService);
        this.walletService.addObserver(notifObs);
        this.expenseService.addObserver(notifObs);
        this.savingsGoalService.addObserver(notifObs);
        this.savingsGoalService.addObserver(rewardObs);

        setTitle("WalletWise — Digital Wallet & Personal Finance Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Maximize to entire screen viewport
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 720));

        initUI();
        refreshAllPanels();
    }

    private void initUI() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Theme.BG_DARK);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER),
                new EmptyBorder(16, 28, 16, 28)
        ));
        headerPanel.setBackground(Theme.HEADER_BG);

        JPanel headerInfo = new JPanel(new GridLayout(2, 1, 0, 4));
        headerInfo.setOpaque(false);

        greetingLabel = new JLabel("Welcome back!");
        greetingLabel.setFont(Theme.TITLE_FONT);
        greetingLabel.setForeground(Theme.TEXT_PRIMARY);

        walletInfoLabel = new JLabel("Wallet Info");
        walletInfoLabel.setFont(Theme.BODY_FONT);
        walletInfoLabel.setForeground(Theme.TEXT_MUTED);

        headerInfo.add(greetingLabel);
        headerInfo.add(walletInfoLabel);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        headerRight.setOpaque(false);

        balanceLabel = new JLabel("Balance: ₹0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        balanceLabel.setForeground(Theme.SUCCESS_COLOR);

        JButton refreshBtn = UIHelper.createBlueButton("⟳ Refresh");
        refreshBtn.addActionListener(e -> refreshAllPanels());

        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setFont(Theme.BODY_BOLD);
        logoutBtn.setBackground(new Color(38, 44, 54));
        logoutBtn.setForeground(Theme.TEXT_PRIMARY);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        logoutBtn.addActionListener(e -> handleLogout());

        headerRight.add(balanceLabel);
        headerRight.add(refreshBtn);
        headerRight.add(logoutBtn);

        headerPanel.add(headerInfo, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Feature Panels
        dashboardPanel = new DashboardPanel(this);
        walletPanel = new WalletPanel(this);
        transactionPanel = new TransactionPanel(this);
        expensePanel = new ExpensePanel(this);
        budgetPanel = new BudgetPanel(this);
        savingsPanel = new SavingsPanel(this);
        notificationPanel = new NotificationPanel(this);
        rewardPanel = new RewardPanel(this);
        reportPanel = new ReportPanel(this);

        // Tabbed Container
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.HEADER_FONT);
        tabbedPane.setBorder(new EmptyBorder(8, 14, 14, 14));

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Wallet", walletPanel);
        tabbedPane.addTab("Transactions", transactionPanel);
        tabbedPane.addTab("Expenses", expensePanel);
        tabbedPane.addTab("Budgets", budgetPanel);
        tabbedPane.addTab("Savings", savingsPanel);
        tabbedPane.addTab("Notifications", notificationPanel);
        tabbedPane.addTab("Rewards", rewardPanel);
        tabbedPane.addTab("Reports", reportPanel);

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer);
    }

    public void refreshAllPanels() {
        if (currentUser == null || currentWallet == null) return;

        try {
            // Refresh latest wallet state from DB via service
            Wallet updatedWallet = walletService.getWalletByUserId(currentUser.getUserId());
            if (updatedWallet != null) {
                this.currentWallet = updatedWallet;
            }

            // Update Header Labels
            greetingLabel.setText("Welcome back, " + currentUser.getFullName());
            walletInfoLabel.setText("Wallet #" + currentWallet.getWalletId() + " (" + currentWallet.getWalletType() + ")  •  " + currentUser.getEmail());
            balanceLabel.setText("₹" + currentWallet.getBalance().toPlainString());

            // Refresh all sub-panels
            dashboardPanel.refreshData();
            walletPanel.refreshData();
            transactionPanel.refreshData();
            expensePanel.refreshData();
            budgetPanel.refreshData();
            savingsPanel.refreshData();
            notificationPanel.refreshData();
            rewardPanel.refreshData();
            reportPanel.refreshData();

        } catch (Exception ex) {
            System.err.println("MainFrame refresh error: " + ex.getMessage());
        }
    }

    private void handleLogout() {
        LoginRegisterFrame loginFrame = new LoginRegisterFrame();
        loginFrame.setVisible(true);
        this.dispose();
    }

    public User getCurrentUser() { return currentUser; }
    public Wallet getCurrentWallet() { return currentWallet; }
    public UserService getUserService() { return userService; }
    public WalletService getWalletService() { return walletService; }
    public ExpenseService getExpenseService() { return expenseService; }
    public BudgetService getBudgetService() { return budgetService; }
    public SavingsGoalService getSavingsGoalService() { return savingsGoalService; }
    public NotificationService getNotificationService() { return notificationService; }
    public RewardService getRewardService() { return rewardService; }
    public ReportService getReportService() { return reportService; }
}
