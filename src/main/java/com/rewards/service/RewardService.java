package com.rewards.service;

import com.rewards.dto.RewardResponse;
import com.rewards.model.*;
import com.rewards.utils.RewardCalculator;
import org.slf4j.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Async
    public CompletableFuture<RewardResponse> calculateRewards(Customer customer, LocalDate startDate,
            LocalDate endDate) {
        logger.info("Calculating rewards for customerId={} between {} and {}", customer.getCustomerId(), startDate,
                endDate);

        if (customer == null || customer.getTransactions() == null) {
            logger.error("Customer and transaction list must not be null for customerId={}", customer.getCustomerId());
            throw new IllegalArgumentException("Customer and transaction list must not be null");
        }

        Map<String, Integer> monthlyPoints = new HashMap<>();
        List<Transaction> validTransactions = customer.getTransactions().stream()
                .filter(tx -> !tx.getTransactionDate().isBefore(startDate) && !tx.getTransactionDate().isAfter(endDate))
                .peek(tx -> {
                    int points = RewardCalculator.calculatePoints(tx.getAmount());
                    tx.setPoints(points);
                    String month = tx.getTransactionDate().format(MONTH_FORMATTER);
                    monthlyPoints.merge(month, points, Integer::sum);
                    logger.info("Transaction {}: amount={}, points={}", tx.getTransactionId(), tx.getAmount(), points);
                })
                .filter(tx -> tx.getPoints() > 0)
                .collect(Collectors.toList());

        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();

        return CompletableFuture.completedFuture(
                new RewardResponse(customer.getCustomerName(), customer.getCustomerId(), monthlyPoints, totalPoints,
                        validTransactions));
    }
}