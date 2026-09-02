package model;

import model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int transactionId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String description;

    public Transaction() {

    }

    public Transaction(int transactionId,
                       TransactionType transactionType,
                       BigDecimal amount,
                       String description) {


        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = LocalDateTime.now();
        this.description = description;

    }


    // Getters
    public int getTransactionId() {
        return transactionId;
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }


    public String getDescription() {
        return description;
    }


    // Setters
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }


    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void displayTransactionDetails() {


        System.out.println("\n========== TRANSACTION ==========");


        System.out.println(
                "Transaction ID : " + transactionId
        );


        System.out.println(
                "Type           : " + transactionType
        );


        System.out.println(
                "Amount         : ₹" + amount
        );


        System.out.println(
                "Date           : " + transactionDate
        );


        System.out.println(
                "Description    : " + description
        );


        System.out.println(
                "================================"
        );

    }

    @Override
    public String toString() {

        return "Transaction{" +
                "transactionId=" + transactionId +
                ", transactionType='" +
                transactionType + '\'' +
                ", amount=" + amount +
                ", transactionDate=" +
                transactionDate +
                ", description='" +
                description + '\'' +
                '}';

    }

}
