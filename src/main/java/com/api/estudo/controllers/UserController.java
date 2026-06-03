package com.api.estudo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.estudo.domain.dtos.common.MessageResponseDTO;
import com.api.estudo.domain.dtos.common.PageResponseDTO;
import com.api.estudo.domain.dtos.user.UserRequestDTO;
import com.api.estudo.domain.dtos.user.UserResponseDTO;
import com.api.estudo.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> create(@Valid @RequestBody UserRequestDTO data) {
        this.userService.save(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDTO("Created User Succesfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID userId) {
        var user = this.userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        var users = this.userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    private static final int MAX_PAGE_SIZE = 20;

    @GetMapping("/held")
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> getAllUsersHeld(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        int validateSize = Math.min(size, MAX_PAGE_SIZE);
        PageResponseDTO<UserResponseDTO> usersPagination = this.userService.findAllPagination(page, validateSize);
        return ResponseEntity.status(HttpStatus.OK).body(usersPagination);
    }

}
