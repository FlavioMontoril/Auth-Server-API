package com.api.authserver.domain.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.authserver.domain.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    // Sem isso, se você tivesse 100 usuários, o Hibernate faria 1 consulta para listar os usuários e depois mais 100 consultas individuais para buscar a role de
    //      cada um (o famoso problema N+1). Agora, ele traz tudo em apenas uma consulta SQL.
    @Query("SELECT u FROM User u JOIN FETCH u.role")
    List<User> findAllWithRoles();

    @Query("""
            SELECT u FROM User u
            JOIN FETCH u.role
            WHERE u.id = :userId
            """)
    Optional<User> findUserWithRoleById(UUID userId);
}
