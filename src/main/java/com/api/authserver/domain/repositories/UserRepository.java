package com.api.authserver.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.authserver.domain.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u FROM User u
            JOIN FETCH u.role
            WHERE u.id = :userId
            """)
    Optional<User> findUserWithRoleById(UUID userId);
}
