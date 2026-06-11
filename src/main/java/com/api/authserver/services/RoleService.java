package com.api.authserver.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.api.authserver.domain.dtos.role.RoleRequestDTO;
import com.api.authserver.domain.dtos.role.RoleResponseDTO;
import com.api.authserver.domain.entities.Role;
import com.api.authserver.domain.exceptions.DataConflictException;
import com.api.authserver.domain.exceptions.ResourceNotFoundException;
import com.api.authserver.domain.repositories.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public void saveRole(RoleRequestDTO data) {
        this.roleRepository.findByName(data.name())
                .ifPresent(role -> {
                    throw new DataConflictException("Role is already registered");
                });

        Role role = Role.builder()
                .name(data.name())
                .build();

        this.roleRepository.save(role);
    }

    public RoleResponseDTO findById(UUID roleId) {
        Role role = this.roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        return new RoleResponseDTO(role);
    }

    public List<RoleResponseDTO> findAllRoles() {
        return this.roleRepository.findAll().stream().map(RoleResponseDTO::new).toList();
    }
}
