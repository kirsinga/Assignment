package com.assignment.eventservice.dao;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDao {

	    private String status;
	    private ErrorResponse errors;
	    private AccountDao account;
	    private BalanceDao balance;
	    private String message;
	    private List<AccountDao> accountHistory;
	    
	  		
	
}

