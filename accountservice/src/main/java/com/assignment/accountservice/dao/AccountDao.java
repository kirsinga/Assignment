package com.assignment.accountservice.dao;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDao {

    /**
     * Generated internally by system
     */
	@NotNull(message = "Event ID can not be Null")
    private String eventId;

    @NotBlank(message = "Account ID cannot be blank")
    private String accountId;

    @NotBlank(message = "Transaction type is required")
    private String type;

    @NotNull(message = "Amount cannot be NULL")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    @NotNull(message = "Event timestamp is required")
    private Instant eventTimestamp;

    @Valid
    private EventMetadataDto metadata;
}