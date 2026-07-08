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

    // Spring automatically injects the handler marked as @Component here.
    public WebSocketConfig(SignalingWebSocketHandler signalingWebSocketHandler) {
        this.signalingWebSocketHandler = signalingWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Exposes the reporting endpoint and allows cross-origin from any domain (including the Vite frontend on 5173)
        registry.addHandler(signalingWebSocketHandler, "/ws/signaling")
                .setAllowedOrigins("*");
    }
}