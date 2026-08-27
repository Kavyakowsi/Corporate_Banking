package com.example.corporatebanking.dto;

import com.example.corporatebanking.entity.TransferStatus;
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
public class ApprovalResponse {
    private Long fundTransferId;
    private String transactionRef;
    private String approver;
    private BigDecimal amount;
    private TransferStatus status;
    private LocalDateTime decidedAt;
}
