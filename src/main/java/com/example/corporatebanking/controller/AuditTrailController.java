package com.example.corporatebanking.controller;

import com.example.corporatebanking.dto.AuditTrailResponse;
import com.example.corporatebanking.service.AuditQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-trails")
@RequiredArgsConstructor
public class AuditTrailController {

    private final AuditQueryService auditQueryService;

    @GetMapping
    public ResponseEntity<Page<AuditTrailResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(auditQueryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditTrailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(auditQueryService.findById(id));
    }
}
