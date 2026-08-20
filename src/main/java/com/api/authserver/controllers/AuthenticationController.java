package com.api.authserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.authserver.domain.dtos.authentication.AuthenticationRequestDTO;
import com.api.authserver.domain.dtos.authentication.AuthenticationResponseDTO;
import com.api.authserver.infra.utils.HttpUtils;
import com.api.authserver.services.LoginUseCaseService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final LoginUseCaseService loginUseCaseService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody @Valid AuthenticationRequestDTO data, HttpServletRequest request) {
        String ipAddress = HttpUtils.getClientIpAddress(request);
        AuthenticationResponseDTO token = loginUseCaseService.executeLogin(data, ipAddress);
        System.out.println(token);
        return ResponseEntity.ok(token);
    }
}
