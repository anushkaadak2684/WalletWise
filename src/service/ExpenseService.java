package service;

import model.*;
import model.enums.*;
import repository.interfaces.IExpenseRepository;
import repository.interfaces.ITransactionRepository;
import repository.interfaces.IWalletRepository;

import java.math.BigDecimal;


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

        if(wallet == null) {
            throw new IllegalArgumentException(
                    "Wallet cannot be null"
            );
        }

        if(expense == null) {
            throw new IllegalArgumentException(
                    "Expense cannot be null"
            );
        }

        BigDecimal amount = expense.getAmount();

        wallet.withdraw(amount);

        walletRepository.update(wallet);

        expenseRepository.save(
                expense,
                wallet.getWalletId()
        );

        // Create transaction entry
        Transaction transaction =
                new Transaction(
                        0,
                        TransactionType.EXPENSE,
                        amount,
                        expense.getDescription()
                );

        // Save transaction
        transactionRepository.save(
                transaction,
                wallet.getWalletId()
        );
    }

    // Get Total Expense From Database
    public BigDecimal calculateTotalExpense(int walletId) {
        BigDecimal total = BigDecimal.ZERO;

        for(Expense expense : expenseRepository.findByWalletId(walletId)) {
            total = total.add(expense.getAmount());
        }
        return total;
    }

    // Get Expense History
    public void showExpenses(int walletId) {
        var expenses = expenseRepository.findByWalletId(walletId);

        if(expenses.isEmpty()) {
            System.out.println(
                    "No expenses found"
            );
            return;
        }

        for(Expense expense : expenses) {
            expense.displayExpenseDetails();
        }
    }
}