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

    // Lista thread-safe per tenere traccia di tutte le schede/dispositivi connessi
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("Nuova connessione WebSocket stabilita. ID Sessione: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Messaggio WebSocket ricevuto dal client: {}", payload);

        // 🔥 SE È UN MESSAGGIO DI CHAT, FAI IL BROADCAST IMMEDIATO A TUTTI
        if (payload.contains("\"event\":\"chat\"") || payload.contains("\"event\": \"chat\"")) {
            broadcastNotification(payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("Connessione WebSocket chiusa per ID: {}", session.getId());
    }

    /**
     * Invia un messaggio a tutti i client connessi
     */
    public void broadcastNotification(String jsonPayload) {
        log.info("Inizializzazione broadcast WebSocket per payload: {}", jsonPayload);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonPayload));
                } catch (IOException e) {
                    log.error("Errore durante l'invio del messaggio alla sessione WebSocket {}", session.getId(), e);
                }
            }
        }
    }
}