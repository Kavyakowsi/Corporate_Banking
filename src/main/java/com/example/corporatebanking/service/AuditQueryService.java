package com.example.corporatebanking.service;

import com.example.audittrail.entity.AuditTrail;
import com.example.audittrail.service.AuditTrailService;
import com.example.corporatebanking.dto.AuditTrailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Corporate Banking only ever READS audit data through
 * AuditTrailService - it never queries the AUDIT_TRAIL table directly,
 * since that table (and its repository) belong to the audit-trail library.
 */
@Service
@RequiredArgsConstructor
public class AuditQueryService {

    private final AuditTrailService auditTrailService;

    public Page<AuditTrailResponse> findAll(Pageable pageable) {
        return auditTrailService.findAll(pageable).map(this::toResponse);
    }

    public AuditTrailResponse findById(Long id) {
        return toResponse(auditTrailService.findById(id));
    }

    private AuditTrailResponse toResponse(AuditTrail a) {
        return AuditTrailResponse.builder()
                .id(a.getId())
                .eventId(a.getEventId())
                .username(a.getUsername())
                .role(a.getRole())
                .action(a.getAction())
                .module(a.getModule())
                .entityType(a.getEntityType())
                .entityId(a.getEntityId())
                .amount(a.getAmount())
                .status(a.getStatus().name())
                .failureReason(a.getFailureReason())
                .timestamp(a.getTimestamp())
                .build();
    }
}
