package service;

import model.Transaction;
import model.enums.TransactionType;
import model.User;
import model.Wallet;
import observer.WalletEventListener;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WalletService {

    private IWalletRepository walletRepository;
    private ITransactionRepository transactionRepository;
    private List<WalletEventListener> observers = new ArrayList<>();

    public WalletService(IWalletRepository walletRepository,
                          ITransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public void addObserver(WalletEventListener listener) {
        if (listener != null) {
            observers.add(listener);
        }
    }

    private void notifyTransactionCreated(User user, Transaction tx) {
        for (WalletEventListener listener : observers) {
            listener.onTransactionCreated(user, tx);
        }
    }

    private void notifySpendingLimitExceeded(User user, Wallet wallet, BigDecimal amount) {
        for (WalletEventListener listener : observers) {
            listener.onSpendingLimitExceeded(user, wallet, amount);
        }
    }

    // Create Wallet
    public void createWallet(Wallet wallet, int userId) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        walletRepository.save(wallet, userId);
    }

    // Deposit Money
    public void depositMoney(Wallet wallet, BigDecimal amount, String description) {
        depositMoney(null, wallet, amount, description);
    }

    public void depositMoney(User user, Wallet wallet, BigDecimal amount, String description) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        Transaction transaction = new Transaction(0, TransactionType.DEPOSIT, amount, description);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                wallet.deposit(amount);
                walletRepository.update(connection, wallet);
                transactionRepository.save(connection, transaction, wallet.getWalletId());
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Deposit transaction failed, rolled back: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during deposit: " + e.getMessage(), e);
        }

        notifyTransactionCreated(user, transaction);
    }

    // Withdraw Money
    public void withdrawMoney(Wallet wallet, BigDecimal amount, String description) {
        withdrawMoney(null, wallet, amount, description);
    }

    public void withdrawMoney(User user, Wallet wallet, BigDecimal amount, String description) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        Transaction transaction = new Transaction(0, TransactionType.WITHDRAWAL, amount, description);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                wallet.withdraw(amount);
                walletRepository.update(connection, wallet);
                transactionRepository.save(connection, transaction, wallet.getWalletId());
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Withdrawal transaction failed, rolled back: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during withdrawal: " + e.getMessage(), e);
        }

        notifyTransactionCreated(user, transaction);

        if (wallet.isLimitExceeded(amount)) {
            notifySpendingLimitExceeded(user, wallet, amount);
        }
    }

    public List<Transaction> getTransactionsByWallet(int walletId) {
        return transactionRepository.findByWalletId(walletId);
    }

    // Get Wallet
    public Wallet getWalletById(int walletId) {
        return walletRepository.findById(walletId);
    }

    // Get User Wallet
    public Wallet getWalletByUserId(int userId) {
        return walletRepository.findByUserId(userId);
    }

    // Update Wallet
    public void updateWallet(Wallet wallet) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        walletRepository.update(wallet);
    }

    // Delete Wallet
    public void deleteWallet(int walletId) {
        walletRepository.delete(walletId);
    }

    // Check Balance
    public BigDecimal checkBalance(Wallet wallet) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        return wallet.getBalance();
    }
}