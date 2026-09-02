package model;

import model.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Budget {
    private int budgetId;
    private ExpenseCategory category;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private LocalDate startDate;
    private LocalDate endDate;

    public Budget() {
        this.spentAmount = BigDecimal.ZERO;
    }

    public Budget(int budgetId,
                  ExpenseCategory category,
                  BigDecimal limitAmount,
                  LocalDate startDate,
                  LocalDate endDate) {


        this.budgetId = budgetId;
        this.category = category;
        this.limitAmount = limitAmount;
        this.spentAmount = BigDecimal.ZERO;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //Getters
    public int getBudgetId() {
        return budgetId;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }


    // Setters
    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }


    public void addExpenseAmount(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException(
                    "Expense amount must be greater than zero"
            );
        }

        spentAmount = spentAmount.add(amount);
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return limitAmount.subtract(spentAmount);

    }

    public boolean isBudgetExceeded() {
        return spentAmount.compareTo(limitAmount) > 0;

    }

    public double getUsagePercentage() {
        if(limitAmount.compareTo(BigDecimal.ZERO)==0){
            return 0;
        }

        BigDecimal percentage =
        spentAmount
        .multiply(new BigDecimal("100"))
        .divide(limitAmount, 2, RoundingMode.HALF_UP);

        return percentage.doubleValue();

    }

    public void displayBudgetDetails(){

        System.out.println("\n========== BUDGET DETAILS ==========");

        System.out.println(
                "Category : " + category
        );

        System.out.println(
                "Limit    : ₹" + limitAmount
        );

        System.out.println(
                "Spent    : ₹" + spentAmount
        );

        System.out.println(
                "Remaining: ₹" + getRemainingAmount()
        );

        System.out.println(
                "Usage    : " + getUsagePercentage() + "%"
        );

        System.out.println(
                "Exceeded : " + isBudgetExceeded()
        );

        System.out.println(
                "==================================="
        );
    }

    @Override
    public String toString(){

        return "Budget{" +
                "budgetId=" + budgetId +
                ", category='" + category + '\'' +
                ", limitAmount=" + limitAmount +
                ", spentAmount=" + spentAmount +
                '}';
    }

}
