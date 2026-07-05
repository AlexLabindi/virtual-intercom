package com.alex.intercom.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pure Domain model representing the historical record of a completed intercom call.
 */
public class CallLog {
    private final Long id;
    private final UUID sessionId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String terminationReason; // e.g., "HUNG_UP_BY_OWNER", "REJECTED", "TIMEOUT"
    private final String guestIp;

    public CallLog(Long id, UUID sessionId, LocalDateTime startTime, LocalDateTime endTime, String terminationReason, String guestIp) {
        this.id = id;
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.terminationReason = terminationReason;
        this.guestIp = guestIp;
    }

    // Getters
    public Long getId() { return id; }
    public UUID getSessionId() { return sessionId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getTerminationReason() { return terminationReason; }
    public String getGuestIp() { return guestIp; }
}