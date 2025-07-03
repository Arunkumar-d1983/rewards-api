package com.rewards.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Represents a customer's transaction used for calculating reward points.
 * Each transaction includes an ID, date, amount, and the reward points earned.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    /**
     * Unique identifier for the transaction.
     * Must not be null.
     */
    @NotNull(message = "Transaction ID must be present.")
    private Integer transactionId;

    /**
     * Date of the transaction.
     * Must be today or a past date.
     */
    @NotNull(message = "Transaction date is required.")
    @PastOrPresent(message = "Transaction date cannot be in the future.")
    private LocalDate transactionDate;

    /**
     * Amount spent in the transaction.
     * Must be at least 1.0.
     */
    @Min(value = 1, message = "Transaction amount must be greater than zero.")
    private double amount;

    /**
     * Points earned for this transaction based on reward calculation.
     * This is usually set internally and not provided in the request.
     */
    private int points;
}