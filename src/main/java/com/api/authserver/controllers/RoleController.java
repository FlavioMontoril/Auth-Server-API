package com.api.authserver.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.authserver.domain.dtos.common.MessageResponseDTO;
import com.api.authserver.domain.dtos.role.RoleRequestDTO;
import com.api.authserver.domain.dtos.role.RoleResponseDTO;
import com.api.authserver.services.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<MessageResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO data) {
        this.roleService.saveRole(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDTO("Role created successfully"));
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<RoleResponseDTO> findRoleById(@PathVariable UUID roleId) {
        RoleResponseDTO role = this.roleService.findById(roleId);
        return ResponseEntity.status(HttpStatus.OK).body(role);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<List<RoleResponseDTO>> findAllRoles() {
        List<RoleResponseDTO> roles = this.roleService.findAllRoles();
        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }

}
