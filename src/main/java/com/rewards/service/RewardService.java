package com.rewards.service;

import com.rewards.dto.RewardResponse;
import com.rewards.model.Customer;
import com.rewards.model.MonthlyReward;
import com.rewards.model.Transaction;
import com.rewards.repository.CustomerRepository;
import com.rewards.utils.RewardCalculator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class RewardService {

    private final CustomerRepository customerRepository;

    public Customer addCustomer(Customer customer) {

        log.info("Attempting to add new customer: {}", customer);

        if (customer == null || customer.getCustomerId() == null) {
            log.error("Invalid customer input: {}", customer);
            throw new IllegalArgumentException("Customer ID must not be null.");
        }
        if (customer == null || customer.getCustomerName() == null) {
            log.error("Invalid customer input: {}", customer);
            throw new IllegalArgumentException("Customer Name must not be null.");
        }

        if (customerRepository.existsById(customer.getCustomerId())) {
            log.warn("Customer already exists with ID: {}", customer.getCustomerId());
            throw new IllegalArgumentException("Customer with ID " + customer.getCustomerId() + " already exists.");
        }

        // Calculate points for all transactions
        customer.getTransactions().forEach(tx -> {
            tx.setPoints(RewardCalculator.calculatePoints(tx.getAmount()));
        });

        return customerRepository.save(customer);
    }

    public Customer addTransaction(Integer customerId, @Valid Transaction transaction) {
        log.info("Adding transaction to customer ID: {}", customerId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        if (transaction.getTransactionId() == null) {
            log.error("Invalid transaction input: {}", transaction);
            throw new IllegalArgumentException("Transaction ID must not be null.");
        }
        if (transaction.getTransactionDate() == null) {
            log.error("Invalid transaction input: {}", transaction);
            throw new IllegalArgumentException("Transaction Date must not be null.");
        }
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            log.error("Invalid Amount input: {}", transaction);
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        transaction.setPoints(RewardCalculator.calculatePoints(transaction.getAmount()));
        customer.getTransactions().add(transaction);
        Customer updated = customerRepository.save(customer);

        log.info("Transaction ID {} added to customer ID {}. Total transactions: {}",
                transaction.getTransactionId(), customerId, updated.getTransactions().size());
        return updated;
    }

    public RewardResponse calculateRewards(Integer customerId, String start, String end) {

        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        if (start == null || end == null) {
            // Default: Last 3 months
            startDate = now.minusMonths(3);
            endDate = now;
            log.info("No startDate/endDate provided. Using default last 3 months: {} to {}", startDate, endDate);
        } else {
            try {
                startDate = LocalDate.parse(start);
                // endDate = LocalDate.parse(end);
                endDate = end != null ? LocalDate.parse(end) : LocalDate.now();
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD.");
            }

            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date must not be after end date.");
            }
            if (endDate.isAfter(now)) {
                throw new IllegalArgumentException("End date cannot be in the future.");
            }
            if (startDate.isAfter(now)) {
                throw new IllegalArgumentException("Start date cannot be in the future.");
            }
        }
        LocalDate calculatedStartDate = endDate.minusMonths(3).plusDays(1);
        log.info("Calculating rewards for customerId={} between {} and {}", customerId, calculatedStartDate,
                endDate);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));

        List<Transaction> filteredTransactions = customer.getTransactions().stream()
                .filter(tx -> !tx.getTransactionDate().isBefore(calculatedStartDate)
                        && !tx.getTransactionDate().isAfter(endDate))
                .peek(tx -> tx.setPoints(RewardCalculator.calculatePoints(tx.getAmount())))
                .collect(Collectors.toList());

        Map<YearMonth, Integer> grouped = filteredTransactions.stream()
                .collect(Collectors.groupingBy(
                        tx -> YearMonth.from(tx.getTransactionDate()),
                        Collectors.summingInt(Transaction::getPoints)));

        List<MonthlyReward> monthlyRewards = grouped.entrySet().stream()
                .map(e -> new MonthlyReward(e.getKey().getYear(), e.getKey().getMonth().name(), e.getValue()))
                .sorted(Comparator.comparing(MonthlyReward::getYear)
                        .thenComparing(r -> Month.valueOf(r.getMonth()).getValue()))
                .collect(Collectors.toList());

        int totalPoints = monthlyRewards.stream().mapToInt(MonthlyReward::getPoints).sum();

        return new RewardResponse(customer.getCustomerName(), customerId, monthlyRewards, totalPoints,
                filteredTransactions);
    }

}