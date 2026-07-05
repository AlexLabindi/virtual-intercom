package com.alex.intercom.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pure Domain model representing an active intercom call session triggered at the gate.
 * This class is decoupled from any database framework.
 */
public class CallSession {
    private final UUID id;
    private final String guestIp;
    private final String token;
    private String status; // e.g., "ACTIVE", "CONNECTED", "TERMINATED"
    private final LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public CallSession(UUID id, String guestIp, String token, String status, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.guestIp = guestIp;
        this.token = token;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Business Logic: verify if the current session token is still valid
    public boolean isValid(LocalDateTime currentTime) {
        return "ACTIVE".equals(status) && currentTime.isBefore(expiresAt);
    }

    // Business Logic: terminate the current session immediately
    public void terminate() {
        this.status = "TERMINATED";
        this.expiresAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getGuestIp() { return guestIp; }
    public String getToken() { return token; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}