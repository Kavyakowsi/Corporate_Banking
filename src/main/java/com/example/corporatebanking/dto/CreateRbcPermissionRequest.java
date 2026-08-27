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
public class CreateRbcPermissionRequest {

    @NotBlank
    private String permissionCode;

    @NotBlank
    private String permissionName;
}