package com.rewards.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

/**
 * Represents a customer entity with basic information and transaction history.
 * 
 * This class holds the customer name, unique customer ID, and the list of
 * transactions
 * made by the customer. It is used across the rewards calculation system.
 * 
 * Lombok annotations simplify the creation of boilerplate code like getters,
 * setters,
 * constructors, etc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    /**
     * Name of the customer.
     * This field must not be empty.
     */
    @NotEmpty(message = "Customer name must not be empty.")
    private String customerName;

    /**
     * Unique identifier for the customer.
     * This field must not be null.
     */
    @NotNull(message = "Customer ID must not be null.")
    private Integer customerId;

    /**
     * List of transactions associated with the customer.
     * This list must not be empty during creation.
     */
    @NotEmpty(message = "Transactions list must not be empty.")
    private List<Transaction> transactions;
}