package com.example.corporatebanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryRequest {

    @NotBlank
    private String username; // acting RBC user, e.g. "maker1"

    @NotNull
    private Long customerId;

    @NotBlank
    private String beneficiaryName;

    @NotBlank
    private String beneficiaryAccountNumber;

    private String bankIfsc;
}
