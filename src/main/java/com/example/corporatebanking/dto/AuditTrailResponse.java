package com.example.corporatebanking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditTrailResponse {
    private Long id;
    private String eventId;
    private String username;
    private String role;
    private String action;
    private String module;
    private String entityType;
    private String entityId;
    private BigDecimal amount;
    private String status;
    private String failureReason;
    private LocalDateTime timestamp;
}
