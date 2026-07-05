package com.alex.intercom.adapters.persistence.repositories;

import com.alex.intercom.adapters.persistence.entities.CallSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for CallSessionEntity.
 */
public interface JpaCallSessionRepository extends JpaRepository<CallSessionEntity, UUID> {
    Optional<CallSessionEntity> findByToken(String token);
}