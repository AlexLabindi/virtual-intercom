package com.alex.intercom.domain;

import com.alex.intercom.adapters.web.websocket.SignalingWebSocketHandler;
import com.alex.intercom.domain.service.CallService;
import com.alex.intercom.ports.out.CallSessionRepositoryPort;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallServiceTest {

    @Mock
    private CallSessionRepositoryPort sessionRepositoryPort;

    @Mock
    private SignalingWebSocketHandler webSocketNotificationPort;

    @InjectMocks
    private CallService callService; // Core Business Service under analysis

    @Test
    @DisplayName("1. Should create a new call session in ACTIVE status when guest rings the bell")
    void shouldCreateActiveSessionWhenGuestRings() {
        // GIVEN
        String guestIp = "192.168.1.50";
        CallSession expectedSession = new CallSession(UUID.randomUUID(),guestIp,"TOKEN_ABC", "CONNECTED", LocalDateTime.now(),LocalDateTime.now().plusMinutes(2));

        when(sessionRepositoryPort.save(any(CallSession.class))).thenReturn(expectedSession);

        // WHEN
        CallSession actualSession = callService.triggerRing(guestIp);

        // ASSERT / THEN
        assertNotNull(actualSession, "The generated session must not be null");
        assertEquals("ACTIVE", actualSession.getStatus(), "The initial session status must be ACTIVE");
        assertEquals(guestIp, actualSession.getGuestIp(), "The stored guest IP must match the sender's IP");

        // Verify outbound dependencies are invoked correctly
        verify(sessionRepositoryPort, times(1)).save(any(CallSession.class));
        verify(webSocketNotificationPort, times(1)).broadcastNotification(anyString());
    }

    @Test
    @DisplayName("2. Should transition session status to CONNECTED when owner accepts the call")
    void shouldChangeStatusToConnectedWhenOwnerAccepts() {
        // GIVEN
        UUID sessionId = UUID.randomUUID();
        String guestIp = "192.168.1.50";
        CallSession existingSession = new  CallSession(UUID.randomUUID(),guestIp,"TOKEN_ABC", "ACTIVE", LocalDateTime.now(),LocalDateTime.now().plusMinutes(2));

        when(sessionRepositoryPort.findById(sessionId)).thenReturn(Optional.of(existingSession));
        when(sessionRepositoryPort.save(any(CallSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        callService.acceptCall(sessionId);

        // ASSERT / THEN
        assertEquals("CONNECTED", existingSession.getStatus(), "The session status must transition to CONNECTED");
        verify(sessionRepositoryPort, times(1)).findById(sessionId);
        verify(sessionRepositoryPort, times(1)).save(existingSession);
        verify(webSocketNotificationPort, times(1)).broadcastNotification(contains("accept"));
    }

    @SneakyThrows
    @Test
    @DisplayName("3. Should throw an IllegalArgumentException when attempting to accept a non-existing session")
    void shouldThrowExceptionWhenAcceptingNonExistingSession() {
        // GIVEN
        UUID nonExistingSessionId = UUID.randomUUID();
        when(sessionRepositoryPort.findById(nonExistingSessionId)).thenReturn(Optional.empty());

        // WHEN & ASSERT / THEN
        assertThrows(IllegalArgumentException.class, () -> {
            callService.acceptCall(nonExistingSessionId);
        }, "Should throw an IllegalArgumentException if the session is missing from the database");

        verify(sessionRepositoryPort, times(1)).findById(nonExistingSessionId);
        verify(sessionRepositoryPort, never()).save(any());
        verify(webSocketNotificationPort, never()).afterConnectionClosed(any(), CloseStatus.NORMAL );
    }

    @Test
    @DisplayName("4. Should transition session status to TERMINATED when the call is ended")
    void shouldChangeStatusToTerminatedWhenCallIsEnded() {
        // GIVEN
        UUID sessionId = UUID.randomUUID();
        String guestIp = "192.168.1.50";
        CallSession connectedSession = new  CallSession(UUID.randomUUID(),guestIp,"TOKEN_ABC", "ACTIVE", LocalDateTime.now(),LocalDateTime.now().plusMinutes(2));
        String terminationReason = "CANCELED";

        when(sessionRepositoryPort.findById(sessionId)).thenReturn(Optional.of(connectedSession));
        when(sessionRepositoryPort.save(any(CallSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        callService.terminateCall(sessionId, terminationReason);

        // ASSERT / THEN
        assertEquals("TERMINATED", connectedSession.getStatus(), "The final session status must be set to TERMINATED");
        verify(sessionRepositoryPort, times(1)).findById(sessionId);
        verify(sessionRepositoryPort, times(1)).save(connectedSession);
        verify(webSocketNotificationPort, times(1)).broadcastNotification(contains("terminate"));
    }
}