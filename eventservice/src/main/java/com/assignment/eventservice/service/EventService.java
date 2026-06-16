package com.assignment.eventservice.service;


import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.eventservice.client.AccountServiceClient;
import com.assignment.eventservice.dao.EventDao;
import com.assignment.eventservice.dao.EventMetadataDao;
import com.assignment.eventservice.dao.EventResponseDao;
import com.assignment.eventservice.entity.EventEntity;
import com.assignment.eventservice.entity.EventMetadataEntity;
import com.assignment.eventservice.repository.EventRepository;
import com.assignment.eventservice.repository.MetaDataRepository;


/**
 * Service class to handle event details processing.
 * Communicates with AccountService via Feign client to post transactions.
 * Uses Eureka Service Registry for dynamic service discovery.
 */
@Service
public class EventService {

	private static final Logger logger = LoggerFactory.getLogger(EventService.class);
	private AccountServiceClient accountServiceClient;
	private EventRepository eventResponsitory;
	private MetaDataRepository metaDataRepository;

	public EventService(AccountServiceClient accountServiceClient, EventRepository eventResponsitory,
			MetaDataRepository metaDataRepository) {
		this.accountServiceClient = accountServiceClient;
		this.eventResponsitory = eventResponsitory;
		this.metaDataRepository = metaDataRepository;
	}
   
    
    
    

    
   /**
	 * Posts event information to the AccountService. This method receives event
	 * details and forwards the transaction to the account service for processing
	 * via Feign client with service discovery.
	 *
	 * @param eventDao the event data containing transaction details
	 * @return response entity with the processing result
	 */
	public ResponseEntity<EventResponseDao> postEventInfo(EventDao eventDao) {

		if (eventDao == null) {
			logger.error("Event data is null");
			throw new RuntimeException("Event data cannot be null");
		}

		if (eventDao.getAccountId() == null || eventDao.getAccountId().trim().isEmpty()) {
			logger.error("Account ID is missing in event data");
			throw new RuntimeException("Account ID is missing in event data");
			
		}

		try {
			logger.info("Posting event to account service for account: {}", eventDao.getAccountId());
			
			// Pass the caller method name as a header so AccountService can know the origin
			String methodName = "postEventInfo";
			ResponseEntity<EventResponseDao> accountResponse= accountServiceClient
					.postAccountTransaction(methodName, eventDao.getAccountId(), eventDao);
			
			 
			 
					
			// Store event data (includes save operation)
			EventEntity eventEntity=storeEventData(eventDao);
					
			
			
			if (accountResponse != null) {
				logger.info("Received response from account service: {}",accountResponse );
				return accountResponse;
			} else {
				logger.error("No response body from account service");
				
			}
			
			return accountResponse;

		} catch (Exception e) {
			logger.error("Failed to post transaction to account service: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to post transaction to account service: " + e.getMessage());
		}

	}

    /**
     * Fetch account information from the AccountService and return it wrapped in EventResponseDao.
     * If the Feign-mapped response is missing transaction details, try a raw JSON fetch and map it manually.
     */
    public EventResponseDao getAccountHistory(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            logger.error("Account ID is null or empty");
            throw new RuntimeException("Account ID cannot be null or empty");
        }

