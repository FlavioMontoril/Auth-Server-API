package com.api.authserver.domain.dtos.role;

import com.api.authserver.domain.enums.RoleOptions;

import jakarta.validation.constraints.NotNull;

public record RoleRequestDTO(@NotNull(message = "Role name is required") RoleOptions name) {
}
