package gui;

import model.User;
import model.Wallet;
import repository.*;
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

    // Repositories
    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private ExpenseRepository expenseRepository;
    private BudgetRepository budgetRepository;
    private SavingsGoalRepository savingsGoalRepository;
    private NotificationRepository notificationRepository;
    private RewardRepository rewardRepository;
    private ReportRepository reportRepository;

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
                     RewardService rewardService, ReportService reportService, UserRepository userRepository,
                     WalletRepository walletRepository, TransactionRepository transactionRepository, ExpenseRepository expenseRepository,
                     BudgetRepository budgetRepository, SavingsGoalRepository savingsGoalRepository, NotificationRepository notificationRepository,
                     RewardRepository rewardRepository, ReportRepository reportRepository) {

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
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.notificationRepository = notificationRepository;
        this.rewardRepository = rewardRepository;
        this.reportRepository = reportRepository;

        // Register Observers (Observer Pattern)
        observer.NotificationObserver notifObs = new observer.NotificationObserver(notificationService);
        observer.RewardObserver rewardObs = new observer.RewardObserver(rewardService);
        this.walletService.addObserver(notifObs);
        this.savingsGoalService.addObserver(notifObs);
        this.savingsGoalService.addObserver(rewardObs);

        setTitle("Digital Wallet & Finance Tracker - Main Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 780);
        setMinimumSize(new Dimension(980, 680));
        setLocationRelativeTo(null);

        initUI();
        refreshAllPanels();
    }

    private void initUI() {
        JPanel mainContainer = new JPanel(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setBackground(new Color(25, 25, 25));

        JPanel headerInfo = new JPanel(new GridLayout(2, 1));
        headerInfo.setOpaque(false);

        greetingLabel = new JLabel("Welcome back!");
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        greetingLabel.setForeground(Color.WHITE);

        walletInfoLabel = new JLabel("Wallet Info");
        walletInfoLabel.setFont(Theme.BODY_FONT);
        walletInfoLabel.setForeground(Color.LIGHT_GRAY);

        headerInfo.add(greetingLabel);
        headerInfo.add(walletInfoLabel);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerRight.setOpaque(false);

        balanceLabel = new JLabel("Balance: ₹0.00");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        balanceLabel.setForeground(Theme.SUCCESS_COLOR);

        JButton refreshBtn = UIHelper.createBlueButton("Refresh State");
        refreshBtn.addActionListener(e -> refreshAllPanels());

        JButton logoutBtn = UIHelper.createBlueButton("Logout");
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
            // Refresh latest wallet state from DB
            Wallet updatedWallet = walletService.getWalletByUserId(currentUser.getUserId());
            if (updatedWallet != null) {
                this.currentWallet = updatedWallet;
            }

            // Update Header Labels
            greetingLabel.setText("Welcome back, " + currentUser.getFullName() + " (" + currentUser.getEmail() + ")");
            walletInfoLabel.setText("Wallet ID: " + currentWallet.getWalletId() + " | Type: " + currentWallet.getWalletType());
            balanceLabel.setText("Balance: ₹" + currentWallet.getBalance().toPlainString());

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

    public UserRepository getUserRepository() { return userRepository; }
    public WalletRepository getWalletRepository() { return walletRepository; }
    public TransactionRepository getTransactionRepository() { return transactionRepository; }
    public ExpenseRepository getExpenseRepository() { return expenseRepository; }
    public BudgetRepository getBudgetRepository() { return budgetRepository; }
    public SavingsGoalRepository getSavingsGoalRepository() { return savingsGoalRepository; }
    public NotificationRepository getNotificationRepository() { return notificationRepository; }
    public RewardRepository getRewardRepository() { return rewardRepository; }
    public ReportRepository getReportRepository() { return reportRepository; }
}
