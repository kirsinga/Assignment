package com.assignment.accountservice.dao;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BalanceDao {

	
	private String accountId;
	private String currency;
	private BigDecimal balance;
	
}
