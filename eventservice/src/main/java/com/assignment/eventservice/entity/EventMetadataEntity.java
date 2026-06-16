package com.assignment.eventservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "event_metadata"
    
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String batchId;
    private String eventId;

    

  
}