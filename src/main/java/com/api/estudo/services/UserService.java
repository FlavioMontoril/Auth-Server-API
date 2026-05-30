package com.api.estudo.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.estudo.domain.dtos.user.UserRequestDTO;
import com.api.estudo.domain.dtos.user.UserResponseDTO;
import com.api.estudo.domain.entities.User;
import com.api.estudo.domain.exceptions.DataConflictException;
import com.api.estudo.domain.exceptions.ResourceNotFoundException;
import com.api.estudo.domain.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(UserRequestDTO data) {

        this.userRepository
                .findByEmail(data.email())
                .ifPresent(user -> {
                    throw new DataConflictException("Já existe usuário com este email no sistema");
                });

        User user = new User();

        String encryptedPassword = this.passwordEncoder.encode(data.password());

        user.setName(data.name());
        user.setEmail(data.email());
        user.setPassword(encryptedPassword);

        this.userRepository.save(user);
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
}
