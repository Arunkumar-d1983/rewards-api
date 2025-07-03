package com.rewards.repository;

import com.rewards.model.Customer;
import org.springframework.stereotype.Repository;
import java.util.*;

/**
 * In-memory repository for customers.
 */
@Repository
public class CustomerRepository {

    private final Map<Integer, Customer> store = new HashMap<>();

    /**
     * Checks if a customer with the specified ID exists in the repository.
     *
     * @param id The ID of the customer to check.
     * @return true if the customer exists in the repository; false otherwise.
     */
    public boolean existsById(Integer id) {
        return store.containsKey(id);
    }

    /**
     * Save or update a customer.
     *
     * @param customer the customer to save
     * @return saved customer
     */
    public Customer save(Customer customer) {
        store.put(customer.getCustomerId(), customer);
        return customer;
    }

    /**
     * Find a customer by ID.
     *
     * @param id customer ID
     * @return Optional of Customer
     */
    public Optional<Customer> findById(Integer id) {
        return Optional.ofNullable(store.get(id));
    }

}
