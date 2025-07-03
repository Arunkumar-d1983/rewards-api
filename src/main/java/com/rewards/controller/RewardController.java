package com.rewards.controller;

import com.rewards.dto.RewardResponse;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.service.RewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    /**
     * Returns rewards for a specific customer for the last 3 months.
     *
     * @param customerId ID of the customer
     * @return RewardResponse with points summary
     */
    @GetMapping("/customerRewards/{customerId}")
    public CompletableFuture<ResponseEntity<RewardResponse>> getCustomerRewards(
            @PathVariable Integer customerId) {
        log.info("Received request to calculate rewards for customer ID: {}", customerId);
        return rewardService.calculateRewards(customerId)
                .thenApply(response -> {
                    log.info("Calculated total reward points for customer ID {}: {}", customerId,
                            response.getTotalPoints());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Adds a customer to the system.
     *
     * @param customer The customer to add
     * @return The added customer
     */
    @PostMapping("/customers")
    public ResponseEntity<Customer> addCustomer(@Valid @RequestBody Customer customer) {
        log.info("Received request to add new customer: {}", customer.getCustomerName());
        Customer savedCustomer = rewardService.addCustomer(customer);
        log.info("Customer added successfully with ID: {}", savedCustomer.getCustomerId());
        return ResponseEntity.ok(savedCustomer);
    }

    /**
     * Adds a transaction to an existing customer.
     *
     * @param customerId  ID of the customer
     * @param transaction Transaction to add
     * @return Updated customer with the new transaction
     */
    @PostMapping("/customers/{customerId}/transactions")
    public ResponseEntity<Customer> addTransactionToCustomer(
            @PathVariable Integer customerId,
            @Valid @RequestBody Transaction transaction) {
        log.info("Received request to add transaction (ID: {}) to customer ID: {}",
                transaction.getTransactionId(), customerId);
        Customer updatedCustomer = rewardService.addTransaction(customerId, transaction);
        log.info("Transaction added. Customer ID: {}, New total transactions: {}",
                customerId, updatedCustomer.getTransactions().size());
        return ResponseEntity.ok(updatedCustomer);
    }
}