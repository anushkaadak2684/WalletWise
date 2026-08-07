package service;

import model.Transaction;
import model.enums.TransactionType;
import model.User;
import model.Wallet;
import observer.WalletEventListener;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;

import java.math.BigDecimal;
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

    // Create Wallet
    public void createWallet(Wallet wallet, int userId) {
        if(wallet == null) {
            throw new IllegalArgumentException(
                    "Wallet cannot be null"
            );
        }
        walletRepository.save(wallet, userId);
    }

    // Deposit Money
    public void depositMoney(Wallet wallet,
                             BigDecimal amount,
                             String description) {
        depositMoney(null, wallet, amount, description);
    }

    public void depositMoney(User user,
                             Wallet wallet,
                             BigDecimal amount,
                             String description) {

        if(wallet == null) {
            throw new IllegalArgumentException(
                    "Wallet cannot be null"
            );
        }

        wallet.deposit(amount);

        // Update wallet balance in database
        walletRepository.update(wallet);
        Transaction transaction =
                new Transaction(
                        0,
                        TransactionType.DEPOSIT,
                        amount,
                        description
                );

        // Save transaction in database
        transactionRepository.save(
                transaction,
                wallet.getWalletId()
        );

        notifyTransactionCreated(user, transaction);
    }

    // Withdraw Money
    public void withdrawMoney(Wallet wallet,
                               BigDecimal amount,
                               String description) {
        withdrawMoney(null, wallet, amount, description);
    }

    public void withdrawMoney(User user,
                               Wallet wallet,
                               BigDecimal amount,
                               String description) {

        if(wallet == null) {
            throw new IllegalArgumentException(
                    "Wallet cannot be null"
            );
        }
        wallet.withdraw(amount);

        // Update wallet balance
        walletRepository.update(wallet);

        Transaction transaction =
                new Transaction(
                        0,
                        TransactionType.WITHDRAWAL,
                        amount,
                        description
                );

        // Save transaction
        transactionRepository.save(
                transaction,
                wallet.getWalletId()
        );

        notifyTransactionCreated(user, transaction);
    }

    // Get Wallet
    public Wallet getWalletById(int walletId) {
        try {
            return walletRepository.findById(walletId);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching wallet by id: " + ex.getMessage(), ex);
        }
    }

    // Get User Wallet
    public Wallet getWalletByUserId(int userId) {
        try {
            return walletRepository.findByUserId(userId);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching wallet by user id: " + ex.getMessage(), ex);
        }
    }

    // Update Wallet
    public void updateWallet(Wallet wallet) {
        if(wallet == null) {
            throw new IllegalArgumentException(
                    "Wallet cannot be null"
            );
        }
        try {
            walletRepository.update(wallet);
        } catch (Exception ex) {
            throw new RuntimeException("Error updating wallet: " + ex.getMessage(), ex);
        }
    }

    // Delete Wallet
    public void deleteWallet(int walletId) {
        try {
            walletRepository.delete(walletId);
        } catch (Exception ex) {
            throw new RuntimeException("Error deleting wallet: " + ex.getMessage(), ex);
        }
    }

    // Check Balance
    public BigDecimal checkBalance(Wallet wallet) {
        if(wallet == null) {
            throw new IllegalArgumentException(
                    "Wallet cannot be null"
            );
        }
        return wallet.getBalance();
    }
}