package com.assignment.eventservice.dao;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMetadataDao {

    private Long id;
    private String source;
    private String batchId;
    private String eventId;
}