package com.assignment.eventservice.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "event"
    
)
@Getter
@Setter
public class EventEntity {

    @Id
    @Column(name = "event_id", nullable = false, length = 50)
    private String eventId;

    @Column(name = "account_id", nullable = false, length = 50)
    private String accountId;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @OneToOne(
        
        cascade = CascadeType.ALL
     
    )
    @JoinColumn(name = "meta_id")
    private EventMetadataEntity metadata;
}