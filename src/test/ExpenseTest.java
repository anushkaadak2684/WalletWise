package test;

import model.*;
import model.enums.*;
import repository.*;
import service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDate;


public class ExpenseTest {
    public static void main(String[] args) {

        // Repositories
        ExpenseRepository expenseRepository = new ExpenseRepository();
        WalletRepository walletRepository = new WalletRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        // Service
        ExpenseService expenseService =
                new ExpenseService(
                        expenseRepository,
                        walletRepository,
                        transactionRepository
                );

        // Use existing wallet id from database
        int walletId = 1;

        // Fetch wallet from DB
        Wallet wallet = walletRepository.findById(walletId);

        if(wallet == null) {
            System.out.println(
                    "Wallet not found"
            );
            return;
        }

        System.out.println(
                "Initial Balance: "
                + wallet.getBalance()
        );

        // Create Fixed Expense
        Expense expense =
                new FixedExpense(
                        0,
                        ExpenseCategory.FOOD,
                        new BigDecimal("1000"),
                        LocalDate.now(),
                        "Dinner",
                        RecurringFrequency.MONTHLY
                );

        // Add expense
        expenseService.addExpense(
                wallet,
                expense
        );

        System.out.println(
                "Expense added"
        );

        // Total expense
        System.out.println(
                "Total Expense: " + expenseService.calculateTotalExpense(walletId)
        );

        // Show expenses
        expenseService.showExpenses(walletId);

        System.out.println(
                "Expense testing completed"
        );
    }
}
