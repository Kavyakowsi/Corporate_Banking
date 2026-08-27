package com.example.corporatebanking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRbcRoleRequest {

    @NotBlank
    private String roleCode;

    @NotBlank
    private String roleName;

    @NotBlank
    private String roleType;
}