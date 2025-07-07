package com.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response object representing class representing reward points for a specific
 * year and month.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReward {
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
