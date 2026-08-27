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
public class FundTransferResponse {
    private Long id;
    private String transactionRef;
    private Long beneficiaryId;
    private BigDecimal amount;
    private String currency;
    private TransferStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
}
