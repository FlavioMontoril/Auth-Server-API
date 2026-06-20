package com.api.authserver.domain.dtos.user;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
                @NotBlank(message = "Name is mandatory ") @Size(max = 50, message = "Name must not exceed 50 characters") String name,
                @NotBlank(message = "E-mail is mandatory ") @Email(message = "Invalid e-mail") String email,
                @NotBlank(message = "Password is mandatory ") @Size(min = 6, max = 8, message = "Password must contain between 6 and 8 characters") String password,
                @NotNull(message = "Role_id is mandatory") UUID roleId) {
}
