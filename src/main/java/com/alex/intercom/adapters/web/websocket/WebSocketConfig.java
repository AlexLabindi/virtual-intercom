package com.alex.intercom.adapters.web.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Infrastructure configuration to enable and map WebSocket handlers for the WebRTC signaling server.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SignalingWebSocketHandler signalingWebSocketHandler;

    // Spring inietta qui automaticamente l'handler contrassegnato come @Component
    public WebSocketConfig(SignalingWebSocketHandler signalingWebSocketHandler) {
        this.signalingWebSocketHandler = signalingWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Espone l'endpoint di segnalazione e permette cross-origin da qualsiasi dominio (incluso il frontend Vite su 5173)
        registry.addHandler(signalingWebSocketHandler, "/ws/signaling")
                .setAllowedOrigins("*");
    }
}