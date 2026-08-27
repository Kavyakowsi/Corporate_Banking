package com.example.corporatebanking.controller;

import com.example.corporatebanking.dto.FundTransferRequest;
import com.example.corporatebanking.dto.FundTransferResponse;
import com.example.corporatebanking.service.FundTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class FundTransferController {

    private final FundTransferService fundTransferService;

    @PostMapping
    public ResponseEntity<FundTransferResponse> create(@Valid @RequestBody FundTransferRequest request) {
        FundTransferResponse response = fundTransferService.createTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{transactionRef}")
    public ResponseEntity<FundTransferResponse> getByRef(@PathVariable String transactionRef) {
        return ResponseEntity.ok(fundTransferService.getByRef(transactionRef));
    }

    @GetMapping
    public ResponseEntity<List<FundTransferResponse>> getAll() {
        return ResponseEntity.ok(fundTransferService.getAll());
    }
}
