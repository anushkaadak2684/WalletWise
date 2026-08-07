package model;

import model.enums.ReportType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Report {

    private int reportId;
    private int walletId;
    private ReportType reportType;
    private LocalDate generatedDate;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalSavings;
    private String summaryDetails;

    public Report() {

        this.totalIncome = BigDecimal.ZERO;
        this.totalExpense = BigDecimal.ZERO;
        this.totalSavings = BigDecimal.ZERO;

    }

    public Report(int reportId,
                  ReportType reportType) {


        this.reportId = reportId;
        this.reportType = reportType;
        this.generatedDate = LocalDate.now();
        this.totalIncome = BigDecimal.ZERO;
        this.totalExpense = BigDecimal.ZERO;
        this.totalSavings = BigDecimal.ZERO;

    }

    public Report(int reportId,
                  int walletId,
                  ReportType reportType) {

        this.reportId = reportId;
        this.walletId = walletId;
        this.reportType = reportType;
        this.generatedDate = LocalDate.now();
        this.totalIncome = BigDecimal.ZERO;
        this.totalExpense = BigDecimal.ZERO;
        this.totalSavings = BigDecimal.ZERO;

    }


    // Getters
    public int getReportId() {
        return reportId;
    }

    public int getWalletId() {
        return walletId;
    }

    public String getSummaryDetails() {
        return summaryDetails;
    }


    public ReportType getReportType() {
        return reportType;
    }


    public LocalDate getGeneratedDate() {
        return generatedDate;
    }


    public BigDecimal getTotalIncome() {
        return totalIncome;
    }


    public BigDecimal getTotalExpense() {
        return totalExpense;
    }


    public BigDecimal getTotalSavings() {
        return totalSavings;
    }

    // Setters
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public void setSummaryDetails(String summaryDetails) {
        this.summaryDetails = summaryDetails;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public void setTotalSavings(BigDecimal totalSavings) {
        this.totalSavings = totalSavings;
    }


    public void addIncome(BigDecimal amount) {

        if(amount.compareTo(BigDecimal.ZERO) <= 0){

            throw new IllegalArgumentException(
                    "Income must be greater than zero"
            );
        }

        totalIncome = totalIncome.add(amount);

    }


    public void addExpense(BigDecimal amount) {

        if(amount.compareTo(BigDecimal.ZERO) <= 0){

            throw new IllegalArgumentException(
                    "Expense must be greater than zero"
            );
        }

        totalExpense = totalExpense.add(amount);

    }


    public void addSavings(BigDecimal amount) {

        if(amount.compareTo(BigDecimal.ZERO) <= 0){

            throw new IllegalArgumentException(
                    "Savings must be greater than zero"
            );
        }

        totalSavings = totalSavings.add(amount);

    }

    public BigDecimal calculateBalance() {
        return totalIncome.subtract(totalExpense).subtract(totalSavings);
    }



    public void displayReport(){


        System.out.println("\n========== FINANCIAL REPORT ==========");


        System.out.println(
                "Report Type : " + reportType
        );


        System.out.println(
                "Generated   : " + generatedDate
        );


        System.out.println(
                "Income      : ₹" + totalIncome
        );


        System.out.println(
                "Expense     : ₹" + totalExpense
        );


        System.out.println(
                "Savings     : ₹" + totalSavings
        );


        System.out.println(
                "Balance     : ₹" + calculateBalance()
        );


        System.out.println(
                "======================================"
        );

    }

    public String getOrGenerateSummaryDetails() {
        if (summaryDetails != null && !summaryDetails.trim().isEmpty()) {
            return summaryDetails;
        }

        BigDecimal netBalance = calculateBalance();
        String balanceColor = netBalance.compareTo(BigDecimal.ZERO) >= 0 ? "#2ecc71" : "#e74c3c";

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: sans-serif; padding: 15px; color: #ffffff;'>");
        sb.append("<h2 style='color: #3498db; text-align: center; margin-bottom: 5px;'>")
          .append(reportType != null ? reportType : "FINANCIAL").append(" Financial Report</h2>");
        sb.append("<p style='text-align: center; color: #bdc3c7; margin-top: 0;'>Generated Date: ")
          .append(generatedDate != null ? generatedDate : LocalDate.now()).append(" | Report ID: #").append(reportId).append("</p>");
        sb.append("<hr style='border: 1px solid #444444; margin-bottom: 15px;'/>");

        sb.append("<div style='background-color: #2c3e50; padding: 15px; border-radius: 6px;'>");
        sb.append("<h3 style='color: #f39c12; margin-top: 0;'>📊 Summary Breakdown</h3>");
        sb.append("<table style='width: 100%; border-collapse: collapse; color: #ffffff; font-size: 14px;'>");
        sb.append("<tr><td style='padding: 6px 0;'><b>Total Income (Deposits):</b></td><td style='color: #2ecc71; text-align: right;'>₹")
          .append(totalIncome != null ? totalIncome.toPlainString() : "0.00").append("</td></tr>");
        sb.append("<tr><td style='padding: 6px 0;'><b>Total Expenses:</b></td><td style='color: #e74c3c; text-align: right;'>₹")
          .append(totalExpense != null ? totalExpense.toPlainString() : "0.00").append("</td></tr>");
        sb.append("<tr><td style='padding: 6px 0;'><b>Total Savings Allocated:</b></td><td style='color: #f1c40f; text-align: right;'>₹")
          .append(totalSavings != null ? totalSavings.toPlainString() : "0.00").append("</td></tr>");
        sb.append("<tr><td style='padding: 10px 0 4px 0; border-top: 1px solid #555555;'><b>Net Surplus / Balance:</b></td><td style='color: ")
          .append(balanceColor).append("; text-align: right; padding: 10px 0 4px 0; border-top: 1px solid #555555; font-weight: bold;'>₹")
          .append(netBalance.toPlainString()).append("</td></tr>");
        sb.append("</table></div></div>");

        return sb.toString();
    }

    @Override
    public String toString(){

        return "Report{" +
                "reportId=" + reportId +
                ", reportType='" +
                reportType + '\'' +
                ", totalIncome=" +
                totalIncome +
                ", totalExpense=" +
                totalExpense +
                ", totalSavings=" +
                totalSavings +
                '}';

    }

}
