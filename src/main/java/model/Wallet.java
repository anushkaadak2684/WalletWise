package model;

import model.enums.WalletType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class Wallet {

    private int walletId;
    private BigDecimal balance;
    private WalletType walletType;

    // Composition (HAS-A relationship)
    private List<Transaction> transactions;
    private List<Expense> expenses;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
        this.transactions = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public Wallet(int walletId, BigDecimal balance, WalletType walletType) {
        this.walletId = walletId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.walletType = walletType;
        this.transactions = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    // Getters & Setters
    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    // Encapsulation with Defensive Copying
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    // Polymorphic Abstract Methods
    public abstract BigDecimal calculateTransactionLimit();
    public abstract boolean isLimitExceeded(BigDecimal newAmount);
    public abstract String getLimitWarningMessage();

    // Domain Business Operations
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }
        balance = balance.subtract(amount);
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        transactions.add(transaction);
    }

    public void addExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }
        expenses.add(expense);
    }

    public void displayWalletDetails() {
        System.out.println("\n========== WALLET DETAILS ==========");
        System.out.println("Wallet ID     : " + walletId);
        System.out.println("Wallet Type   : " + walletType);
        System.out.println("Balance       : ₹" + balance);
        System.out.println("Transactions  : " + transactions.size());
        System.out.println("Expenses      : " + expenses.size());
        System.out.println("====================================");
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", balance=" + balance +
                ", walletType=" + walletType +
                '}';
    }
}
