package com.example.corporatebanking.service;

import com.example.audittrail.service.AuditTrailService;
import com.example.corporatebanking.dto.FundTransferRequest;
import com.example.corporatebanking.dto.FundTransferResponse;
import com.example.corporatebanking.entity.FundTransfer;
import com.example.corporatebanking.entity.TransferStatus;
import com.example.corporatebanking.exception.ResourceNotFoundException;
import com.example.corporatebanking.repository.BeneficiaryRepository;
import com.example.corporatebanking.repository.FundTransferRepository;
import com.example.rbcaccess.dto.AccessCheckResult;
import com.example.rbcaccess.exception.RbcAuthorizationException;
import com.example.rbcaccess.service.RbcAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Exactly the pattern shown in the requirements:
 *
 * <pre>
 * rbcAccessService.checkAccess(username, "CREATE_FUND_TRANSFER", amount, null);
 * </pre>
 *
 * If it throws, the transfer is never created and a FAILED audit record is
 * written. If it succeeds, the transfer is created and a SUCCESS audit
 * record is written. Corporate Banking never inspects amount thresholds
 * itself - rbc-access already validated the amount against
 * RBC_PERMISSION_LIMIT before returning.
 */
@Service
@RequiredArgsConstructor
public class FundTransferService {

    private static final String PERMISSION_CREATE_TRANSFER = "CREATE_FUND_TRANSFER";
    private static final String MODULE = "FUND_TRANSFER";

    private final RbcAccessService rbcAccessService;
    private final AuditTrailService auditTrailService;
    private final FundTransferRepository fundTransferRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    @Transactional
    public FundTransferResponse createTransfer(FundTransferRequest request) {
        String username = request.getUsername();

        if (!beneficiaryRepository.existsById(request.getBeneficiaryId())) {
            throw new ResourceNotFoundException("Beneficiary " + request.getBeneficiaryId() + " not found");
        }

        AccessCheckResult access;
        try {
            // Before creating a transfer - ask RBC. transactionCreatedBy is null
            // here because this IS the creation step, not an approval.
            access = rbcAccessService.checkAccess(username, PERMISSION_CREATE_TRANSFER, request.getAmount(), null);
        } catch (RbcAuthorizationException ex) {
            auditTrailService.logFailure(username, null, PERMISSION_CREATE_TRANSFER, MODULE,
                    "FUND_TRANSFER", null, request.getAmount(), ex.getMessage());
            throw ex;
        }

        FundTransfer transfer = FundTransfer.builder()
                .transactionRef("TX" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .beneficiaryId(request.getBeneficiaryId())
                .amount(request.getAmount())
                .currency(request.getCurrency() == null ? "INR" : request.getCurrency())
                .status(TransferStatus.PENDING_APPROVAL)
                .createdBy(username)
                .createdAt(LocalDateTime.now())
                .build();
        transfer = fundTransferRepository.save(transfer);

        auditTrailService.logSuccess(username, access.getMatchedRoleCode(), PERMISSION_CREATE_TRANSFER,
                MODULE, "FUND_TRANSFER", String.valueOf(transfer.getId()), request.getAmount());

        return toResponse(transfer);
    }

    @Transactional(readOnly = true)
    public FundTransferResponse getByRef(String transactionRef) {
        return toResponse(fundTransferRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Fund transfer '" + transactionRef + "' not found")));
    }

    public FundTransferResponse toResponse(FundTransfer t) {
        return FundTransferResponse.builder()
                .id(t.getId())
                .transactionRef(t.getTransactionRef())
                .beneficiaryId(t.getBeneficiaryId())
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .status(t.getStatus())
                .createdBy(t.getCreatedBy())
                .createdAt(t.getCreatedAt())
                .approvedBy(t.getApprovedBy())
                .approvedAt(t.getApprovedAt())
                .build();
    }

    public List<FundTransferResponse> getAll() {

        return fundTransferRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
