package com.api.authserver.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.authserver.domain.entities.LoginHistory;
import com.api.authserver.domain.enums.LoginStatus;
import com.api.authserver.domain.repositories.LoginHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    @Transactional
    public void registerLoginAttempt(String email, String ipAddress, LoginStatus status) {
        LoginHistory history = LoginHistory.builder()
                .email(email)
                .ipAddress(ipAddress)
                .status(status)
                .build();
        
        loginHistoryRepository.save(history);
    }
}
