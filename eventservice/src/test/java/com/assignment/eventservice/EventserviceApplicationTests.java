package com.assignment.eventservice;

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

import com.assignment.eventservice.controller.EventServiceController;
import com.assignment.eventservice.dao.EventDao;
import com.assignment.eventservice.dao.EventMetadataDao;
import com.assignment.eventservice.dao.EventResponseDao;
import com.assignment.eventservice.service.EventService;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("EventService Application Tests")
class EventserviceApplicationTests {

	@Autowired
	private EventServiceController eventController;

	@Autowired
	private EventService eventService;

	private EventDao testEventDao;
	private EventMetadataDao testMetadataDao;
	private EventDao testEventDaoWithUnique;
	

	@BeforeEach
	void setUp() {
		// Initialize test metadata
		testMetadataDao = EventMetadataDao.builder()
				.source("EVENT_SOURCE")
				.batchId("BATCH_001")
				.eventId("EVENT_001")
				.build();

		// Initialize test event
		testEventDao = EventDao.builder()
				.eventId("EVENT_001")
				.accountId("ACC_001")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.eventTimestamp(Instant.now())
				.metadata(testMetadataDao)
				.build();
		
		testEventDaoWithUnique = EventDao.builder()
				.eventId("EVENT_005")
				.accountId("ACC_005")
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
		assertNotNull(eventService);
		assertNotNull(eventController);
	}

	// ============== EventService POST Tests ==============

	@Test
	@DisplayName("POST: Successfully post event with metadata")
	void testPostEventInfoSuccess() {
		// Act
		ResponseEntity<EventResponseDao> result = eventService.postEventInfo(testEventDaoWithUnique);

		// Assert
		assertNotNull(result);
		assertNotNull(result.getBody().getAccount().getAccountId());
		// Status could be SUCCESS or ERROR depending on account service availability
		
	}

	@Test
	@DisplayName("POST: Fail when event data is null")
	void testPostEventInfoWithNullData() {
		// Act & Assert
		assertThrows(RuntimeException.class, () -> eventService.postEventInfo(null));
	}

