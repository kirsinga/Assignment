package com.assignment.accountservice.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

	@Id
	@Column(name = "event_id", nullable = false, length = 100)
	private String eventId;

    @Column(nullable = false, length = 50)
    private String accountId;

    @Column(nullable = false, length = 20)
    private String type;

    private BigDecimal amount;

 
    private String currency;

    @Column(nullable = false)
    private Instant eventTimestamp;

    
   // private EventMetadataEntity metadata;
}