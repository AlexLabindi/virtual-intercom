package com.alex.intercom.ports.out;

import com.alex.intercom.domain.CallSession;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for managing CallSession persistence operations.
 *
 * Outbound Port (Driven SPI): Interfaccia CallSessionRepositoryPort che esprime i bisogni infrastrutturali ,
 * del core per il salvataggio persistente dei dati.
 */
public interface CallSessionRepositoryPort {
    CallSession save(CallSession callSession);
    Optional<CallSession> findById(UUID id);
    Optional<CallSession> findByToken(String token);
}