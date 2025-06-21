package com.rewards;

import com.rewards.dto.RewardResponse;
import com.rewards.model.*;
import com.rewards.service.RewardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.*;
import org.slf4j.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RewardsApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(RewardsApplicationTests.class);

	@Autowired
	private RewardService rewardService;

	@Test
	@DisplayName("Test: Reward calculation for a dynamic 3-month window")
	void testRewardCalculationWithDynamicTimeFrame() {
		// Arrange
		LocalDate now = LocalDate.now();
		LocalDate startDate = now.minusMonths(3);
		LocalDate endDate = now;

		List<Transaction> transactions = Arrays.asList(
				new Transaction(1, now.minusMonths(1), 120.0), // 90 points
				new Transaction(2, now.minusMonths(2), 80.0), // 30 points
				new Transaction(3, now.minusMonths(4), 70.0) // Should be excluded
		);

		Customer customer = new Customer("Arunkumar", 1001, transactions);

		// Act
		RewardResponse response = rewardService.calculateRewards(customer, startDate, endDate).join(); // Await async

		// Assert
		assertEquals(120, response.getTotalPoints(), "Total points should be 120");
		assertEquals(2, response.getTransactions().size(), "Only 2 transactions should be included in range");

		Map<String, Integer> monthlyPoints = response.getMonthlyPoints();
		assertTrue(monthlyPoints.values().contains(90), "Should include a month with 90 points");
		assertTrue(monthlyPoints.values().contains(30), "Should include a month with 30 points");

		logger.info("Reward Calculation Test Passed:");
		logger.info("Customer: {}", response.getCustomerName());
		logger.info("Monthly Points: {}", monthlyPoints);
		logger.info("Total Points: {}", response.getTotalPoints());
	}

	@Test
	@DisplayName("Test: No transactions within time frame")
	void testNoValidTransactions() {
		LocalDate now = LocalDate.now();
		LocalDate startDate = now.minusMonths(3);
		LocalDate endDate = now;

		List<Transaction> transactions = Arrays.asList(
				new Transaction(10, now.minusMonths(5), 120.0),
				new Transaction(11, now.minusMonths(4), 90.0));

		Customer customer = new Customer("EmptyCase", 2002, transactions);

		RewardResponse response = rewardService.calculateRewards(customer, startDate, endDate).join();

		assertEquals(0, response.getTotalPoints(), "Expected 0 reward points");
		assertTrue(response.getMonthlyPoints().isEmpty(), "Monthly points map should be empty");
		assertEquals(0, response.getTransactions().size(), "Transaction list should be empty");

		logger.info("No Valid Transactions Test Passed for customer '{}'", customer.getCustomerName());
	}

	@Test
	@DisplayName("Test: Single high-value transaction")
	void testSingleHighTransaction() {
		LocalDate date = LocalDate.now().minusDays(10);

		Customer customer = new Customer("Arunkumar", 3003, Arrays.asList(
				new Transaction(99, date, 250.0)));

		RewardResponse response = rewardService.calculateRewards(customer, date.minusDays(1), date.plusDays(1)).join();

		// Points: 2x(250 - 100) + 50 = 2x150 + 50 = 350
		assertEquals(350, response.getTotalPoints(), "Expected 350 points for $250 transaction");

		logger.info("Single High Transaction Test Passed for customer '{}'", customer.getCustomerName());
		logger.info("Transaction Points: {}", response.getTotalPoints());
	}
}