package com.alex.intercom.ports.in;

import com.alex.intercom.domain.CallSession;
import java.util.UUID;

/**
 * Inbound port defining the core business actions for an intercom call lifecycle.
 */
public interface ManageCallUseCase {

    /**
     * Triggers a new visitor ring from the gate.
     * @param guestIp the IP address of the caller at the gate.
     * @return the created CallSession details.
     */
    CallSession triggerRing(String guestIp);

    /**
     * Connects the call when the owner accepts it from the web interface.
     */
    void acceptCall(UUID sessionId);

    /**
     * Terminates the current active call session.
     * @param sessionId the unique session identifier.
     * @param reason why the call ended (e.g., HUNG_UP, REJECTED).
     */
    void terminateCall(UUID sessionId, String reason);
}