package com.alex.intercom.adapters.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Handler acting as a WebRTC Signaling Server to route structural audio session parameters.
 */
@Component
@CrossOrigin(origins = "*")
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SignalingWebSocketHandler.class);

    // Thread-safe map storing active connected WebSocket sessions (session ID -> WebSocket session)
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        activeSessions.put(session.getId(), session);
        log.info("New WebSocket connection established. Internal Session ID: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received real-time payload from session {}: {}", session.getId(), payload);

        // Broadcast the WebRTC signal or ring command to all other connected devices (e.g., Owner's PC)
        for (WebSocketSession activeSession : activeSessions.values()) {
            if (activeSession.isOpen() && !activeSession.getId().equals(session.getId())) {
                try {
                    activeSession.sendMessage(new TextMessage(payload));
                } catch (IOException e) {
                    log.error("Failed to route signaling message to session {}", activeSession.getId(), e);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        activeSessions.remove(session.getId());
        log.info("WebSocket connection closed for session {}. Status: {}", session.getId(), status);
    }
}