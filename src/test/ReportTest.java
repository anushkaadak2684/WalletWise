package test;

import model.*;
import model.enums.*;
import repository.ReportRepository;
import service.ReportService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportTest {
    public static void main(String[] args) {

        ReportRepository repository = new ReportRepository();
        ReportService service = new ReportService(repository);

        // Create Wallet
        PersonalWallet wallet =
                new PersonalWallet(
                        1,
                        new BigDecimal("50000"),
                        new BigDecimal("50000")
                );

        // Add Income Transaction
        Transaction income =
                new Transaction(
                        1,
                        TransactionType.DEPOSIT,
                        new BigDecimal("30000"),
                        "Salary"
                );

        wallet.addTransaction(income);

        // Add Expense
        Expense expense =
                new VariableExpense(
                        1,
                        ExpenseCategory.FOOD,
                        new BigDecimal("5000"),
                        LocalDate.now(),
                        "Dinner",
                        new BigDecimal("10000")
                );

        wallet.addExpense(expense);

        // Create Savings Goal
        SavingsGoal goal =
                new SavingsGoal(
                        1,
                        "Bike Purchase",
                        new BigDecimal("80000"),
                        LocalDate.of(2026,12,31)
                );

        goal.addSavings(new BigDecimal("20000"));

        // Generate Report
        Report report =
                service.generateReport(
                        wallet,
                        goal,
                        ReportType.MONTHLY
                );

        System.out.println("Report Created Successfully");
        System.out.println(
                "Report ID: " + report.getReportId()
        );

        // Display Report
        service.showReport(report);

        // Fetch from Database
        Report fetchedReport =
                service.getReportById(
                        report.getReportId()
                );

        System.out.println("\nFetched From Database:");
        service.showReport(fetchedReport);

        System.out.println("\nTesting completed");
    }
}
