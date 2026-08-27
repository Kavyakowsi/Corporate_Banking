package com.example.corporatebanking.controller;

import com.example.corporatebanking.dto.BeneficiaryRequest;
import com.example.corporatebanking.dto.BeneficiaryResponse;
import com.example.corporatebanking.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> create(@Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse response = beneficiaryService.createBeneficiary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
