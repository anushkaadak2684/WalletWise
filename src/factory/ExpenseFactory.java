package factory;

import model.Expense;
import model.enums.ExpenseCategory;
import model.FixedExpense;
import model.enums.RecurringFrequency;
import model.VariableExpense;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseFactory {

    public static Expense createExpense(
            ExpenseCategory category,
            BigDecimal amount,
            LocalDate date,
            String description,
            String typeStr,
            RecurringFrequency frequency,
            BigDecimal maxExpectedAmount) {

        if ("FIXED".equalsIgnoreCase(typeStr)) {
            RecurringFrequency freq = (frequency != null) ? frequency : RecurringFrequency.MONTHLY;
            return new FixedExpense(0, category, amount, date, description, freq);
        } else {
            BigDecimal maxExp = (maxExpectedAmount != null) ? maxExpectedAmount : amount;
            return new VariableExpense(0, category, amount, date, description, maxExp);
        }
    }
}
