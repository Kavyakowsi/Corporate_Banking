package com.example.corporatebanking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Uniform error envelope returned for every ACCESS_DENIED / LIMIT_EXCEEDED /
 * SELF_APPROVAL_NOT_ALLOWED / validation / not-found failure.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private String status;   // "DENIED" or "ERROR"
    private String code;     // e.g. ACCESS_DENIED, LIMIT_EXCEEDED, SELF_APPROVAL_NOT_ALLOWED
    private String message;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
