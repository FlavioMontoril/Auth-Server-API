package com.api.authserver.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.authserver.domain.dtos.common.PageResponseDTO;
import com.api.authserver.domain.dtos.user.UserRequestDTO;
import com.api.authserver.domain.dtos.user.UserResponseDTO;
import com.api.authserver.domain.dtos.user.UserWithRoleResponseDTO;
import com.api.authserver.domain.entities.Role;
import com.api.authserver.domain.entities.User;
import com.api.authserver.domain.exceptions.DataConflictException;
import com.api.authserver.domain.exceptions.ResourceNotFoundException;
import com.api.authserver.domain.repositories.RoleRepository;
import com.api.authserver.domain.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(UserRequestDTO data) {

        this.userRepository
                .findByEmail(data.email())
                .ifPresent(user -> {
                    throw new DataConflictException("Existe usuário cadastrado com este email no sistema");
                });

        Role role = this.roleRepository.findById(data.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        String encryptedPassword = this.passwordEncoder.encode(data.password());

        User user = User.builder()
                .name(data.name())
                .email(data.email())
                .password(encryptedPassword)
                .role(role)
                .build();

        this.userRepository.save(user);
    }

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

    public UserResponseDTO findById(UUID userId) {
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return new UserResponseDTO(user);
    }

    public List<UserResponseDTO> findAll() {
        return this.userRepository
                .findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    public UserWithRoleResponseDTO findUserWithRole(UUID userId) {
        User usersWithRole = this.userRepository.findUserWithRoleById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return new UserWithRoleResponseDTO(usersWithRole);
    }
}
