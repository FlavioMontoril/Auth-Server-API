package com.api.authserver.domain.dtos.role;

import java.time.LocalDateTime;
import java.util.UUID;

import com.api.authserver.domain.entities.Role;
import com.api.authserver.domain.enums.RoleOptions;

public record RoleResponseDTO(UUID id, RoleOptions name, LocalDateTime createdAt) {
    public RoleResponseDTO(Role role) {
        this(
                role.getId(),
                role.getName(),
                role.getCreatedAt());
    }

}
