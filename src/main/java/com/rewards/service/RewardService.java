package com.rewards.service;

import com.rewards.dto.RewardResponse;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.repository.CustomerRepository;
import com.rewards.utils.RewardCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service class responsible for business logic of rewards and transactions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {

    private final CustomerRepository customerRepository;

    /**
     * Asynchronously calculates reward points for a customer over the last 3
     * months.
     *
     * @param customer The customer whose rewards are to be calculated
     * @return A CompletableFuture containing RewardResponse
     */
    @Async
    public CompletableFuture<RewardResponse> calculateRewards(Integer customerId) {
        log.info("Initiating reward calculation for customer ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found for ID: {}", customerId);
                    return new NoSuchElementException("Customer not found with ID: " + customerId);
                });

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(3);
        log.debug("Filtering transactions from {} to {}", startDate, now);

        List<Transaction> last3MonthsTxns = customer.getTransactions().stream()
                .filter(tx -> !tx.getTransactionDate().isBefore(startDate) && !tx.getTransactionDate().isAfter(now))
                .map(tx -> {
                    int points = RewardCalculator.calculatePoints(tx.getAmount());
                    tx.setPoints(points);
                    log.debug("Transaction ID {} on {} - Amount: {} => Points: {}",
                            tx.getTransactionId(), tx.getTransactionDate(), tx.getAmount(), points);
                    return tx;
                })
                .collect(Collectors.toList());

        Map<YearMonth, Integer> grouped = last3MonthsTxns.stream()
                .collect(Collectors.groupingBy(
                        tx -> YearMonth.from(tx.getTransactionDate()),
                        Collectors.summingInt(Transaction::getPoints)));

        List<RewardResponse.MonthlyReward> monthlyRewards = grouped.entrySet().stream()
                .map(entry -> new RewardResponse.MonthlyReward(
                        entry.getKey().getYear(),
                        entry.getKey().getMonth().name(),
                        entry.getValue()))
                .sorted(Comparator.comparing((RewardResponse.MonthlyReward r) -> r.getYear())
                        .thenComparing(r -> Month.valueOf(r.getMonth()).getValue()))
                .collect(Collectors.toList());

        int totalPoints = monthlyRewards.stream().mapToInt(RewardResponse.MonthlyReward::getPoints).sum();

        log.info("Reward calculation complete for customer ID {}. Total Points: {}", customerId, totalPoints);

        return CompletableFuture.completedFuture(
                new RewardResponse(customer.getCustomerName(), customerId, monthlyRewards, totalPoints));
    }

    /**
     * Adds a new customer after validating uniqueness and input.
     *
     * @param customer customer to be added
     * @return The saved customer object
     */
    public Customer addCustomer(Customer customer) {
        log.info("Attempting to add new customer: {}", customer);

        if (customer == null || customer.getCustomerId() == null) {
            log.error("Invalid customer input: {}", customer);
            throw new IllegalArgumentException("Customer ID must not be null.");
        }

        boolean exists = customerRepository.existsById(customer.getCustomerId());
        if (exists) {
            log.warn("Customer already exists with ID: {}", customer.getCustomerId());
            throw new IllegalArgumentException("Customer with ID " + customer.getCustomerId() + " already exists.");
        }

        Customer saved = customerRepository.save(customer);
        log.info("Customer successfully added: {}", saved);
        return saved;
    }

    /**
     * Adds a transaction to the specified customer.
     *
     * @param customerId  ID of the customer
     * @param transaction new transaction to be added
     * @return updated customer with new transaction
     */
    public Customer addTransaction(Integer customerId, Transaction transaction) {
        log.info("Adding transaction to customer ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new IllegalArgumentException("Customer not found with ID: " + customerId);
                });

        if (transaction == null) {
            log.error("Transaction is null for customer ID: {}", customerId);
            throw new IllegalArgumentException("Transaction cannot be null.");
        }

        // Calculate points before saving
        // int points = RewardCalculator.calculatePoints(transaction.getAmount());
        // transaction.setPoints(points);
        // log.debug("Transaction calculated: ID = {}, Amount = {}, Points = {}",
        // transaction.getTransactionId(), transaction.getAmount(), points);

        customer.getTransactions().add(transaction);

        Customer updated = customerRepository.save(customer);

        log.info("Transaction ID {} added to customer ID {}. Total transactions: {}",
                transaction.getTransactionId(), customerId, updated.getTransactions().size());

        return updated;
    }
}