package com.example.corporatebanking.service;

import com.example.corporatebanking.dto.*;
import com.example.rbcaccess.entity.*;
import com.example.rbcaccess.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRbcService {

    private final RbcUserRepository userRepository;
    private final RbcRoleRepository roleRepository;
    private final RbcPermissionRepository permissionRepository;
    private final RbcUserRoleRepository userRoleRepository;
    private final RbcRolePermissionRepository rolePermissionRepository;
    private final RbcPermissionLimitRepository permissionLimitRepository;
    private final RbcPolicyRepository policyRepository;


    // ============================================================
    // CREATE USER
    // ============================================================

    public RbcUser createUser(CreateRbcUserRequest request) {

        if (userRepository
                .findByUsernameIgnoreCase(request.getUsername())
                .isPresent()) {

            throw new RuntimeException(
                    "User already exists: " + request.getUsername()
            );
        }

        RbcUser user = new RbcUser();

        user.setUsername(request.getUsername());
        user.setStatus(UserStatus.ACTIVE);

        return userRepository.save(user);
    }


    // ============================================================
    // GET USERS
    // ============================================================

    @Transactional(readOnly = true)
    public List<RbcUser> getUsers() {
        return userRepository.findAll();
    }


    // ============================================================
    // CREATE ROLE
    // ============================================================

    public RbcRole createRole(CreateRbcRoleRequest request) {

        if (roleRepository
                .findByRoleCode(request.getRoleCode())
                .isPresent()) {

            throw new RuntimeException(
                    "Role already exists: " + request.getRoleCode()
            );
        }

        RbcRole role = new RbcRole();

        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRoleType(RoleType.valueOf(request.getRoleType()));

        return roleRepository.save(role);
    }


    // ============================================================
    // GET ROLES
    // ============================================================

    @Transactional(readOnly = true)
    public List<RbcRole> getRoles() {
        return roleRepository.findAll();
    }


    // ============================================================
    // CREATE PERMISSION
    // ============================================================

    public RbcPermission createPermission(
            CreateRbcPermissionRequest request) {

        if (permissionRepository
                .findByPermissionCode(request.getPermissionCode())
                .isPresent()) {

            throw new RuntimeException(
                    "Permission already exists: "
                            + request.getPermissionCode()
            );
        }

        RbcPermission permission = new RbcPermission();

        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());

        return permissionRepository.save(permission);
    }


    // ============================================================
    // GET PERMISSIONS
    // ============================================================

    @Transactional(readOnly = true)
    public List<RbcPermission> getPermissions() {
        return permissionRepository.findAll();
    }


    // ============================================================
    // ASSIGN ROLE TO USER
    // ============================================================

    public String assignRoleToUser(
            AssignRoleRequest request) {

        RbcUser user = userRepository
                .findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: "
                                        + request.getUsername()
                        ));

        RbcRole role = roleRepository
                .findByRoleCode(request.getRoleCode())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Role not found: "
                                        + request.getRoleCode()
                        ));

        RbcUserRole userRole = new RbcUserRole();

        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());

        userRoleRepository.save(userRole);

        return "Role " + role.getRoleCode()
                + " assigned to user "
                + user.getUsername();
    }


    // ============================================================
    // GRANT PERMISSION TO ROLE
    // ============================================================

    public String grantPermissionToRole(
            GrantPermissionRequest request) {

        RbcRole role = roleRepository
                .findByRoleCode(request.getRoleCode())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Role not found: "
                                        + request.getRoleCode()
                        ));

        RbcPermission permission = permissionRepository
                .findByPermissionCode(request.getPermissionCode())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Permission not found: "
                                        + request.getPermissionCode()
                        ));

        RbcRolePermission rolePermission =
                new RbcRolePermission();

        rolePermission.setRoleId(role.getId());
        rolePermission.setPermissionId(permission.getId());

        rolePermissionRepository.save(rolePermission);

        return "Permission "
                + permission.getPermissionCode()
                + " granted to role "
                + role.getRoleCode();
    }


    // ============================================================
    // SET PERMISSION LIMIT
    // ============================================================

    public String setPermissionLimit(
            SetPermissionLimitRequest request) {

        RbcRole role = roleRepository
                .findByRoleCode(request.getRoleCode())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Role not found: "
                                        + request.getRoleCode()
                        ));

        RbcPermission permission = permissionRepository
                .findByPermissionCode(
                        request.getPermissionCode()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Permission not found: "
                                        + request.getPermissionCode()
                        ));

        RbcPermissionLimit limit =
                permissionLimitRepository
                        .findByRoleIdAndPermissionId(
                                role.getId(),
                                permission.getId()
                        )
                        .orElseGet(RbcPermissionLimit::new);

        limit.setRoleId(role.getId());
        limit.setPermissionId(permission.getId());
        limit.setMaxAmount(request.getMaxAmount());
        limit.setCurrency(request.getCurrency());

        permissionLimitRepository.save(limit);

        return "Limit configured successfully for role "
                + role.getRoleCode()
                + " and permission "
                + permission.getPermissionCode();
    }


    // ============================================================
    // CREATE / UPDATE POLICY
    // ============================================================

    public String createOrUpdatePolicy(
            PolicyRequest request) {

        RbcPolicy policy =
                policyRepository
                        .findByPolicyCodeAndStatus(
                                request.getPolicyCode(),
                                request.getStatus()
                        )
                        .orElseGet(RbcPolicy::new);

        policy.setPolicyCode(request.getPolicyCode());
        policy.setPolicyValue(request.getPolicyValue());
        policy.setStatus(request.getStatus());

        policyRepository.save(policy);

        return "Policy "
                + request.getPolicyCode()
                + " configured successfully";
    }
}