package model;

import model.enums.ExpenseCategory;
import model.enums.RecurringFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FixedExpense extends Expense {
    private RecurringFrequency recurringFrequency;

    public FixedExpense() {
        super();
        this.recurringFrequency = RecurringFrequency.MONTHLY;
    }

    public FixedExpense(int expenseId,
                        ExpenseCategory category,
                        BigDecimal amount,
                        LocalDate date,
                        String description,
                        RecurringFrequency recurringFrequency) {

        super(expenseId, category, amount, date, description);
        this.recurringFrequency = recurringFrequency;
    }

    // Getter
    public RecurringFrequency getRecurringFrequency() {
        return recurringFrequency;
    }

    public void setRecurringFrequency(RecurringFrequency recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
    }

    @Override
    public String getExpenseType() {
        return "FIXED";
    }

    public void displayRecurringDetails() {
        System.out.println(
                "Recurring Frequency : "
                + recurringFrequency
        );
    }
    @Override
    public String toString() {
        return "FixedExpense{" +
                "recurringFrequency='" +
                recurringFrequency + '\'' +
                '}';
    }
}
