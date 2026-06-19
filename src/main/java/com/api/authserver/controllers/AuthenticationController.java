package com.api.authserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.authserver.domain.dtos.authentication.AuthenticationRequestDTO;
import com.api.authserver.domain.dtos.authentication.AuthenticationResponseDTO;
import com.api.authserver.domain.entities.User;
import com.api.authserver.infra.security.TokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody @Valid AuthenticationRequestDTO data) {
        // 1. Cria o token interno do Spring com e-mail e senha
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        // 2. O Gerenciador autentica (valida a senha no banco)
        var authResult = this.authenticationManager.authenticate(usernamePassword);
        // 3. Pega o usuário autenticado (que é a sua entidade User)
        var user = (User) authResult.getPrincipal();
        // 4. Gera o token JWT para esse usuário
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponseDTO(token));
    }

}
