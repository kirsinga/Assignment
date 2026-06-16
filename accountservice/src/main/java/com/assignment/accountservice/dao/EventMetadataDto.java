package com.assignment.accountservice.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventMetadataDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Source is required")
    @Size(max = 50, message = "Source cannot exceed 50 characters")
    private String source;

    @NotBlank(message = "Batch Id is required")
    @Size(max = 100, message = "Batch Id cannot exceed 100 characters")
    private String batchId;

    private String eventId;
}