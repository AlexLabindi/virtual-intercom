package com.alex.intercom.adapters.persistence;

import com.alex.intercom.adapters.persistence.entities.CallSessionEntity;
import com.alex.intercom.adapters.persistence.repositories.JpaCallSessionRepository;
import com.alex.intercom.domain.CallSession;
import com.alex.intercom.ports.out.CallSessionRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    public CallSession save(CallSession domainSession) {
        // Creiamo SEMPRE una nuova riga (Nuovo record di storico)
        CallSessionEntity entity = new CallSessionEntity();

        entity.setSessionId(domainSession.getId()); // Mantiene l'ID originale inviato dal dominio
        entity.setGuestIp(domainSession.getGuestIp());
        entity.setToken(domainSession.getToken());
        entity.setStatus(domainSession.getStatus()); // "ACTIVE", "CONNECTED" o "TERMINATED"
        entity.setCreatedAt(LocalDateTime.now()); // Timestamp preciso di questa transizione
        entity.setExpiresAt(domainSession.getExpiresAt());

        CallSessionEntity saved = repository.save(entity);

        return toDomain(saved);
    }

    @Override
    public Optional<CallSession> findById(UUID sessionId) {
        // Cerchiamo l'ultimo record inserito per questo ID di sessione
        return repository.findFirstBySessionIdOrderByCreatedAtDesc(sessionId)
                .map(this::toDomain);
    }

    private CallSession toDomain(CallSessionEntity entity) {
        return new CallSession(
                entity.getSessionId(), entity.getGuestIp(), entity.getToken(),
                entity.getStatus(), entity.getCreatedAt(), entity.getExpiresAt()
        );
    }
}