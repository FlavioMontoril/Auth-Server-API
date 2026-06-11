package com.api.authserver.domain.dtos.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.api.authserver.domain.entities.User;

public record UserResponseDTO(UUID id, String name, String email, LocalDateTime createdAt) {

    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt());
    }
}
