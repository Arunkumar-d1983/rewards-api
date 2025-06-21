package com.rewards.controller;

import com.rewards.dto.*;
import com.rewards.model.Customer;
import com.rewards.service.RewardService;
import jakarta.validation.Valid;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private static final Logger logger = LoggerFactory.getLogger(RewardController.class);

    @Autowired
    private RewardService rewardService;

    @PostMapping
    public CompletableFuture<ResponseEntity<RewardResponse>> getCustomerRewards(
            @Valid @RequestBody Customer customer,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        logger.info("Received reward calculation request for customerId: {}", customer.getCustomerId());

        validateCustomer(customer);

        LocalDate today = LocalDate.now();
        LocalDate endDate = end != null ? end : LocalDate.now();
        LocalDate startDate = start != null ? start : endDate.minusMonths(3);

        validateDateRange(startDate, endDate, today);

        logger.info("Received reward calculation request for customerId: {}", customer.getCustomerId());
        logger.info("Calculating rewards from {} to {}", startDate, endDate);

        return rewardService.calculateRewards(customer, startDate, endDate)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/bulk")
    public CompletableFuture<ResponseEntity<BulkRewardResponse>> getBulkCustomerRewards(
            @Valid @RequestBody List<Customer> customers,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException("Customer list cannot be empty.");
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = end != null ? end : LocalDate.now();
        LocalDate startDate = start != null ? start : endDate.minusMonths(3);

        validateDateRange(startDate, endDate, today);

        List<CompletableFuture<RewardResponse>> futures = customers.stream()
                .map(customer -> {
                    validateCustomer(customer);
                    return rewardService.calculateRewards(customer, startDate, endDate);
                }).collect(Collectors.toList());

        CompletableFuture<Void> allDoneFuture = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]));

        return allDoneFuture.thenApply(v -> {
            List<RewardResponse> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new BulkRewardResponse(results));
        });
    }

    private void validateCustomer(Customer customer) {
        if (Objects.isNull(customer)) {
            throw new IllegalArgumentException("Customer request body cannot be null.");
        }

        if (Objects.isNull(customer.getCustomerId())) {
            throw new IllegalArgumentException("Customer ID cannot be null.");
        }

        if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }

        if (customer.getTransactions() == null || customer.getTransactions().isEmpty()) {
            throw new IllegalArgumentException("Customer transactions must be provided.");
        }
    }

    private void validateDateRange(LocalDate start, LocalDate end, LocalDate today) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        if (end.isAfter(today)) {
            throw new IllegalArgumentException("End date cannot be in the future.");
        }
        if (start.isAfter(today)) {
            throw new IllegalArgumentException("Start date cannot be in the future.");
        }
    }
}