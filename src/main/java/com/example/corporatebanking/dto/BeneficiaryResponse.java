package com.example.corporatebanking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryResponse {
    private Long id;
    private Long customerId;
    private String beneficiaryName;
    private String beneficiaryAccountNumber;
    private String bankIfsc;
    private String createdBy;
}
