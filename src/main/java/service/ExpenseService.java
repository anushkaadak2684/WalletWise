package service;

import model.Expense;
import model.Transaction;
import model.User;
import model.Wallet;
import model.enums.TransactionType;
import repository.interfaces.IExpenseRepository;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ExpenseService {

    private IExpenseRepository expenseRepository;
    private IWalletRepository walletRepository;
    private ITransactionRepository transactionRepository;

    public ExpenseService(IExpenseRepository expenseRepository,
                          IWalletRepository walletRepository,
                          ITransactionRepository transactionRepository) {

        this.expenseRepository = expenseRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    // Add Expense
    public void addExpense(Wallet wallet, Expense expense) {
        addExpense(null, wallet, expense);
    }

    public void addExpense(User user, Wallet wallet, Expense expense) {
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }

        BigDecimal amount = expense.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero");
        }

        Transaction transaction = new Transaction(
                0,
                TransactionType.EXPENSE,
                amount,
                expense.getDescription()
        );

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Deduct from wallet balance
                wallet.withdraw(amount);
                walletRepository.update(connection, wallet);

                // Save expense entity
                expenseRepository.save(connection, expense, wallet.getWalletId());

                // Save transaction record
                transactionRepository.save(connection, transaction, wallet.getWalletId());

                // Commit atomic transaction
                connection.commit();
            } catch (Exception ex) {
                connection.rollback();
                throw new RuntimeException("Expense transaction failed, rolled back: " + ex.getMessage(), ex);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error adding expense: " + e.getMessage(), e);
        }
    }

    public List<Expense> getExpensesByWallet(int walletId) {
        return expenseRepository.findByWalletId(walletId);
    }

    public void deleteExpense(int expenseId) {
        expenseRepository.delete(expenseId);
    }

    // Get Total Expense From Database
    public BigDecimal calculateTotalExpense(int walletId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense expense : expenseRepository.findByWalletId(walletId)) {
            total = total.add(expense.getAmount());
        }
        return total;
    }

    // Get Expense History
    public void showExpenses(int walletId) {
        var expenses = expenseRepository.findByWalletId(walletId);
        if (expenses.isEmpty()) {
            System.out.println("No expenses found");
            return;
        }
        for (Expense expense : expenses) {
            expense.displayExpenseDetails();
        }
    }
}