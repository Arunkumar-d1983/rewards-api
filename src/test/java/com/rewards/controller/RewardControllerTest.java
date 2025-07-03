package com.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewards.model.*;
import com.rewards.dto.RewardResponse;
import com.rewards.service.RewardService;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

/**
 * Unit tests for {@link RewardController} using MockMvc and Mockito.
 * Covers customer creation, transaction addition, and reward calculation.
 */
@Slf4j
@WebMvcTest(RewardController.class)
class RewardControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private RewardService rewardService;

        @Autowired
        private ObjectMapper objectMapper;

        /**
         * Test for adding a customer.
         * Verifies status code 200 and JSON response contains expected customer name.
         */
        @Test
        void testAddCustomer() throws Exception {
                Customer customer = new Customer("Arunkumar", 1001, new ArrayList<>());
                Mockito.when(rewardService.addCustomer(any())).thenReturn(customer);
                log.info("Testing POST /api/rewards/customers for adding customer: {}", customer.getCustomerName());
                mockMvc.perform(post("/api/rewards/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.customerName").value("Arunkumar"));
        }

        /**
         * Test for adding a transaction to a customer.
         * Verifies the transaction is correctly added and returned in response.
         */
        @Test
        void testAddTransaction() throws Exception {
                Transaction tx = new Transaction(1, LocalDate.now().minusDays(3), 100.0, 0);
                Customer customer = new Customer("Arunkumar", 1001, new ArrayList<>(Arrays.asList(tx)));
                Mockito.when(rewardService.addTransaction(Mockito.eq(1001), any(Transaction.class)))
                                .thenReturn(customer);
                log.info("Testing POST /api/rewards/customers/1001/transactions for transaction ID: {}",
                                tx.getTransactionId());
                mockMvc.perform(post("/api/rewards/customers/1001/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tx)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.transactions.length()").value(1));
        }

        /**
         * Test for retrieving customer reward summary for the last 3 months.
         * Ensures correct response structure and total reward points.
         */
        @Test
        void testGetCustomerRewards() throws Exception {
                List<RewardResponse.MonthlyReward> monthlyRewards = Arrays.asList(
                                new RewardResponse.MonthlyReward(2025, "JULY", 60));
                RewardResponse response = new RewardResponse("Arunkumar", 1001, monthlyRewards, 60);

                // Use 1001 to match the endpoint URL
                Mockito.when(rewardService.calculateRewards(1001))
                                .thenReturn(CompletableFuture.completedFuture(response));

                log.info("Testing GET /api/rewards/customerRewards/1001 for reward summary...");
                // Perform the initial async request
                MvcResult mvcResult = mockMvc.perform(get("/api/rewards/customerRewards/1001")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(request().asyncStarted())
                                .andReturn();

                // Now dispatch and verify the actual response
                mockMvc.perform(asyncDispatch(mvcResult))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.customerName").value("Arunkumar"))
                                .andExpect(jsonPath("$.totalPoints").value(60));

                log.info("GET /api/rewards/customerRewards/1001 test passed. Total Points: 60");
        }

}
