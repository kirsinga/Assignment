package com.assignment.eventservice.dao;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class AccountDao {

	private String eventId;

	private String accountId;

	private String type;
	
	private BigDecimal amount;

	private String currency;

	private Instant eventTimestamp;

	private EventMetadataDao metadata;
}