package model;

import model.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VariableExpense extends Expense {
    private BigDecimal maximumExpectedAmount;

    public VariableExpense() {
        super();
        this.maximumExpectedAmount = BigDecimal.ZERO;
    }

    public VariableExpense(int expenseId,
                           ExpenseCategory category,
                           BigDecimal amount,
                           LocalDate date,
                           String description,
                           BigDecimal maximumExpectedAmount) {

        super(expenseId, category, amount, date, description);
        this.maximumExpectedAmount = maximumExpectedAmount;
    }

    // Getter
    public BigDecimal getMaximumExpectedAmount() {
        return maximumExpectedAmount;
    }


    // Setter
    public void setMaximumExpectedAmount(BigDecimal maximumExpectedAmount) {
        this.maximumExpectedAmount = maximumExpectedAmount;
    }
    @Override
    public String getExpenseType() {
        return "VARIABLE";
    }

    public boolean exceedsExpectedLimit() {
        return getAmount()
                .compareTo(maximumExpectedAmount) > 0;
    }


    public void displaySpendingAnalysis() {
        System.out.println(
                "Maximum Expected Amount : ₹"
                + maximumExpectedAmount
        );
        if (exceedsExpectedLimit()) {
            System.out.println(
                    "Warning: Expense exceeded expected limit!"
            );

        } else {
            System.out.println(
                    "Expense is within expected limit."
            );
        }
    }
    @Override
    public String toString() {
        return "VariableExpense{" +
                "maximumExpectedAmount=" +
                maximumExpectedAmount +
                '}';
    }
}
