package com.example.corporatebanking.controller;

import com.example.corporatebanking.dto.*;
import com.example.corporatebanking.service.AdminRbcService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/rbc")
@RequiredArgsConstructor
public class AdminRbcController {

    private final AdminRbcService adminRbcService;

    // ============================================================
    // USERS
    // ============================================================

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody CreateRbcUserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminRbcService.createUser(request));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {

        return ResponseEntity.ok(
                adminRbcService.getUsers()
        );
    }


    // ============================================================
    // ROLES
    // ============================================================

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(
            @Valid @RequestBody CreateRbcRoleRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminRbcService.createRole(request));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {

        return ResponseEntity.ok(
                adminRbcService.getRoles()
        );
    }


    // ============================================================
    // PERMISSIONS
    // ============================================================

    @PostMapping("/permissions")
    public ResponseEntity<?> createPermission(
            @Valid @RequestBody CreateRbcPermissionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminRbcService.createPermission(request));
    }

    @GetMapping("/permissions")
    public ResponseEntity<?> getPermissions() {

        return ResponseEntity.ok(
                adminRbcService.getPermissions()
        );
    }


    // ============================================================
    // USER -> ROLE
    // ============================================================

    @PostMapping("/user-roles")
    public ResponseEntity<?> assignRole(
            @Valid @RequestBody AssignRoleRequest request) {

        return ResponseEntity.ok(
                adminRbcService.assignRoleToUser(request)
        );
    }


    // ============================================================
    // ROLE -> PERMISSION
    // ============================================================

    @PostMapping("/role-permissions")
    public ResponseEntity<?> grantPermission(
            @Valid @RequestBody GrantPermissionRequest request) {

        return ResponseEntity.ok(
                adminRbcService.grantPermissionToRole(request)
        );
    }


    // ============================================================
    // ROLE/PERMISSION LIMIT
    // ============================================================

    @PostMapping("/limits")
    public ResponseEntity<?> setLimit(
            @Valid @RequestBody SetPermissionLimitRequest request) {

        return ResponseEntity.ok(
                adminRbcService.setPermissionLimit(request)
        );
    }


    // ============================================================
    // POLICY
    // ============================================================

    @PostMapping("/policies")
    public ResponseEntity<?> createOrUpdatePolicy(
            @Valid @RequestBody PolicyRequest request) {

        return ResponseEntity.ok(
                adminRbcService.createOrUpdatePolicy(request)
        );
    }
}