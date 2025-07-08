package com.rewards.dto;

import lombok.*;
import java.util.List;
import com.rewards.model.MonthlyReward;
import com.rewards.model.Transaction;

/**
 * Data Transfer Object (DTO) representing the reward details for a customer.
 * This includes customer information, list of transactions within the reward
 * period,
 * a breakdown of monthly reward points, and the total points accumulated.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {
    /**
     * Name of the customer.
     */
    private String customerName;

    /**
     * Unique identifier of the customer.
     */
    private int customerId;

    /**
     * The list of transactions for the customer during the specified reward period.
     * Each transaction includes date, amount, and calculated reward points.
     */
    private List<Transaction> transactions;

    /**
     * A list containing reward points broken down by month.
     * Each entry represents the total reward points earned in a particular month.
     */
    private List<MonthlyReward> monthlyRewards;

    /**
     * The total number of reward points earned across all months and transactions
     * within the specified period.
     */
    private int totalPoints;

}