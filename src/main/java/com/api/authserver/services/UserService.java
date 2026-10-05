package com.api.authserver.services;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api.authserver.domain.dtos.common.PageResponseDTO;
import com.api.authserver.domain.dtos.user.UserRequestDTO;
import com.api.authserver.domain.dtos.user.UserResponseDTO;
import com.api.authserver.domain.dtos.user.UserWithRoleResponseDTO;
import com.api.authserver.domain.dtos.user.events.UserCreatedEventDTO;
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
    private final UploadService uploadService;
    private final UserPresenceRegistry presenceRegistry;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void saveUser(UserRequestDTO data) {

        if (userRepository.existsByEmail(data.email())) {
                throw new DataConflictException("Existe usuário cadastrado com este email no sistema");
        }

        Role role = roleRepository.getReferenceById(data.roleId());

        String encryptedPassword = passwordEncoder.encode(data.password());

        String avatarFileName = null;
        if (data.avatar() != null && !data.avatar().isEmpty()) {
            avatarFileName = this.uploadService.uploadImg(data.avatar());
        }

        User user = User.builder()
                .name(data.name())
                .email(data.email())
                .password(encryptedPassword)
                .avatar(avatarFileName)
                .role(role)
                .build();

        User newUser = userRepository.save(user);

        // Dispara o evento interno do Spring.
        // O @TransactionalEventListener só enviará ao Kafka após o COMMIT desta
        // transação ser efetuado com sucesso.
        eventPublisher.publishEvent(new UserCreatedEventDTO(
                newUser.getId().toString(),
                newUser.getName(),
                newUser.getEmail()
            ));
    }

    public PageResponseDTO<UserResponseDTO> findAllUsersPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserResponseDTO> users = userRepository.findAll(pageable)
                .map(user -> new UserResponseDTO(user, presenceRegistry.isConnected(user.getEmail())));

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

        return new UserResponseDTO(user, presenceRegistry.isConnected(user.getEmail()));
    }

    public List<UserResponseDTO> findAllUsers() {
        return userRepository
                .findAllWithRoles()
                .stream()
                .map(user -> new UserResponseDTO(user, presenceRegistry.isConnected(user.getEmail())))
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

    @Transactional
    public void updateAvatar(UUID userId, MultipartFile avatar) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (avatar == null || avatar.isEmpty()) {
            throw new IllegalArgumentException(
                    "Avatar é obrigatório");
        }

        // Guarda o avatar antigo
        String oldAvatar = user.getAvatar();

        // Faz upload do novo avatar
        String newAvatar = uploadService.uploadImg(avatar);

        user.setAvatar(newAvatar);

        userRepository.save(user);

        // Remove o arquivo antigo
        uploadService.deleteImg(oldAvatar);
    }

}
