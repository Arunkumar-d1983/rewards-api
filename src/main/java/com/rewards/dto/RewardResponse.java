package com.rewards.dto;

import lombok.*;
import java.util.List;

/**
 * Response object representing the reward details for a customer.
 * Contains total reward points earned over the last 3 months,
 * and a breakdown of points earned each month.
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
     * List of monthly reward summaries over the past 3 months.
     */
    private List<MonthlyReward> monthlyRewards;

    /**
     * Total reward points accumulated across all months.
     */
    private int totalPoints;

    /**
     * Inner static class representing reward points for a specific year and month.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyReward {
        /**
         * The year for which the rewards were calculated.
         */
        private int year;

        /**
         * The month name (e.g., "July", "August").
         */
        private String month;

        /**
         * Total reward points earned in the specific month.
         */
        private int points;
    }
}