package com.alex.intercom.domain.service;

import com.alex.intercom.adapters.web.websocket.SignalingWebSocketHandler;
import com.alex.intercom.domain.CallLog;
import com.alex.intercom.domain.CallSession;
import com.alex.intercom.ports.in.ManageCallUseCase;
import com.alex.intercom.ports.out.CallLogRepositoryPort;
import com.alex.intercom.ports.out.CallSessionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Core domain service implementing the inbound port for call management.
 * Fully decoupled from infrastructure and frameworks.
 */
@Service
public class CallService implements ManageCallUseCase {

    private static final Logger log = LoggerFactory.getLogger(CallService.class);

    private final CallSessionRepositoryPort sessionRepositoryPort;
    private final CallLogRepositoryPort logRepositoryPort;
    private final SignalingWebSocketHandler webSocketHandler;

    // Dependency injection through constructor (clean and testable code)
    public CallService(CallSessionRepositoryPort sessionRepositoryPort,
                       CallLogRepositoryPort logRepositoryPort,
                       SignalingWebSocketHandler webSocketHandler) {
        this.sessionRepositoryPort = sessionRepositoryPort;
        this.logRepositoryPort = logRepositoryPort;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public CallSession triggerRing(String guestIp) {
        log.info("Processing ring request from gate IP: {}", guestIp);

        UUID sessionId = UUID.randomUUID();
        String secureToken = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        // The session expires by default in 5 minutes if not accepted or managed
        LocalDateTime expiresAt = now.plusMinutes(5);

        CallSession newSession = new CallSession(
                sessionId,
                guestIp,
                secureToken,
                "ACTIVE",
                now,
                expiresAt
        );

        CallSession savedSession = sessionRepositoryPort.save(newSession);// salvataggio DB  tramite chiama una porta di uscita (Outbound Port): sessionRepositoryPort.save(session).
        // L'adattatore della persistenza prende l'oggetto di dominio,
        // lo traduce in un'entità Hibernate e lo scrive sul DB tramite la query INSERT INTO call_sessions....
        log.info("New call session successfully created with ID: {} and status: {}", sessionId, savedSession.getStatus());

        // TRIGGER WEBSOCKET: Avvisa la Dashboard in tempo reale che qualcuno sta suonando
        try {
            String jsonPayload = String.format(
                    "{\"event\": \"ring\", \"status\": \"%s\", \"sessionId\": \"%s\", \"guestIp\": \"%s\"}",
                    savedSession.getStatus(),
                    savedSession.getId().toString(),
                    savedSession.getGuestIp()
            );
            webSocketHandler.broadcastNotification(jsonPayload);
            log.info("WebSocket broadcast notification sent for session: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket broadcast notification for session: {}", sessionId, e);
        }

        return savedSession;
    }

    @Override
    public void acceptCall(UUID sessionId) {
        log.info("Owner is accepting the call session: {}", sessionId);

        CallSession session = sessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new IllegalStateException("Call is not in an active state");
        }

        CallSession updatedSession = new CallSession(
                session.getId(), session.getGuestIp(), session.getToken(),
                "CONNECTED", session.getCreatedAt(), session.getExpiresAt()
        );

        sessionRepositoryPort.save(updatedSession);

        // 🔥 BROADCAST DI ACCETTAZIONE: Sposta entrambi in modalità CHAT!
        try {
            String jsonPayload = String.format("{\"event\": \"accept\", \"sessionId\": \"%s\"}", sessionId.toString());
            webSocketHandler.broadcastNotification(jsonPayload);
        } catch (Exception e) {
            log.error("Failed to send accept broadcast", e);
        }
    }

    @Override
    public void terminateCall(UUID sessionId, String reason) {
        log.info("Terminating call session: {} due to reason: {}", sessionId, reason);

        CallSession session = sessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> {
                    log.error("Call session not found for termination: {}", sessionId);
                    return new IllegalArgumentException("Session not found");
                });

        // Terminate the pure domain object logic
        session.terminate();
        CallSession savedSession = sessionRepositoryPort.save(session);

        // Historically archive the completed call in the logs table
        CallLog callLog = new CallLog(
                null, // ID will be auto-generated by DB adapter
                session.getId(),
                session.getCreatedAt(),
                LocalDateTime.now(),
                reason,
                session.getGuestIp()
        );
        logRepositoryPort.save(callLog);
        log.info("Call session {} successfully archived in CallLog with reason: {}", sessionId, reason);

        // 🔥 NOTIFICA IL WEBSOCKET: Diciamo al Frontend di resettarsi!
        try {
            String jsonPayload = String.format(
                    "{\"event\": \"terminate\", \"status\": \"%s\", \"sessionId\": \"%s\"}",
                    savedSession.getStatus(), // sarà TERMINATED o COMPLETED
                    savedSession.getId().toString()
            );
            webSocketHandler.broadcastNotification(jsonPayload);
            log.info("WebSocket termination broadcast sent for session: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket termination notification", e);
        }
    }
}