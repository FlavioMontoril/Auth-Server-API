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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createUser(@Valid @RequestBody RoleRequestDTO data) {
        this.roleService.saveRole(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDTO("Role created Succesfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RoleResponseDTO> findUserById(@PathVariable UUID userId) {
        RoleResponseDTO role = this.roleService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(role);
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> findAllRoles() {
        List<RoleResponseDTO> roles = this.roleService.findAllRoles();
        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }

}