        try {
            logger.info("Fetching account info for account: {}", accountId);

           
            try {
                EventResponseDao eventResponseDao = accountServiceClient.getAccountHistory(accountId);
                if (eventResponseDao != null && eventResponseDao.getAccountHistory() != null ) {
                    logger.info("Successfully fetched account info for account: {}", accountId);
                    return eventResponseDao;
                }
            } catch (Exception ex) {
                logger.error("Failed to fetch account info from service: {}", ex.getMessage(), ex);
                throw new RuntimeException("Server Exception: " + ex.getMessage());
            }

            logger.warn("No account details found for account: {}", accountId);
            return EventResponseDao.builder()
                    .status("ERROR")
                    .message("No account details found")
                    .build();
                    
        } catch (Exception e) {
            logger.error("Failed to fetch account info: {}", e.getMessage(), e);
            throw new RuntimeException("ACCOUNT_SERVICE_ERROR: " + e.getMessage());
        }
    }
    
    
    
    @Transactional
    public EventEntity storeEventData(EventDao eventDao) {
    	
    	EventEntity entity = new EventEntity();
		String eventId = eventDao.getEventId() != null ? eventDao.getEventId() : UUID.randomUUID().toString();
		entity.setEventId(eventId);
		entity.setAccountId(eventDao.getAccountId());
		entity.setType(eventDao.getType());
		entity.setAmount(eventDao.getAmount());
		entity.setCurrency(eventDao.getCurrency());
		entity.setEventTimestamp(
				eventDao.getEventTimestamp() != null ? eventDao.getEventTimestamp() : Instant.now());

		// previous mapping of metadata - ensure we set both sides of the relationship
		if (eventDao.getMetadata() != null) {
			
			EventMetadataEntity metaData =
					EventMetadataEntity.builder()
					.source(eventDao.getMetadata().getSource())
					.batchId(eventDao.getMetadata().getBatchId()).eventId(eventId) // set owning side so join column is
																					// populated
					.build();
			// set both sides to keep the object graph consistent
			entity.setMetadata(metaData);
		} else {
			entity.setMetadata(null);
		}

		// Save entity (cascades metadata)
		EventEntity saved = eventResponsitory.save(entity);
	
		
		
		return saved;
     }
    
    
  public EventDao getEventDetails(String eventId) {
		
	 EventEntity eventEntity= eventResponsitory.findById(eventId).orElseThrow(()-> new RuntimeException("Event not found for id: "+eventId));
	 
	return EventDao.builder()
	 .eventId(eventEntity.getEventId())
	 .accountId(eventEntity.getAccountId())
	 .type(eventEntity.getType())
	 .amount(eventEntity.getAmount())
	 .currency(eventEntity.getCurrency())
	 .eventTimestamp(eventEntity.getEventTimestamp())
	 .metadata(eventEntity
			 .getMetadata() != null ?  EventMetadataDao.builder().batchId(eventEntity.getMetadata().getBatchId()).source(eventEntity.getMetadata().getSource()).build() : null).build();
	 
	
	  
  }

  /**
   * Retrieves event information by event ID and returns it as EventResponseDao.
   * 
   * @param eventId the ID of the event to retrieve
   * @return EventResponseDao containing the event details
   */
  public EventDao getEventInfo(String eventId) {
	  if (eventId == null || eventId.trim().isEmpty()) {
		  logger.error("Event ID is null or empty");
		  throw new RuntimeException("Event ID cannot be null or empty");
	  }
	  
	  try {
		  logger.info("Fetching event info for event ID: {}", eventId);
		  EventDao eventDao = getEventDetails(eventId);
		  
		  
		  
		  logger.info("Successfully retrieved event info for event ID: {}", eventId);
		  return eventDao;
	  } catch (Exception e) {
		  logger.error("Failed to fetch event info for event ID: {}: {}", eventId, e.getMessage(), e);
		  throw new RuntimeException("Failed to fetch event info: " + e.getMessage());
	  }
  }
  
  public EventResponseDao getAccountBalence(String accountId) {
	  if (accountId == null || accountId.trim().isEmpty()) {
		  logger.error("Account ID is null or empty");
		  throw new RuntimeException("Account ID cannot be null or empty");
	  }
	  
	  try {
		  logger.info("Fetching event history for account ID: {}", accountId);
		  // This method would need to be implemented in the repository to fetch events by account ID
		  EventResponseDao balenceDao = accountServiceClient.getbanlence(accountId);
		  
		
		  
		  logger.info("Successfully retrieved event history for account ID: {}", accountId);
		  return EventResponseDao.builder()
				  .status("SUCCESS")
				  .message("Successfully retive the balence")
				  .balance(balenceDao.getBalance())
				  .build();
	  } catch (Exception e) {
		  logger.error("Failed to fetch event history for account ID: {}: {}", accountId, e.getMessage(), e);
		  throw new RuntimeException("Failed to fetch event history: " + e.getMessage());
	  }
  }
}