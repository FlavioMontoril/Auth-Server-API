package com.api.authserver.domain.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.authserver.domain.entities.LoginHistory;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {
}
