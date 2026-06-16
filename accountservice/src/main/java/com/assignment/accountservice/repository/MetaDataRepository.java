package com.assignment.accountservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.accountservice.entity.EventMetadataEntity;

public interface MetaDataRepository extends JpaRepository<EventMetadataEntity, Long> {

    // ✅ Correct: navigate relationship
	List<EventMetadataEntity> findByAccountId(String accountId);
    Optional<EventMetadataEntity> findByEventId(String eventId);
    


    
}