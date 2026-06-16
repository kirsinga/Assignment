package com.assignment.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "account_metadata"
    
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

    @Column(nullable = false, length = 50)
    private String source;

    @Column(nullable = false, length = 100)
    private String batchId;
    
    @Column(nullable = false, length = 100)
    private String eventId;
    
    @Column(nullable = false, length = 100)
    private String accountId;
  
   //rivate AccountEntity account;
}