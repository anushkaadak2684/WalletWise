package test;

import model.Budget;
import model.enums.ExpenseCategory;
import repository.BudgetRepository;
import service.BudgetService;

import java.math.BigDecimal;
import java.time.LocalDate;


public class BudgetTest {
    public static void main(String[] args) {

        BudgetRepository budgetRepository = new BudgetRepository();
        BudgetService budgetService = new BudgetService(budgetRepository);


        int walletId = 1;
        Budget budget =
                budgetService.createBudget(
                        walletId,
                        ExpenseCategory.FOOD,
                        new BigDecimal("5000"),
                        LocalDate.now(),
                        LocalDate.now().plusMonths(1)
                );

        System.out.println(
                "Budget created successfully"
        );

        System.out.println(
                "Budget ID: "
                + budget.getBudgetId()
        );

        Budget fetchedBudget =
                budgetService.getBudgetById(
                        budget.getBudgetId()
                );


        fetchedBudget.displayBudgetDetails();

        budgetService.updateBudget(
                fetchedBudget,
                new BigDecimal("1200")
        );

        System.out.println(
                "After expense update:"
        );


        fetchedBudget.displayBudgetDetails();

        System.out.println(
                "Testing completed"
        );

    }
}
