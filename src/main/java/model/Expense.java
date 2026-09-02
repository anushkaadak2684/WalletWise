package model;

import model.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Expense {
    private int expenseId;
    private ExpenseCategory category;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    public Expense() {
    }

    public Expense(int expenseId,
                   ExpenseCategory category,
                   BigDecimal amount,
                   LocalDate date,
                   String description) {

        this.expenseId = expenseId;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters
    public int getExpenseId() {
        return expenseId;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract String getExpenseType();

    public void displayExpenseDetails() {

        System.out.println("\n========== EXPENSE DETAILS ==========");

        System.out.println("Expense ID  : " + expenseId);
        System.out.println("Category    : " + category);
        System.out.println("Amount      : ₹" + amount);
        System.out.println("Date        : " + date);
        System.out.println("Description : " + description);
        System.out.println("Type        : " + getExpenseType());

        System.out.println("=====================================");
    }
    @Override
    public String toString() {

        return "Expense{" +
                "expenseId=" + expenseId +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}