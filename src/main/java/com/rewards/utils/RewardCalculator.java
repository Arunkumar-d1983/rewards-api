package com.rewards.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to calculate reward points based on purchase amount.
 *
 * Reward calculation rules:
 * No rewards for amounts less than or equal to $50.</li>
 * 1 point for every dollar spent over $50 and up to $100.</li>
 * 2 points for every dollar spent over $100.</li>
 */
@Slf4j
public class RewardCalculator {

    /**
     * Calculates reward points based on the transaction amount.
     *
     * @param amount The transaction amount.
     * @return The calculated reward points.
     */
    public static int calculatePoints(double amount) {
        int points = 0;
        log.debug("Calculating reward points for transaction amount: ${}", amount);
        if (amount > 100) {
            points += (int) ((amount - 100) * 2);
            log.debug("Added {} points for amount over $100", (int) ((amount - 100) * 2));
            points += 50;
            log.debug("Added 50 points for amount between $50 and $100");
        } else if (amount > 50) {
            points += (int) (amount - 50);
            log.debug("Added {} points for amount between $50 and ${}", (int) (amount - 50), amount);
        }
        log.info("Total reward points calculated: {}", points);
        return points;
    }
}
