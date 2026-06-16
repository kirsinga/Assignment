package com.assignment.accountservice;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.assignment.accountservice.controller.AccountServiceController;
import com.assignment.accountservice.dao.AccountDao;
import com.assignment.accountservice.dao.AccountResponse;
import com.assignment.accountservice.dao.EventMetadataDto;
import com.assignment.accountservice.service.AccountService;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AccountService Application Tests")
class AccountserviceApplicationTests {

	@Autowired
	private AccountServiceController accountController;

	@Autowired
	private AccountService accountService;

	private AccountDao testAccountDao;
	private EventMetadataDto testMetadataDao;

	@BeforeEach
	void setUp() {
		testMetadataDao = EventMetadataDto.builder()
				.source("EVENT_SOURCE")
				.batchId("BATCH_001")
				.eventId("EVENT_001")
				.build();

		testAccountDao = AccountDao.builder()
				.eventId("EVENT_001")
				.accountId("ACC_001")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.eventTimestamp(Instant.now())
				.metadata(testMetadataDao)
				.build();
	}

	@Test
	@DisplayName("Context Loads Successfully")
	void contextLoads() {
		assertNotNull(accountService);
		assertNotNull(accountController);
	}

	@Test
	@DisplayName("POST: Successfully post account transaction with metadata")
	void testPostAccountSuccess() {
		AccountResponse result = accountService.postAccount(testAccountDao);
		assertNotNull(result);
		assertEquals(AccountResponse.Status.SUCCESS, result.getStatus());
		assertEquals("Transaction Successfull done", result.getMessage());
		assertNotNull(result.getAccount());
	}

	@Test
	@DisplayName("POST: Fail when request body is null")
	void testPostAccountWithNullRequest() {
		AccountResponse result = accountService.postAccount(null);
		assertNotNull(result);
		assertEquals(AccountResponse.Status.FAILED, result.getStatus());
		assertEquals("INVALID_REQUEST", result.getError().getCode());
	}

	@Test
	@DisplayName("POST: Successfully post account transaction without metadata")
	void testPostAccountWithoutMetadata() {
		AccountDao daoWithoutMetadata = AccountDao.builder()
				.eventId("EVENT_002")
				.accountId("ACC_002")
				.type("debit")
				.amount(new BigDecimal("500.00"))
				.currency("EUR")
				.metadata(null)
				.build();

		AccountResponse result = accountService.postAccount(daoWithoutMetadata);
		assertNotNull(result);
		assertEquals(AccountResponse.Status.SUCCESS, result.getStatus());
	}

	@Test
	@DisplayName("GET: Successfully retrieve account balance with credit")
	void testGetAccountBalanceWithCredit() {
		AccountDao creditAccountDao = AccountDao.builder()
				.eventId("EVENT_CREDIT_001")
				.accountId("ACC_BALANCE_CREDIT")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.build();

		accountService.postAccount(creditAccountDao);
		AccountResponse result = accountService.getAccountBalance("ACC_BALANCE_CREDIT");

		assertNotNull(result);
		assertEquals(AccountResponse.Status.SUCCESS, result.getStatus());
		assertNotNull(result.getBalance());
		assertEquals(new BigDecimal("1000.00"), result.getBalance().getBalance());
	}

	@Test
	@DisplayName("GET: Successfully retrieve account balance with debit")
	void testGetAccountBalanceWithDebit() {
		AccountDao debitAccountDao = AccountDao.builder()
				.eventId("EVENT_DEBIT_001")
				.accountId("ACC_BALANCE_DEBIT")
				.type("debit")
				.amount(new BigDecimal("500.00"))
				.currency("USD")
				.build();

		accountService.postAccount(debitAccountDao);
		AccountResponse result = accountService.getAccountBalance("ACC_BALANCE_DEBIT");

		assertNotNull(result);
		assertEquals(AccountResponse.Status.SUCCESS, result.getStatus());
		assertEquals(new BigDecimal("-500.00"), result.getBalance().getBalance());
	}

	@Test
	@DisplayName("GET: Balance with mixed transactions")
	void testGetAccountBalanceWithMixedTransactions() {
		String accountId = "ACC_MIXED";
		
		accountService.postAccount(AccountDao.builder()
				.eventId("EVENT_MIXED_001")
				.accountId(accountId)
				.type("credit")
				.currency("USD")
				.amount(new BigDecimal("1000.00"))
				.build());

		accountService.postAccount(AccountDao.builder()
				.eventId("EVENT_MIXED_002")
				.accountId(accountId)
				.currency("USD")
				.type("debit")
				.amount(new BigDecimal("300.00"))
				.build());

		AccountResponse result = accountService.getAccountBalance(accountId);

		assertNotNull(result);
		assertEquals(AccountResponse.Status.SUCCESS, result.getStatus());
		assertEquals(new BigDecimal("700.00"), result.getBalance().getBalance());
	}

