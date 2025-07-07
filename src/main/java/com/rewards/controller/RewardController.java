package com.rewards.controller;

import com.rewards.dto.RewardResponse;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.service.RewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
public class RewardController {

    private final RewardService rewardService;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
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
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCustomer);
    }

    /**
     * Returns rewards for a specific customer for the given date range.
     *
     * @param customerId ID of the customer
     * @param startDate  Start date of range
     * @param endDate    End date of range
     * @return RewardResponse with points summary
     */
    @GetMapping("/customerRewards/{customerId}")
    public ResponseEntity<RewardResponse> getCustomerRewards(
            @PathVariable Integer customerId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("Received request to calculate rewards for customer ID: {}", customerId);
        RewardResponse response = rewardService.calculateRewards(customerId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}