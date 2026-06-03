package com.api.estudo.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.estudo.domain.dtos.common.PageResponseDTO;
import com.api.estudo.domain.dtos.user.UserRequestDTO;
import com.api.estudo.domain.dtos.user.UserResponseDTO;
import com.api.estudo.domain.entities.User;
import com.api.estudo.domain.exceptions.DataConflictException;
import com.api.estudo.domain.exceptions.ResourceNotFoundException;
import com.api.estudo.domain.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(UserRequestDTO data) {

        this.userRepository
                .findByEmail(data.email())
                .ifPresent(user -> {
                    throw new DataConflictException("Existe usuário cadastrado com este email no sistema");
                });

        String encryptedPassword = this.passwordEncoder.encode(data.password());

        User user = User.builder()
                .name(data.name())
                .email(data.email())
                .password(encryptedPassword)
                .build();

        this.userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<UserResponseDTO> findAllPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserResponseDTO> users = this.userRepository.findAll(pageable).map(UserResponseDTO::new);

        return new PageResponseDTO<>(
                users.getContent(),
                users.getNumber(),
                users.getSize(),
                users.getTotalPages(),
                users.getTotalElements());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID userId) {
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return this.userRepository
                .findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }
}
