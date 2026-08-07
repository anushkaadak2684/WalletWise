package service;

import model.Budget;
import model.enums.ExpenseCategory;
import repository.interfaces.IBudgetRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class BudgetService {
    private IBudgetRepository budgetRepository;

    public BudgetService(IBudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    // Create Budget
    public Budget createBudget(
            int walletId,
            ExpenseCategory category,
            BigDecimal limitAmount,
            LocalDate startDate,
            LocalDate endDate) {


        if(category == null) {
            throw new IllegalArgumentException(
                    "Category cannot be null"
            );
        }

        if(limitAmount == null || limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Budget limit must be positive"
            );
        }

        Budget budget =
                new Budget(
                        0,
                        category,
                        limitAmount,
                        startDate,
                        endDate
                );

        budgetRepository.save(budget, walletId);
        return budget;
    }

    // Get Budget By ID
    public Budget getBudgetById(int budgetId) {
        return budgetRepository.findById(budgetId);
    }

    // Get Wallet Budgets
    public List<Budget> getBudgetsByWallet(int walletId) {
        return budgetRepository.findByWalletId(walletId);
    }

    // Update Budget after Expense
    public void updateBudget(Budget budget, BigDecimal expenseAmount) {
        if(budget == null) {
            throw new IllegalArgumentException(
                    "Budget cannot be null"
            );
        }
        budget.addExpenseAmount(expenseAmount);
        budgetRepository.update(budget);
    }

    public boolean isBudgetExceeded(Budget budget) {
        return budget.isBudgetExceeded();
    }

    public double getBudgetUsagePercentage(Budget budget) {
        return budget.getUsagePercentage();
    }

    public void showBudgetStatus(Budget budget) {
        budget.displayBudgetDetails();
    }
}