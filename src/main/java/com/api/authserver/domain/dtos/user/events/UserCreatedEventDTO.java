package com.api.authserver.domain.dtos.user.events;

import java.io.Serializable;

public record UserCreatedEventDTO(
    String id,
    String name,
    String email,
    String role
) implements Serializable {}