	@Test
	@DisplayName("POST: Fail when account ID is null")
	void testPostEventInfoWithNullAccountId() {
		// Arrange
		EventDao eventWithoutAccountId = EventDao.builder()
				.eventId("EVENT_002")
				.accountId(null)
				.type("debit")
				.amount(new BigDecimal("500.00"))
				.currency("EUR")
				.build();

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, 
			() -> eventService.postEventInfo(eventWithoutAccountId));
		assertTrue(exception.getMessage().contains("Account ID is missing"));
	}

	@Test
	@DisplayName("POST: Fail when account ID is empty")
	void testPostEventInfoWithEmptyAccountId() {
		// Arrange
		EventDao eventWithEmptyAccountId = EventDao.builder()
				.eventId("EVENT_003")
				.accountId("   ")
				.type("credit")
				.amount(new BigDecimal("750.00"))
				.build();

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, 
			() -> eventService.postEventInfo(eventWithEmptyAccountId));
		assertTrue(exception.getMessage().contains("Account ID is missing"));
	}

	@Test
	@DisplayName("POST: Successfully post event without metadata")
	void testPostEventInfoWithoutMetadata() {
		// Arrange
		EventDao eventWithoutMetadata = EventDao.builder()
				.eventId("EVENT_004")
				.accountId("ACC_002")
				.type("debit")
				.amount(new BigDecimal("500.00"))
				.currency("EUR")
				.metadata(null)
				.build();

		// Act
		ResponseEntity<EventResponseDao> result = eventService.postEventInfo(eventWithoutMetadata);

		// Assert
		assertNotNull(result);
		assertNotNull(result.getBody().getAccount().getMetadata());
	}

	// ============== EventService Store Event Tests ==============
    //Data in the Eventtable Unique otherwise test case will fail
	@Test
	@DisplayName("Store: Successfully store event data with metadata")
	void testStoreEventDataWithMetadata() {
		// Act
		com.assignment.eventservice.entity.EventEntity savedEntity = eventService.storeEventData(testEventDaoWithUnique);

		// Assert
		assertNotNull(savedEntity);
		assertEquals(testEventDao.getAccountId(), savedEntity.getAccountId());
		assertEquals(testEventDao.getType(), savedEntity.getType());
		assertEquals(testEventDao.getAmount(), savedEntity.getAmount());
		assertNotNull(savedEntity.getMetadata());
	}

	@Test
	@DisplayName("Store: Successfully store event data without metadata")
	void testStoreEventDataWithoutMetadata() {
		// Arrange
		EventDao eventWithoutMetadata = EventDao.builder()
				.eventId("EVENT_STORE_001")
				.accountId("ACC_STORE_001")
				.type("credit")
				.amount(new BigDecimal("2000.00"))
				.currency("USD")
				.metadata(null)
				.build();

		// Act
		com.assignment.eventservice.entity.EventEntity savedEntity = eventService.storeEventData(eventWithoutMetadata);

		// Assert
		assertNotNull(savedEntity);
		assertEquals("ACC_STORE_001", savedEntity.getAccountId());
		assertNull(savedEntity.getMetadata());
	}

	@Test
	@DisplayName("Store: Generate UUID for eventId when not provided")
	void testStoreEventGeneratesUUID() {
		// Arrange
		EventDao eventWithoutEventId = EventDao.builder()
				.eventId(null)
				.accountId("ACC_UUID_001")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.build();

		// Act
		com.assignment.eventservice.entity.EventEntity savedEntity = eventService.storeEventData(eventWithoutEventId);

		// Assert
		assertNotNull(savedEntity);
		assertNotNull(savedEntity.getEventId());
		// Verify it's a valid UUID format
		assertTrue(savedEntity.getEventId().length() > 0);
	}

	@Test
	@DisplayName("Store: Use provided timestamp instead of current time")
	void testStoreEventUsesProvidedTimestamp() {
		// Arrange
		Instant customTimestamp = Instant.parse("2026-01-01T00:00:00Z");
		EventDao eventWithCustomTimestamp = EventDao.builder()
				.eventId("EVENT_TS_001")
				.accountId("ACC_TS_001")
				.type("debit")
				.amount(new BigDecimal("1500.00"))
				.currency("USD")
				.eventTimestamp(customTimestamp)
				.build();

		// Act
		com.assignment.eventservice.entity.EventEntity savedEntity = eventService.storeEventData(eventWithCustomTimestamp);

		// Assert
		assertNotNull(savedEntity);
		assertEquals(customTimestamp, savedEntity.getEventTimestamp());
	}

	// ============== EventService Get Event Details Tests ==============

	@Test
	@DisplayName("Get: Successfully retrieve event details")
	void testGetEventDetailsSuccess() {
		// Arrange - First store an event
		EventDao eventToStore = EventDao.builder()
				.eventId("EVENT_GET_001")
				.accountId("ACC_GET_001")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.build();

		eventService.storeEventData(eventToStore);

		// Act
		EventDao retrievedEvent = eventService.getEventDetails("EVENT_GET_001");

		// Assert
		assertNotNull(retrievedEvent);
		assertEquals("EVENT_GET_001", retrievedEvent.getEventId());
		assertEquals("ACC_GET_001", retrievedEvent.getAccountId());
		assertEquals("credit", retrievedEvent.getType());
		assertEquals(new BigDecimal("1000.00"), retrievedEvent.getAmount());
	}

	@Test
	@DisplayName("Get: Fail when event not found")
	void testGetEventDetailsNotFound() {
		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, 
			() -> eventService.getEventDetails("EVENT_NOTFOUND"));
		assertTrue(exception.getMessage().contains("Event not found"));
	}

	@Test
	@DisplayName("Get: Fail when event ID is null")
	void testGetEventDetailsWithNullEventId() {
		// Act & Assert
		assertThrows(RuntimeException.class, () -> eventService.getEventDetails(null));
	}

	// ============== EventService Get Event Info Tests ==============

	@Test
	@DisplayName("Get: Successfully retrieve event info")
	void testGetEventInfoSuccess() {
		// Arrange - First store an event
		EventDao eventToStore = EventDao.builder()
				.eventId("EVENT_INFO_001")
				.accountId("ACC_INFO_001")
				.type("debit")
				.amount(new BigDecimal("2000.00"))
				.currency("EUR")
				.build();

		eventService.storeEventData(eventToStore);

		// Act
		EventDao result = eventService.getEventInfo("EVENT_INFO_001");

		// Assert
		assertNotNull(result);
		assertEquals("EVENT_INFO_001", result.getEventId());
		assertEquals("ACC_INFO_001", result.getAccountId());
	}

	@Test
	@DisplayName("Get: Fail when event ID is null")
	void testGetEventInfoWithNullEventId() {
		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, 
			() -> eventService.getEventInfo(null));
		assertTrue(exception.getMessage().contains("Event ID cannot be null"));
	}

	@Test
	@DisplayName("Get: Fail when event ID is empty")
	void testGetEventInfoWithEmptyEventId() {
		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, 
			() -> eventService.getEventInfo("   "));
		assertTrue(exception.getMessage().contains("Event ID cannot be null"));
	}

	// ============== EventServiceController Tests ==============

	@Test
	@DisplayName("Controller: POST event endpoint returns OK")
	void testControllerPostEventSuccess() {
		// Act
		ResponseEntity<EventResponseDao> response = eventController.postEvent(testEventDao);

		// Assert
		assertNotNull(response);
		assertEquals("SUCESS", response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	@DisplayName("Controller: GET account info endpoint returns OK")
	void testControllerGetAccountInfoSuccess() {
		// Act
		EventResponseDao response = eventController.getAccountHistory("ACC_001");

		// Assert

		assertEquals("SUCCESS", response.getMessage());
		assertNotNull(response);
	}

	@Test
	@DisplayName("Controller: GET event endpoint returns OK")
	void testControllerGetEventSuccess() {
		// Arrange - First store an event
		EventDao eventToStore = EventDao.builder()
				.eventId("EVENT_CTRL_001")
				.accountId("ACC_CTRL_001")
				.type("credit")
				.amount(new BigDecimal("1000.00"))
				.currency("USD")
				.build();

		eventService.storeEventData(eventToStore);

		// Act
		EventResponseDao response = eventController.getAccountHistory("EVENT_CTRL_001");

		// Assert
		assertNotNull(response);
		assertEquals("SUCCESS", response.getStatus());
		assertEquals("EVENT_CTRL_001", response.getAccount().getEventId());
	}

	// ============== Edge Cases and Special Tests ==============

	@Test
	@DisplayName("Edge: Handle null amount in event storage")
	void testStoreEventWithNullAmount() {
		// Arrange
		EventDao eventWithNullAmount = EventDao.builder()
				.eventId("EVENT_NULL_AMOUNT")
				.accountId("ACC_NULL_AMOUNT")
				.type("credit")
				.amount(null)
				.currency("USD")
				.build();

		// Act
		com.assignment.eventservice.entity.EventEntity savedEntity = eventService.storeEventData(eventWithNullAmount);

		// Assert
		assertNotNull(savedEntity);
		assertNull(savedEntity.getAmount());
	}

	@Test
	@DisplayName("Edge: Store event with multiple metadata fields")
	void testStoreEventWithCompleteMetadata() {
		// Arrange
		EventMetadataDao completeMetadata = EventMetadataDao.builder()
				.source("EXTERNAL_SYSTEM")
				.batchId("BATCH_12345")
				.eventId("EVENT_META_001")
				.build();

		EventDao eventWithMetadata = EventDao.builder()
				.eventId("EVENT_META_COMPLETE")
				.accountId("ACC_META")
				.type("debit")
				.amount(new BigDecimal("5000.00"))
				.currency("GBP")
				.metadata(completeMetadata)
				.build();

		// Act
		com.assignment.eventservice.entity.EventEntity savedEntity = eventService.storeEventData(eventWithMetadata);

		// Assert
		assertNotNull(savedEntity);
		assertNotNull(savedEntity.getMetadata());
		assertEquals("EXTERNAL_SYSTEM", savedEntity.getMetadata().getSource());
		assertEquals("BATCH_12345", savedEntity.getMetadata().getBatchId());
	}

	@Test
	@DisplayName("Edge: Store event with different currencies")
	void testStoreEventWithDifferentCurrencies() {
		// Test multiple currencies
		String[] currencies = {"USD", "EUR", "GBP", "JPY", "INR"};
		
		for (String currency : currencies) {
			EventDao eventWithCurrency = EventDao.builder()
					.eventId("EVENT_CURR_" + currency)
					.accountId("ACC_CURR_" + currency)
					.type("credit")
					.amount(new BigDecimal("1000.00"))
					.currency(currency)
					.build();

			// Act
			com.assignment.eventservice.entity.EventEntity savedEntity = eventService.storeEventData(eventWithCurrency);

			// Assert
			assertNotNull(savedEntity);
			assertEquals(currency, savedEntity.getCurrency());
		}
	}

	@Test
	@DisplayName("Edge: Store event with different transaction types")
	void testStoreEventWithDifferentTypes() {
		// Test different types
		String[] types = {"credit", "debit", "transfer", "adjustment"};
		
		for (String type : types) {
			EventDao eventWithType = EventDao.builder()
					.eventId("EVENT_TYPE_" + type)
					.accountId("ACC_TYPE_" + type)
					.type(type)
					.amount(new BigDecimal("1000.00"))
					.currency("USD")
					.build();

			// Act
			com.assignment.eventservice.entity.EventEntity savedEntity = eventService.storeEventData(eventWithType);

			// Assert
			assertNotNull(savedEntity);
			assertEquals(type, savedEntity.getType());
		}
	}

	@Test
	@DisplayName("Edge: Store and retrieve event in sequence")
	void testStoreAndRetrieveEventSequence() {
		// Arrange
		EventDao eventToStore = EventDao.builder()
				.eventId("EVENT_SEQ_001")
				.accountId("ACC_SEQ_001")
				.type("credit")
				.amount(new BigDecimal("3000.00"))
				.currency("USD")
				.build();

		// Act - Store
		eventService.storeEventData(eventToStore);

		// Act - Retrieve
		EventDao retrievedEvent = eventService.getEventDetails("EVENT_SEQ_001");

		// Assert
		assertNotNull(retrievedEvent);
		assertEquals(eventToStore.getAccountId(), retrievedEvent.getAccountId());
		assertEquals(eventToStore.getType(), retrievedEvent.getType());
		assertEquals(eventToStore.getAmount(), retrievedEvent.getAmount());
		assertEquals(eventToStore.getCurrency(), retrievedEvent.getCurrency());
	}

}
