package gui;

import model.User;
import model.Wallet;
import repository.*;
import service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginRegisterFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private UserService userService;
    private WalletService walletService;
    private ExpenseService expenseService;
    private BudgetService budgetService;
    private SavingsGoalService savingsGoalService;
    private NotificationService notificationService;
    private RewardService rewardService;
    private ReportService reportService;

    public LoginRegisterFrame() {
        setTitle("WalletWise — Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 700);
        setMinimumSize(new Dimension(500, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);

        initBackendServices();
        initUI();
    }

    private void initBackendServices() {
        UserRepository userRepository = new UserRepository();
        WalletRepository walletRepository = new WalletRepository();
        TransactionRepository transactionRepository = new TransactionRepository();
        ExpenseRepository expenseRepository = new ExpenseRepository();
        BudgetRepository budgetRepository = new BudgetRepository();
        SavingsGoalRepository savingsGoalRepository = new SavingsGoalRepository();
        NotificationRepository notificationRepository = new NotificationRepository();
        RewardRepository rewardRepository = new RewardRepository();
        ReportRepository reportRepository = new ReportRepository();

        userService = new UserService(userRepository);
        walletService = new WalletService(walletRepository, transactionRepository);
        expenseService = new ExpenseService(expenseRepository, walletRepository, transactionRepository);
        budgetService = new BudgetService(budgetRepository);
        savingsGoalService = new SavingsGoalService(savingsGoalRepository, walletRepository, transactionRepository);
        notificationService = new NotificationService(notificationRepository);
        rewardService = new RewardService(rewardRepository);
        reportService = new ReportService(reportRepository);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(Theme.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(30, 36, 30, 36));

        // Header Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 8));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(8, 8, 20, 8));

        JLabel titleLabel = new JLabel("WALLETWISE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Theme.PRIMARY_ACCENT);

        JLabel subLabel = new JLabel("Personal Finance & Digital Wallet Tracker", SwingConstants.CENTER);
        subLabel.setFont(Theme.BODY_FONT);
        subLabel.setForeground(Theme.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(subLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Card Container for Tabs
        JPanel cardContainer = new JPanel(new BorderLayout());
        cardContainer.setBackground(Theme.CARD_BG);
        cardContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.HEADER_FONT);

        LoginPanel loginPanel = new LoginPanel(this);
        RegisterPanel registerPanel = new RegisterPanel(this);

        tabbedPane.addTab("Sign In", loginPanel);
        tabbedPane.addTab("Create Account", registerPanel);

        cardContainer.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(cardContainer, BorderLayout.CENTER);

        add(mainPanel);
    }

    public void openMainFrame(User user, Wallet wallet) {
        if (user == null || wallet == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(
                    user,
                    wallet,
                    userService,
                    walletService,
                    expenseService,
                    budgetService,
                    savingsGoalService,
                    notificationService,
                    rewardService,
                    reportService
            );
            mainFrame.setVisible(true);
            this.setVisible(false);
            this.dispose();
        });
    }

    public UserService getUserService() { return userService; }
    public WalletService getWalletService() { return walletService; }
    public ExpenseService getExpenseService() { return expenseService; }
    public BudgetService getBudgetService() { return budgetService; }
    public SavingsGoalService getSavingsGoalService() { return savingsGoalService; }
    public NotificationService getNotificationService() { return notificationService; }
    public RewardService getRewardService() { return rewardService; }
    public ReportService getReportService() { return reportService; }
}
