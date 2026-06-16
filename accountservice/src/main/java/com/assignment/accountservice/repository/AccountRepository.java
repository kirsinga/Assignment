package com.assignment.accountservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.accountservice.entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    // Get all transactions for account
    List<AccountEntity> findByAccountId(String accountId);

    // Get transactions sorted (important for history API)
    List<AccountEntity> findByAccountIdOrderByEventTimestampDesc(String accountId);

    // Pagination support (BEST PRACTICE)
    Page<AccountEntity> findByAccountId(String accountId, Pageable pageable);

    // Get latest transaction
    Optional<AccountEntity> findTopByAccountIdOrderByEventTimestampDesc(String accountId);

    // Get specific transaction
    Optional<AccountEntity> findByEventId(String eventId);
}