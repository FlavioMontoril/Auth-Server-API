package com.api.authserver.domain.dtos.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPresenceDTO {
    private UUID id;
    private String name;
    private  String avatar;
    private boolean connected;
}