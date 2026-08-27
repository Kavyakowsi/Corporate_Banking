package com.example.corporatebanking.service;

import com.example.audittrail.service.AuditTrailService;
import com.example.corporatebanking.dto.BeneficiaryRequest;
import com.example.corporatebanking.dto.BeneficiaryResponse;
import com.example.corporatebanking.entity.Beneficiary;
import com.example.corporatebanking.repository.BeneficiaryRepository;
import com.example.rbcaccess.dto.AccessCheckResult;
import com.example.rbcaccess.exception.RbcAuthorizationException;
import com.example.rbcaccess.service.RbcAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notice this class contains ZERO references to "MAKER_1", "MAKER_2",
 * "MAKER_3" or any amount threshold. It only asks rbc-access the generic
 * question "does this user have CREATE_BENEFICIARY?" and records the
 * outcome via audit-trail. Both libraries are plain constructor-injected
 * Spring beans coming from the rbc-access and audit-trail JARs on the
 * classpath.
 */
@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private static final String PERMISSION_CREATE_BENEFICIARY = "CREATE_BENEFICIARY";
    private static final String MODULE = "BENEFICIARY";

    private final RbcAccessService rbcAccessService;
    private final AuditTrailService auditTrailService;
    private final BeneficiaryRepository beneficiaryRepository;

    @Transactional
    public BeneficiaryResponse createBeneficiary(BeneficiaryRequest request) {
        String username = request.getUsername();

        AccessCheckResult access;
        try {
            // 1) Ask the RBC library - no hard-coded role/permission logic here.
            access = rbcAccessService.checkAccess(username, PERMISSION_CREATE_BENEFICIARY, null, null);
        } catch (RbcAuthorizationException ex) {
            // 2) Record the denial via the audit-trail library, then propagate
            //    so GlobalExceptionHandler can build the HTTP response.
            auditTrailService.logDenied(username, null, PERMISSION_CREATE_BENEFICIARY, MODULE,
                    "BENEFICIARY", null, null, ex.getMessage());
            throw ex;
        }

        // 3) Authorization succeeded - perform the actual banking operation.
        Beneficiary beneficiary = Beneficiary.builder()
                .customerId(request.getCustomerId())
                .beneficiaryName(request.getBeneficiaryName())
                .beneficiaryAccountNumber(request.getBeneficiaryAccountNumber())
                .bankIfsc(request.getBankIfsc())
                .createdBy(username)
                .build();
        beneficiary = beneficiaryRepository.save(beneficiary);

        // 4) Record success.
        auditTrailService.logSuccess(username, access.getMatchedRoleCode(), PERMISSION_CREATE_BENEFICIARY,
                MODULE, "BENEFICIARY", String.valueOf(beneficiary.getId()), null);

        return toResponse(beneficiary);
    }

    private BeneficiaryResponse toResponse(Beneficiary b) {
        return BeneficiaryResponse.builder()
                .id(b.getId())
                .customerId(b.getCustomerId())
                .beneficiaryName(b.getBeneficiaryName())
                .beneficiaryAccountNumber(b.getBeneficiaryAccountNumber())
                .bankIfsc(b.getBankIfsc())
                .createdBy(b.getCreatedBy())
                .build();
    }
}
