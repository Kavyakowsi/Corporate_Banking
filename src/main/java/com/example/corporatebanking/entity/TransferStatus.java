package com.example.corporatebanking.entity;

/**
 * Lifecycle status of a FundTransfer. Notice this is purely a business/
 * workflow status - it has nothing to do with RBC role codes.
 */
public enum TransferStatus {
    PENDING_APPROVAL,
    APPROVED,
    REJECTED
}
