package com.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.time.LocalDate;
import java.util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        /**
         * Test for adding a customer.
         * Verifies status code 200 and JSON response contains expected customer name.
         */
        @Test
        void testAddCustomer() throws Exception {
                Customer customer = new Customer("Arunkumar", 1001, new ArrayList<>());

                Mockito.when(rewardService.addCustomer(Mockito.any())).thenReturn(customer);

                mockMvc.perform(post("/api/rewards/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(customer)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.customerId").value(1001));
        }

        /**
         * Test for adding a transaction to a customer.
         * Verifies the transaction is correctly added and returned in response.
         */
        @Test
        void testAddTransaction() throws Exception {
                Transaction tx = new Transaction(1, LocalDate.now(), 120.0, 60);
                Customer updatedCustomer = new Customer("Arunkumar", 1001, Arrays.asList(tx));

                Mockito.when(rewardService.addTransaction(Mockito.eq(1001), Mockito.any())).thenReturn(updatedCustomer);

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // optional for ISO

                mockMvc.perform(post("/api/rewards/customers/1001/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(tx)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.transactions[0].transactionId").value(1));
        }

        /**
         * Test for retrieving customer reward summary for the last 3 months.
         * Ensures correct response structure and total reward points.
         */

        @Test
        void testGetCustomerRewards() throws Exception {
                List<MonthlyReward> monthlyRewards = Arrays.asList(new MonthlyReward(2025, "JULY", 60));
                List<Transaction> txns = Arrays.asList(
                                new Transaction(1, LocalDate.of(2025, 7, 1), 120.0, 60));

                RewardResponse response = new RewardResponse("Arunkumar", 1001, monthlyRewards, 60, txns);

                Mockito.when(rewardService.calculateRewards(1001, "2025-07-01", "2025-07-31"))
                                .thenReturn(response);

                log.info("Testing GET /api/rewards/customerRewards/1001 with date range...");

                mockMvc.perform(get("/api/rewards/customerRewards/1001")
                                .param("startDate", "2025-07-01")
                                .param("endDate", "2025-07-31")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.customerName").value("Arunkumar"))
                                .andExpect(jsonPath("$.totalPoints").value(60))
                                .andExpect(jsonPath("$.transactions[0].transactionId").value(1))
                                .andExpect(jsonPath("$.transactions[0].points").value(60));

                log.info("GET /api/rewards/customerRewards/1001 test passed. Total Points: 60");
        }

}
