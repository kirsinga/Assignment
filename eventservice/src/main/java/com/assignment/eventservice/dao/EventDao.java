package com.assignment.eventservice.dao;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDao {

    private String eventId;

    @NotBlank(message = "Account ID cannot be blank")
    private String accountId;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "credit|debit", message = "Type must be credit or debit")
    private String type;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be blank")
    @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
    private String currency;

    private Instant eventTimestamp;

    @Valid
    private EventMetadataDao metadata;
}