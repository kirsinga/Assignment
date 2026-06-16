package com.assignment.eventservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.eventservice.entity.EventMetadataEntity;

public interface MetaDataRepository
        extends JpaRepository<EventMetadataEntity, Long> {

    List<EventMetadataEntity> findByEventId(String eventId);
}