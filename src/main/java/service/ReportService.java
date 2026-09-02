package service;

import model.*;
import model.enums.*;
import repository.interfaces.IReportRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private IReportRepository reportRepository;

    public ReportService(IReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report generateReport(
            Wallet wallet,
            ReportType reportType) {
        return generateDetailedReport(wallet, null, null, reportType);
    }

    public Report generateReport(
            Wallet wallet,
            SavingsGoal savingsGoal,
            ReportType reportType) {
        List<SavingsGoal> goals = savingsGoal != null ? List.of(savingsGoal) : null;
        return generateDetailedReport(wallet, null, goals, reportType);
    }

    public Report generateDetailedReport(
            Wallet wallet,
            List<Budget> budgets,
            List<SavingsGoal> goals,
            ReportType reportType) {

        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }

        Report report = new Report(0, wallet.getWalletId(), reportType);

        // 1. Calculate Income & Process Transactions
        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        List<Transaction> txList = wallet.getTransactions();

        for (Transaction transaction : txList) {
            if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
                totalDeposits = totalDeposits.add(transaction.getAmount());
                report.addIncome(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
                totalWithdrawals = totalWithdrawals.add(transaction.getAmount());
            }
        }

        // 2. Calculate Expenses & Category Breakdown
        List<Expense> expList = wallet.getExpenses();
        Map<ExpenseCategory, BigDecimal> categoryTotals = new HashMap<>();
        for (Expense expense : expList) {
            report.addExpense(expense.getAmount());
            categoryTotals.put(expense.getCategory(),
                    categoryTotals.getOrDefault(expense.getCategory(), BigDecimal.ZERO).add(expense.getAmount()));
        }

        // 3. Calculate Savings
        BigDecimal totalSavings = BigDecimal.ZERO;
        if (goals != null) {
            for (SavingsGoal goal : goals) {
                if (goal.getSavedAmount().compareTo(BigDecimal.ZERO) > 0) {
                    totalSavings = totalSavings.add(goal.getSavedAmount());
                }
            }
        }
        if (totalSavings.compareTo(BigDecimal.ZERO) > 0) {
            report.addSavings(totalSavings);
        }

        // 4. Construct Comprehensive HTML Summary Details
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: sans-serif; padding: 15px; color: #ffffff;'>");
        sb.append("<h2 style='color: #3498db; text-align: center; margin-bottom: 5px;'>Comprehensive ").append(reportType).append(" Financial Report</h2>");
        sb.append("<p style='text-align: center; color: #bdc3c7; margin-top: 0;'>Generated Date: ").append(report.getGeneratedDate()).append(" | Wallet ID: #").append(wallet.getWalletId()).append("</p>");
        sb.append("<hr style='border: 1px solid #444444; margin-bottom: 15px;'/>");

        // Financial Overview Section
        BigDecimal balance = report.calculateBalance();
        String balanceColor = balance.compareTo(BigDecimal.ZERO) >= 0 ? "#2ecc71" : "#e74c3c";
        sb.append("<div style='background-color: #2c3e50; padding: 12px; border-radius: 6px; margin-bottom: 15px;'>");
        sb.append("<h3 style='color: #f39c12; margin-top: 0;'>📊 Financial Overview</h3>");
        sb.append("<table style='width: 100%; border-collapse: collapse; color: #ffffff;'>");
        sb.append("<tr><td><b>Total Income (Deposits):</b></td><td style='color: #2ecc71; text-align: right;'>₹").append(report.getTotalIncome().toPlainString()).append("</td></tr>");
        sb.append("<tr><td><b>Total Expenses:</b></td><td style='color: #e74c3c; text-align: right;'>₹").append(report.getTotalExpense().toPlainString()).append("</td></tr>");
        sb.append("<tr><td><b>Total Savings Allocated:</b></td><td style='color: #f1c40f; text-align: right;'>₹").append(report.getTotalSavings().toPlainString()).append("</td></tr>");
        sb.append("<tr><td style='padding-top: 6px;'><b>Net Surplus / Balance:</b></td><td style='color: ").append(balanceColor).append("; text-align: right; padding-top: 6px; font-weight: bold;'>₹").append(balance.toPlainString()).append("</td></tr>");
        sb.append("</table></div>");

        // Transactions Breakdown Section
        sb.append("<div style='background-color: #2c3e50; padding: 12px; border-radius: 6px; margin-bottom: 15px;'>");
        sb.append("<h3 style='color: #3498db; margin-top: 0;'>💳 Transactions Breakdown (Total: ").append(txList.size()).append(")</h3>");
        if (txList.isEmpty()) {
            sb.append("<p style='color: #95a5a6;'>No transactions recorded for this period.</p>");
        } else {
            sb.append("<table style='width: 100%; border-collapse: collapse; color: #ffffff; font-size: 13px;' border='1' cellpadding='5' cellspacing='0'>");
            sb.append("<tr style='background-color: #34495e;'><th>Type</th><th>Amount</th><th>Date</th><th>Description</th></tr>");
            for (Transaction tx : txList) {
                String color = tx.getTransactionType() == TransactionType.DEPOSIT ? "#2ecc71" : "#e74c3c";
                sb.append("<tr>")
                  .append("<td style='color: ").append(color).append("; font-weight: bold;'>").append(tx.getTransactionType()).append("</td>")
                  .append("<td style='color: ").append(color).append(";'>₹").append(tx.getAmount().toPlainString()).append("</td>")
                  .append("<td>").append(tx.getTransactionDate()).append("</td>")
                  .append("<td>").append(tx.getDescription() != null ? tx.getDescription() : "-").append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");
        }
        sb.append("</div>");

        // Expense Breakdown Section
        sb.append("<div style='background-color: #2c3e50; padding: 12px; border-radius: 6px; margin-bottom: 15px;'>");
        sb.append("<h3 style='color: #e74c3c; margin-top: 0;'>💸 Expense & Category Breakdown (Total: ").append(expList.size()).append(")</h3>");
        if (expList.isEmpty()) {
            sb.append("<p style='color: #95a5a6;'>No expenses recorded for this period.</p>");
        } else {
            sb.append("<h4 style='color: #f1c40f;'>Category Totals:</h4>");
            sb.append("<ul>");
            for (Map.Entry<ExpenseCategory, BigDecimal> entry : categoryTotals.entrySet()) {
                sb.append("<li><b>").append(entry.getKey()).append(":</b> ₹").append(entry.getValue().toPlainString()).append("</li>");
            }
            sb.append("</ul>");

            sb.append("<h4 style='color: #f1c40f;'>Itemized Expense List:</h4>");
            sb.append("<table style='width: 100%; border-collapse: collapse; color: #ffffff; font-size: 13px;' border='1' cellpadding='5' cellspacing='0'>");
            sb.append("<tr style='background-color: #34495e;'><th>Category</th><th>Amount</th><th>Type</th><th>Description</th></tr>");
            for (Expense exp : expList) {
                sb.append("<tr>")
                  .append("<td>").append(exp.getCategory()).append("</td>")
                  .append("<td style='color: #e74c3c;'>₹").append(exp.getAmount().toPlainString()).append("</td>")
                  .append("<td>").append(exp.getExpenseType()).append("</td>")
                  .append("<td>").append(exp.getDescription() != null ? exp.getDescription() : "-").append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");
        }
        sb.append("</div>");

        // Savings Goals Section
        sb.append("<div style='background-color: #2c3e50; padding: 12px; border-radius: 6px; margin-bottom: 15px;'>");
        sb.append("<h3 style='color: #f1c40f; margin-top: 0;'>🎯 Savings Goals Status</h3>");
        if (goals == null || goals.isEmpty()) {
            sb.append("<p style='color: #95a5a6;'>No savings goals configured.</p>");
        } else {
            sb.append("<table style='width: 100%; border-collapse: collapse; color: #ffffff; font-size: 13px;' border='1' cellpadding='5' cellspacing='0'>");
            sb.append("<tr style='background-color: #34495e;'><th>Goal Name</th><th>Target</th><th>Saved</th><th>Progress %</th><th>Status</th></tr>");
            for (SavingsGoal goal : goals) {
                boolean completed = goal.getSavedAmount().compareTo(goal.getTargetAmount()) >= 0;
                double pct = goal.getCompletionPercentage();
                String statusColor = completed ? "#2ecc71" : "#e67e22";
                sb.append("<tr>")
                  .append("<td>").append(goal.getGoalName()).append("</td>")
                  .append("<td>₹").append(goal.getTargetAmount().toPlainString()).append("</td>")
                  .append("<td>₹").append(goal.getSavedAmount().toPlainString()).append("</td>")
                  .append("<td>").append(String.format("%.1f%%", pct)).append("</td>")
                  .append("<td style='color: ").append(statusColor).append("; font-weight: bold;'>").append(completed ? "COMPLETED" : "IN PROGRESS").append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");
        }
        sb.append("</div>");

        // Budget Performance Section
        sb.append("<div style='background-color: #2c3e50; padding: 12px; border-radius: 6px;'>");
        sb.append("<h3 style='color: #1abc9c; margin-top: 0;'>📈 Budget Performance & Limits</h3>");
        if (budgets == null || budgets.isEmpty()) {
            sb.append("<p style='color: #95a5a6;'>No category budgets configured.</p>");
        } else {
            sb.append("<table style='width: 100%; border-collapse: collapse; color: #ffffff; font-size: 13px;' border='1' cellpadding='5' cellspacing='0'>");
            sb.append("<tr style='background-color: #34495e;'><th>Category</th><th>Limit</th><th>Spent</th><th>Status</th></tr>");
            for (Budget b : budgets) {
                boolean exceeded = b.getSpentAmount().compareTo(b.getLimitAmount()) > 0;
                String statusColor = exceeded ? "#e74c3c" : "#2ecc71";
                sb.append("<tr>")
                  .append("<td>").append(b.getCategory()).append("</td>")
                  .append("<td>₹").append(b.getLimitAmount().toPlainString()).append("</td>")
                  .append("<td>₹").append(b.getSpentAmount().toPlainString()).append("</td>")
                  .append("<td style='color: ").append(statusColor).append("; font-weight: bold;'>").append(exceeded ? "EXCEEDED" : "ON TRACK").append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");
        }
        sb.append("</div>");
        sb.append("</div>");

        report.setSummaryDetails(sb.toString());

        try {
            reportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Error saving report", e);
        }
        return report;
    }

    public Report getReportById(int reportId) {
        return reportRepository.findById(reportId);
    }

    public List<Report> getReportsByWallet(int walletId) {
        return reportRepository.findByWalletId(walletId);
    }

    public void showReport(Report report) {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }
        report.displayReport();
    }
}