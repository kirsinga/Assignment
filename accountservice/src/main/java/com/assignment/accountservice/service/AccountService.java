package com.assignment.accountservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.accountservice.dao.AccountDao;
import com.assignment.accountservice.dao.AccountResponse;
import com.assignment.accountservice.dao.BalanceDao;
import com.assignment.accountservice.dao.EventMetadataDto;
import com.assignment.accountservice.entity.AccountEntity;
import com.assignment.accountservice.entity.EventMetadataEntity;
import com.assignment.accountservice.repository.AccountRepository;
import com.assignment.accountservice.repository.MetaDataRepository;

@Service
public class AccountService {

    private  AccountRepository accountRepository;
    private  MetaDataRepository metaDataRepository;
    

    public AccountService(AccountRepository accountRepository,MetaDataRepository metaDataRepository) {
        this.accountRepository = accountRepository;
        this.metaDataRepository=metaDataRepository;
    }
    
    
    /**
     * Create transaction
     */
    @Transactional
    public AccountResponse postAccount(AccountDao accountDao) {

        if (accountDao == null) {
            return AccountResponse.failure(
                    "INVALID_REQUEST",
                    "Request body is null");
        }
         AccountEntity accEntity=   accountRepository.findByEventId(accountDao.getEventId()).orElse(null);
         EventMetadataEntity metaEntity= metaDataRepository.findByEventId(accountDao.getEventId()).orElse(null);
                          
              if(accEntity!=null) {
        	 if(accEntity.getEventId().equals(accountDao.getEventId())) {
        	 
        	 AccountDao accountDaoRep= AccountDao.builder().accountId(accEntity.getAccountId()).amount(accEntity.getAmount()).eventId(accEntity.getEventId()).currency(accEntity.getCurrency()).
        			 eventTimestamp(accEntity.getEventTimestamp()).build();
        	 accountDaoRep.setMetadata(EventMetadataDto.builder().batchId(metaEntity.getBatchId()).source(metaEntity.getSource()).eventId(metaEntity.getEventId()).build());
        return	 AccountResponse.builder()
                     .status(AccountResponse.Status.SUCCESS)
                     .account(accountDaoRep)
                     .build();
        	 }
           
         }
         
        AccountEntity entity = AccountEntity.builder()
                .eventId(Optional.ofNullable(accountDao.getEventId())
                        .orElse(UUID.randomUUID().toString()))
                .accountId(accountDao.getAccountId())
                .type(accountDao.getType())
                .amount(accountDao.getAmount())
                .currency(accountDao.getCurrency())
                .eventTimestamp(Optional.ofNullable(accountDao.getEventTimestamp())
                        .orElse(Instant.now()))
                .build();
        

        // metadata mapping (if present)
        if (accountDao.getMetadata() != null) {
        	EventMetadataEntity metaData=EventMetadataEntity.builder().batchId(accountDao.getMetadata().getBatchId()).source(accountDao.getMetadata().getSource()).eventId(accountDao.getEventId()).accountId(accountDao.getAccountId()).build();
        	metaDataRepository.save(metaData);
        }
        

        accountRepository.save(entity);
        
        return AccountResponse.success(
                "Transaction completed successfully",
                accountDao
        );
    }

    /**
     * Get Account Balance
     */
    public AccountResponse getAccountBalance(String accountId) {

        List<AccountEntity> transactions =
                accountRepository.findByAccountId(accountId);
        
       
       
       

        if (transactions == null || transactions.isEmpty()) {
            return AccountResponse.failure(
                    "NOT_FOUND",
                    "No transactions found for accountId: " + accountId);
        }

        BigDecimal balance = BigDecimal.ZERO;
        String currency = transactions.get(0).getCurrency();

        for (AccountEntity tx : transactions) {

            BigDecimal amount =
                    tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO;

            if ("credit".equalsIgnoreCase(tx.getType())) {
                balance = balance.add(amount);
            } else if ("debit".equalsIgnoreCase(tx.getType())) {
                balance = balance.subtract(amount);
            }
        }

        BalanceDao balanceDao = BalanceDao.builder()
                .accountId(accountId)
                .balance(balance)
                .currency(currency)
                .build();

        return AccountResponse.builder()
                .status(AccountResponse.Status.SUCCESS)
                .balance(balanceDao)
                .build();
    }

    /**
     * Get Account History
     */
    public AccountResponse getAccountHistory(String accountId) {

        List<AccountEntity> transactions =
                accountRepository.findByAccountId(accountId);
        
        List<EventMetadataEntity> metatranSaction =  metaDataRepository.findByAccountId(accountId);
        

        if (transactions == null || transactions.isEmpty()) {
            return AccountResponse.failure(
                    "NOT_FOUND",
                    "No transactions found for accountId: " + accountId);
        }
        

        // Sort DESC by timestamp (latest first)
        transactions = transactions.stream()
                .sorted(Comparator
                        .comparing(AccountEntity::getEventTimestamp)
                        .reversed())
                .toList();
        
        //convert AccountEntity to AccountDao 
        List<AccountDao> accountHistory = transactions.stream()
				.map(tx -> AccountDao.builder()
						.eventId(tx.getEventId())
						.accountId(tx.getAccountId())
						.type(tx.getType())
						.amount(tx.getAmount())
						.currency(tx.getCurrency())
						.eventTimestamp(tx.getEventTimestamp())
						
						.build())
				.toList();
        List<AccountDao> accountDaoList=new ArrayList<AccountDao>();
        for(AccountDao dao:accountHistory) {
        	 for(EventMetadataEntity metaMetaEvent:metatranSaction) {
        		 if(dao.getEventId().equals(metaMetaEvent.getEventId())) {
        			 dao.setMetadata(EventMetadataDto.builder().batchId(metaMetaEvent.getBatchId()).eventId(metaMetaEvent.getEventId()).source(metaMetaEvent.getSource()).build()); 
        			 accountDaoList.add(dao);
        		 }
        			 
        	 }
        	  
        }
        
        

       
        
     return   AccountResponse.builder()
        .status(AccountResponse.Status.SUCCESS)
        .accountHistory(accountDaoList)
        .build();
    }
}