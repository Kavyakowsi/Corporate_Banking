package com.example.corporatebanking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetPermissionLimitRequest {

    @NotBlank
    private String roleCode;

    @NotBlank
    private String permissionCode;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal maxAmount;

    @NotBlank
    private String currency;
}