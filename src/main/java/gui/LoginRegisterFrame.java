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
        setTitle("Digital Wallet - Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 680);
        setMinimumSize(new Dimension(480, 580));
        setLocationRelativeTo(null);

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
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 6));
        titlePanel.setBorder(new EmptyBorder(10, 10, 25, 10));

        JLabel titleLabel = new JLabel("DIGITAL WALLET & FINANCE TRACKER", SwingConstants.CENTER);
        titleLabel.setFont(Theme.TITLE_FONT);

        JLabel subLabel = new JLabel("Secure Authentication & Management", SwingConstants.CENTER);
        subLabel.setFont(Theme.BODY_FONT);
        subLabel.setForeground(Color.GRAY);

        titlePanel.add(titleLabel);
        titlePanel.add(subLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.HEADER_FONT);

        LoginPanel loginPanel = new LoginPanel(this);
        RegisterPanel registerPanel = new RegisterPanel(this);

        tabbedPane.addTab("Login", loginPanel);
        tabbedPane.addTab("Register", registerPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
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
