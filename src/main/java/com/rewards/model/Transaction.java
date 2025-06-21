package com.rewards.model;

import java.time.LocalDate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public class Transaction {

    @NotNull(message = "Transaction ID must be positive.")
    private int transactionId;

    @NotNull(message = "Transaction date is required.")
    @PastOrPresent(message = "Transaction date cannot be in the future.")
    private LocalDate transactionDate;

    @Min(value = 1, message = "Transaction amount must be greater than zero.")
    private double amount;

    private int points;

    public Transaction() {
    }

    public Transaction(int transactionId, LocalDate date, double amount) {
        this.transactionId = transactionId;
        this.transactionDate = date;
        this.amount = amount;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate date) {
        this.transactionDate = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Transaction [transactionId=" + transactionId + ", transactionDate=" + transactionDate + ", amount="
                + amount
                + ", points="
                + points + "]";
    }

}