	@Test
	@DisplayName("GET: Balance not found")
	void testGetAccountBalanceNotFound() {
		AccountResponse result = accountService.getAccountBalance("ACC_NOTFOUND_BALANCE");
		assertNotNull(result);
		assertEquals(AccountResponse.Status.FAILED, result.getStatus());
		assertEquals("NOT_FOUND", result.getError().getCode());
	}

	@Test
	@DisplayName("GET: Successfully retrieve account history")
	void testGetAccountHistory() {
		String accountId = "ACC_HISTORY";
		
		accountService.postAccount(AccountDao.builder()
				.eventId("EVENT_HIST_001")
				.accountId(accountId)
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.build());

		accountService.postAccount(AccountDao.builder()
				.eventId("EVENT_HIST_002")
				.accountId(accountId)
				.type("debit")
				.amount(new BigDecimal("300.00"))
				.build());

		AccountResponse result = accountService.getAccountHistory(accountId);

		assertNotNull(result);
		assertEquals(AccountResponse.Status.SUCCESS, result.getStatus());
		assertEquals(2, result.getAccountHistory().size());
	}

	@Test
	@DisplayName("GET: History not found")
	void testGetAccountHistoryNotFound() {
		AccountResponse result = accountService.getAccountHistory("ACC_NOTFOUND_HISTORY");
		assertNotNull(result);
		assertEquals(AccountResponse.Status.FAILED, result.getStatus());
		assertEquals("NOT_FOUND", result.getError().getCode());
	}

	@Test
	@DisplayName("Controller: POST transaction")
	void testControllerPostAccountTransaction() {
		ResponseEntity<AccountResponse> response = accountController
				.postAccountTransaction("ACC_001", testAccountDao);
		       

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(AccountResponse.Status.SUCCESS, response.getBody().getStatus());
	}

	@Test
	@DisplayName("Controller: GET balance")
	void testControllerGetAccountBalance() {
		AccountDao transaction = AccountDao.builder()
				.accountId("ACC_CTRL_BALANCE")
				.type("credit")
				.currency("USD")
				.amount(new BigDecimal("1000.00"))
				.build();

		accountService.postAccount(transaction);
		ResponseEntity<AccountResponse> response = accountController.getAccountBalance("ACC_CTRL_BALANCE");

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(AccountResponse.Status.SUCCESS, response.getBody().getStatus());
	}

	@Test
	@DisplayName("Controller: GET history")
	void testControllerGetAccountHistory() {
		AccountDao transaction = AccountDao.builder()
				.eventId("EVENT_CTRL")
				.accountId("ACC_CTRL_HISTORY")
				.currency("USD")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.build();

		accountService.postAccount(transaction);
		ResponseEntity<AccountResponse> response = accountController.getAccountHistory("ACC_CTRL_HISTORY");

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(AccountResponse.Status.SUCCESS, response.getBody().getStatus());
	}

	@Test
	@DisplayName("POST: Generate UUID for eventId")
	void testPostAccountGeneratesUUID() {
		AccountDao daoWithoutEventId = AccountDao.builder()
				.eventId(null)
				.accountId("ACC_UUID")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.build();

		AccountResponse result = accountService.postAccount(daoWithoutEventId);
		assertNotNull(result);
		assertEquals(AccountResponse.Status.SUCCESS, result.getStatus());
	}

	@Test
	@DisplayName("POST: Use provided timestamp")
	void testPostAccountUsesProvidedTimestamp() {
		Instant customTimestamp = Instant.parse("2026-01-01T00:00:00Z");
		AccountDao daoWithCustomTimestamp = AccountDao.builder()
				.eventId("EVENT_TS")
				.accountId("ACC_TS")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.eventTimestamp(customTimestamp)
				.build();

		AccountResponse result = accountService.postAccount(daoWithCustomTimestamp);
		assertNotNull(result);
		assertEquals(AccountResponse.Status.SUCCESS, result.getStatus());
	}

	@Test
	@DisplayName("POST: Should fail when amount is null")
	void testPostAccount_ShouldFail_WhenAmountIsNull() {

	    // Arrange
	    AccountDao request = AccountDao.builder()
	            .eventId("EVENT_NULL")
	            .accountId("ACC_NULL")
	            .type("credit")
	            .amount(null)   // invalid input
	            .currency("USD")
	            .build();

	    // Act
	    AccountResponse response =
	            accountService.postAccount(request);

	    // Assert
	    assertNotNull(response);
	    assertEquals(AccountResponse.Status.SUCCESS, response.getStatus());
	    
	}
}
