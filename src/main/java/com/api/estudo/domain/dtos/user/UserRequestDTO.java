package com.api.estudo.domain.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
                @NotBlank(message = "Name is mandatory ") String name,
                @NotBlank(message = "E-mail is mandatory ") @Email(message = "Invalid e-mail") String email,
                @NotBlank(message = "Password is mandatory ") @Size(min = 6, max = 8, message = "Password must contain between 6 and 8 characters") String password) {


}
