package com.alex.intercom.adapters.persistence;

import com.alex.intercom.adapters.persistence.entities.CallSessionEntity;
import com.alex.intercom.adapters.persistence.repositories.JpaCallSessionRepository;
import com.alex.intercom.domain.CallSession;
import com.alex.intercom.ports.out.CallSessionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence Adapter implementing the outbound port for managing Call Sessions.
 * Maps between pure domain models and JPA database entities.
 */
@Component
public class CallSessionPersistenceAdapter implements CallSessionRepositoryPort {

    private final JpaCallSessionRepository repository;

    public CallSessionPersistenceAdapter(JpaCallSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public CallSession save(CallSession domainModel) {
        // Map from Domain Model to JPA Entity
        CallSessionEntity entity = new CallSessionEntity(
                domainModel.getId(),
                domainModel.getGuestIp(),
                domainModel.getToken(),
                domainModel.getStatus(),
                domainModel.getCreatedAt(),
                domainModel.getExpiresAt()
        );

        CallSessionEntity savedEntity = repository.save(entity);

        // Map back from JPA Entity to Domain Model
        return toDomain(savedEntity);
    }

    @Override
    public Optional<CallSession> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<CallSession> findByToken(String token) {
        return repository.findByToken(token).map(this::toDomain);
    }

    // Helper method for encapsulation mapping
    private CallSession toDomain(CallSessionEntity entity) {
        return new CallSession(
                entity.getId(),
                entity.getGuestIp(),
                entity.getToken(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }
}