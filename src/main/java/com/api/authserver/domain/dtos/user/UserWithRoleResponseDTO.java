package com.api.authserver.domain.dtos.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.api.authserver.domain.dtos.role.RoleResponseDTO;
import com.api.authserver.domain.entities.User;

public record UserWithRoleResponseDTO(UUID id, String name, String email, LocalDateTime createdAt,
        RoleResponseDTO role) {
    public UserWithRoleResponseDTO(User user) {
        this(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                new RoleResponseDTO(user.getRole()));
    }
}
