package com.rewards.dto;

import java.util.List;

public class BulkRewardResponse {
    private List<RewardResponse> customers;

    public BulkRewardResponse(List<RewardResponse> customers) {
        this.customers = customers;
    }

    public List<RewardResponse> getCustomers() {
        return customers;
    }

    public void setCustomers(List<RewardResponse> customers) {
        this.customers = customers;
    }
}