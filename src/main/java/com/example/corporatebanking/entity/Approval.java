package com.example.corporatebanking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One row per approval ATTEMPT (successful or rejected) against a
 * FundTransfer, kept in the Corporate Banking schema for business
 * reporting. The authoritative, tamper-evident log of every attempt
 * (including denied ones that never even reach this table) lives in
 * AUDIT_TRAIL, owned by the audit-trail library.
 */
@Entity
@Table(name = "APPROVAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fund_transfer_id", nullable = false)
    private Long fundTransferId;

    @Column(name = "approver", nullable = false, length = 100)
    private String approver;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransferStatus status;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;
}
