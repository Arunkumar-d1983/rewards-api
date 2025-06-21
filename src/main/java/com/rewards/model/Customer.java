package com.rewards.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class Customer {

    @NotEmpty
    private String customerName;

    @NotNull
    private Integer customerId;

    @NotEmpty
    private List<Transaction> transactions;

    public Customer() {
    }

    public Customer(String customerName, Integer customerId, List<Transaction> transactions) {
        this.customerName = customerName;
        this.customerId = customerId;
        this.transactions = transactions;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
