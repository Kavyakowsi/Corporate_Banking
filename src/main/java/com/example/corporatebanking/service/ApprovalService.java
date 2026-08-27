package com.example.corporatebanking.service;

import com.example.audittrail.service.AuditTrailService;
import com.example.corporatebanking.dto.ApprovalRequest;
import com.example.corporatebanking.dto.ApprovalResponse;
import com.example.corporatebanking.entity.Approval;
import com.example.corporatebanking.entity.FundTransfer;
import com.example.corporatebanking.entity.TransferStatus;
import com.example.corporatebanking.exception.ResourceNotFoundException;
import com.example.corporatebanking.repository.ApprovalRepository;
import com.example.corporatebanking.repository.FundTransferRepository;
import com.example.rbcaccess.dto.AccessCheckResult;
import com.example.rbcaccess.exception.RbcAuthorizationException;
import com.example.rbcaccess.service.RbcAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implements the "VERY IMPORTANT: MAKER-CHECKER SEGREGATION" requirement.
 *
 * The approving user is checked against the transfer's createdBy purely by
 * passing both usernames into rbcAccessService.checkAccess(...). This class
 * never contains "if (approver.equals(\"stp\"))" or any other hard-coded
 * role comparison - the generic SELF_APPROVAL_ALLOWED policy inside
 * rbc-access does all the work, so the exact same code path denies STP
 * approving its own transfer AND would deny any other role configured the
 * same way, with zero changes here.
 */
@Service
@RequiredArgsConstructor
public class ApprovalService {

    private static final String PERMISSION_APPROVE_TRANSFER = "APPROVE_FUND_TRANSFER";
    private static final String MODULE = "APPROVAL";

    private final RbcAccessService rbcAccessService;
    private final AuditTrailService auditTrailService;
    private final FundTransferRepository fundTransferRepository;
    private final ApprovalRepository approvalRepository;

    @Transactional
    public ApprovalResponse approve(Long fundTransferId, ApprovalRequest request) {
        String checkerUsername = request.getUsername();

        FundTransfer transfer = fundTransferRepository.findById(fundTransferId)
                .orElseThrow(() -> new ResourceNotFoundException("Fund transfer " + fundTransferId + " not found"));

        if (transfer.getStatus() != TransferStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Fund transfer " + fundTransferId + " is already " + transfer.getStatus());
        }

        AccessCheckResult access;
        try {
            // rbcAccessService.checkAccess(checkerUsername, "APPROVE_FUND_TRANSFER", amount, createdBy)
            // - verifies permission, approval limit, AND self-approval policy in one call.
            access = rbcAccessService.checkAccess(
                    checkerUsername, PERMISSION_APPROVE_TRANSFER, transfer.getAmount(), transfer.getCreatedBy());
        } catch (RbcAuthorizationException ex) {
            auditTrailService.logFailure(checkerUsername, null, PERMISSION_APPROVE_TRANSFER, MODULE,
                    "FUND_TRANSFER", String.valueOf(transfer.getId()), transfer.getAmount(), ex.getMessage());
            throw ex;
        }

        TransferStatus newStatus =
                "true".equalsIgnoreCase(request.getApprove())
                        ? TransferStatus.APPROVED
                        : TransferStatus.REJECTED;
        transfer.setStatus(newStatus);
        transfer.setApprovedBy(checkerUsername);
        transfer.setApprovedAt(LocalDateTime.now());
        fundTransferRepository.save(transfer);

        Approval approval = Approval.builder()
                .fundTransferId(transfer.getId())
                .approver(checkerUsername)
                .amount(transfer.getAmount())
                .status(newStatus)
                .decidedAt(LocalDateTime.now())
                .build();
        approvalRepository.save(approval);

        if (Boolean.parseBoolean(request.getApprove())) {
            auditTrailService.logApproval(checkerUsername, access.getMatchedRoleCode(),
                    "FUND_TRANSFER", String.valueOf(transfer.getId()), transfer.getAmount());
        } else {
            auditTrailService.logSuccess(checkerUsername, access.getMatchedRoleCode(), "REJECT_FUND_TRANSFER",
                    MODULE, "FUND_TRANSFER", String.valueOf(transfer.getId()), transfer.getAmount());
        }

        return ApprovalResponse.builder()
                .fundTransferId(transfer.getId())
                .transactionRef(transfer.getTransactionRef())
                .approver(checkerUsername)
                .amount(transfer.getAmount())
                .status(newStatus)
                .decidedAt(transfer.getApprovedAt())
                .build();
    }
}
