package com.api.authserver.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.authserver.domain.dtos.common.MessageResponseDTO;
import com.api.authserver.domain.dtos.common.PageResponseDTO;
import com.api.authserver.domain.dtos.user.UserRequestDTO;
import com.api.authserver.domain.dtos.user.UserResponseDTO;
import com.api.authserver.domain.dtos.user.UserWithRoleResponseDTO;
import com.api.authserver.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private static final int MAX_PAGE_SIZE = 20;

    private final UserService userService;

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<MessageResponseDTO> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDTO("User deleted successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<MessageResponseDTO> create(@Valid @RequestBody UserRequestDTO data) {
        userService.saveUser(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDTO("User created successfully"));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID userId) {
        var user = userService.findUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        var users = userService.findAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> getAllUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        int validateSize = Math.min(size, MAX_PAGE_SIZE);
        PageResponseDTO<UserResponseDTO> usersPagination = userService.findAllUsersPagination(page, validateSize);
        return ResponseEntity.status(HttpStatus.OK).body(usersPagination);
    }

    @GetMapping("/{userId}/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<UserWithRoleResponseDTO> getUserWithRoleById(@PathVariable UUID userId) {
        UserWithRoleResponseDTO userWithRole = userService.findUserWithRole(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userWithRole);
    }

}
