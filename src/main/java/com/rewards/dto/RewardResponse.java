package com.rewards.dto;

import com.rewards.model.Transaction;

import java.util.List;
import java.util.Map;

public class RewardResponse {
    private String customerName;
    private int customerId;
    private Map<String, Integer> monthlyPoints;
    private int totalPoints;
    private List<Transaction> transactions;

    public RewardResponse() {
    }

    public RewardResponse(String customerName, int customerId, Map<String, Integer> monthlyPoints, int totalPoints,
            List<Transaction> transactions) {
        this.customerName = customerName;
        this.customerId = customerId;
        this.monthlyPoints = monthlyPoints;
        this.totalPoints = totalPoints;
        this.transactions = transactions;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Map<String, Integer> getMonthlyPoints() {
        return monthlyPoints;
    }

    public void setMonthlyPoints(Map<String, Integer> monthlyPoints) {
        this.monthlyPoints = monthlyPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
