package com.example.corporatebanking.controller;

import com.example.corporatebanking.dto.ApprovalRequest;
import com.example.corporatebanking.dto.ApprovalResponse;
import com.example.corporatebanking.service.ApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApprovalResponse> approve(
            @PathVariable("id") String fundTransferId,
            @Valid @RequestBody ApprovalRequest request) {

        return ResponseEntity.ok(
                approvalService.approve(parseFundTransferId(fundTransferId), request)
        );
    }

        @PostMapping("/approve")
        public ResponseEntity<ApprovalResponse> approveFromBody(
            @Valid @RequestBody ApprovalRequest request) {

        return ResponseEntity.ok(
            approvalService.approve(parseFundTransferId(request.getFundTransferId()), request)
        );
        }

    private Long parseFundTransferId(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            throw new MethodArgumentTypeMismatchException(value, Long.class, "id", null, ex);
        }
    }
}
