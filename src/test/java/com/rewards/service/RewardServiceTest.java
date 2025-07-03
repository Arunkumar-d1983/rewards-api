package com.rewards.service;

import com.rewards.model.*;
import com.rewards.repository.CustomerRepository;
import com.rewards.utils.RewardCalculator;

import lombok.extern.slf4j.Slf4j;

import com.rewards.dto.RewardResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RewardService methods: addCustomer, addTransaction, and
 * calculateRewards.
 */

@Slf4j
class RewardServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RewardService rewardService;

    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
        log.info("Initialized mocks for RewardService tests.");
    }

    @AfterEach
    void teardown() throws Exception {
        closeable.close();
        log.info("Closed mocks after test execution.");
    }

    /**
     * Test to verify that a new customer is correctly added.
     */
    @Test
    void testAddCustomer() {
        Customer customer = new Customer("Arunkumar", 1001, new ArrayList<>());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = rewardService.addCustomer(customer);
        assertNotNull(result);
        assertEquals("Arunkumar", result.getCustomerName());
        verify(customerRepository, times(1)).save(customer);
        log.info("testAddCustomer passed: Customer '{}' added successfully.", result.getCustomerName());
    }

    /**
     * Test to verify that a transaction is added and reward points are calculated.
     */
    @Test
    void testAddTransaction() {
        Transaction tx = new Transaction(3, LocalDate.now().minusDays(2), 120.0, 0);
        Customer customer = new Customer("Arunkumar", 1001, new ArrayList<>());

        when(customerRepository.findById(2)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);

        tx.setPoints(RewardCalculator.calculatePoints(tx.getAmount()));

        Customer result = rewardService.addTransaction(2, tx);
        assertEquals(1, result.getTransactions().size());
        assertTrue(result.getTransactions().get(0).getPoints() > 0);
        log.info("testAddTransaction passed: Transaction added with {} points.",
                result.getTransactions().get(0).getPoints());
    }

    /**
     * Test to verify reward calculation for the last 3 months.
     */
    @Test
    void testCalculateRewards() throws Exception {
        List<Transaction> txns = Arrays.asList(
                new Transaction(1, LocalDate.now().minusDays(10), 150.0, 0),
                new Transaction(2, LocalDate.now().minusMonths(1), 90.0, 0));
        Customer customer = new Customer("Arunkumar", 1001, txns);
        when(customerRepository.findById(3)).thenReturn(Optional.of(customer));

        CompletableFuture<RewardResponse> future = rewardService.calculateRewards(3);
        RewardResponse response = future.get();

        assertEquals("Arunkumar", response.getCustomerName());
        assertTrue(response.getTotalPoints() > 0);
        assertFalse(response.getMonthlyRewards().isEmpty());

        log.info("testCalculateRewards passed: Total points = {}", response.getTotalPoints());
    }
}