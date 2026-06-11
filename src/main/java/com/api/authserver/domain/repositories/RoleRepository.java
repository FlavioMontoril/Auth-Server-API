    package com.api.authserver.domain.repositories;

    import java.util.Optional;
    import java.util.UUID;

    import org.springframework.data.jpa.repository.JpaRepository;

import com.api.authserver.domain.entities.Role;
import com.api.authserver.domain.enums.RoleOptions;

    public interface RoleRepository extends JpaRepository<Role, UUID> {
        Optional<Role> findByName(RoleOptions name);
    }
    
