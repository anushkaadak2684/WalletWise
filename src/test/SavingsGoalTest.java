package test;

import model.SavingsGoal;
import repository.SavingsGoalRepository;
import service.SavingsGoalService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsGoalTest {

    public static void main(String[] args) {

        SavingsGoalRepository repository = new SavingsGoalRepository();
        SavingsGoalService service = new SavingsGoalService(repository);

        // Create Goal
        SavingsGoal goal =
                service.createSavingsGoal(
                        1,
                        "Buy Laptop",
                        new BigDecimal("80000"),
                        LocalDate.of(2026,12,31)
                );

        System.out.println("Savings Goal Created");
        System.out.println("Goal ID: " + goal.getGoalId());

        // Add Savings
        service.addSavings(
                goal,
                new BigDecimal("20000")
        );

        System.out.println("\nAfter adding savings:");

        service.showSavingsGoal(goal);

        // Fetch from database
        SavingsGoal fetchedGoal =
                service.getGoalById(
                        goal.getGoalId()
                );


        System.out.println("\nFetched from Database:");

        service.showSavingsGoal(fetchedGoal);

        System.out.println("\nTesting completed");
    }
}