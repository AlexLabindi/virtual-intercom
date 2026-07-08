package com.alex.intercom.adapters.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SignalingWebSocketHandler.class);

    // Thread-safe list to keep track of all connected cards/devices
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("New WebSocket connection established. ID Session: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("WebSocket message received from the client: {}", payload);

        // 🔥 SE È UN MESSAGGIO DI CHAT, FAI IL BROADCAST IMMEDIATO A TUTTI
        if (payload.contains("\"event\":\"chat\"") || payload.contains("\"event\": \"chat\"")) {
            broadcastNotification(payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("WebSocket connection closed for ID: {}", session.getId());
    }

    /**
     * Send a message to all connected clients
     */
    public void broadcastNotification(String jsonPayload) {
        log.info("WebSocket broadcast initialization for payload: {}", jsonPayload);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonPayload));
                } catch (IOException e) {
                    log.error("Error sending message to WebSocket session {}", session.getId(), e);
                }
            }
        }
    }
}