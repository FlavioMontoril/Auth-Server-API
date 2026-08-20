package com.api.authserver.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.api.authserver.domain.dtos.authentication.AuthenticationRequestDTO;
import com.api.authserver.domain.dtos.authentication.AuthenticationResponseDTO;
import com.api.authserver.domain.entities.User;
import com.api.authserver.domain.enums.LoginStatus;
import com.api.authserver.infra.security.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUseCaseService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final LoginHistoryService loginHistoryService;

    public AuthenticationResponseDTO executeLogin(AuthenticationRequestDTO data, String ipAddress) {
        try {
            // 1. Cria o token interno do Spring com e-mail e senha
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            // 2. O Gerenciador autentica (valida a senha no banco)
            var authResult = this.authenticationManager.authenticate(usernamePassword);
            // 3. Pega o usuário autenticado (que é a sua entidade User)
            var user = (User) authResult.getPrincipal();
            // 4. Gera o token JWT para esse usuário
            String token = tokenService.generateToken(user);
            
            // Grava sucesso
            loginHistoryService.registerLoginAttempt(data.email(), ipAddress, LoginStatus.SUCCESS);
            
            return new AuthenticationResponseDTO(token);
        } catch (AuthenticationException e) {
            // Grava falha
            loginHistoryService.registerLoginAttempt(data.email(), ipAddress, LoginStatus.FAILURE);
            throw e;
        }
    }
}
