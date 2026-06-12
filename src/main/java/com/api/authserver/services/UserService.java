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
    public void saveUser(UserRequestDTO data) {

        userRepository
                .findByEmail(data.email())
                .ifPresent(user -> {
                    throw new DataConflictException("Existe usuário cadastrado com este email no sistema");
                });

        Role role = roleRepository.findById(data.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        String encryptedPassword = passwordEncoder.encode(data.password());

        User user = User.builder()
                .name(data.name())
                .email(data.email())
                .password(encryptedPassword)
                .role(role)
                .build();

        userRepository.save(user);
    }

    public PageResponseDTO<UserResponseDTO> findAllUsersPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserResponseDTO> users = userRepository.findAll(pageable).map(UserResponseDTO::new);

        return new PageResponseDTO<>(
                users.getContent(),
                users.getNumber(),
                users.getSize(),
                users.getTotalPages(),
                users.getTotalElements());
    }

    public UserResponseDTO findUserById(UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return new UserResponseDTO(user);
    }

    public List<UserResponseDTO> findAllUsers() {
        return userRepository
                .findAllWithRoles()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    public UserWithRoleResponseDTO findUserWithRole(UUID userId) {
        User usersWithRole = userRepository.findUserWithRoleById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        return new UserWithRoleResponseDTO(usersWithRole);
    }

    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        userRepository.delete(user);
    }
}
