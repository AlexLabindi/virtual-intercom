package com.alex.intercom.adapters.web;

import com.alex.intercom.adapters.web.dto.CallSessionResponseDto;
import com.alex.intercom.domain.CallSession;
import com.alex.intercom.ports.in.ManageCallUseCase;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller representing the Web Adapter driving port interactions from the gate.
 */
@RestController
@RequestMapping("/api/intercom")
@CrossOrigin(origins = "http://localhost:5173")
public class IntercomGatewayController {

    private static final Logger log = LoggerFactory.getLogger(IntercomGatewayController.class);
    private final ManageCallUseCase manageCallUseCase;

    public IntercomGatewayController(ManageCallUseCase manageCallUseCase) {
        this.manageCallUseCase = manageCallUseCase;
    }

    /**
     * Endpoint triggered when a guest scans the QR code and clicks "Ring".
     * Protected by Resilience4j Rate Limiter (Max 3 requests per minute per IP).
     */
    @PostMapping("/ring")
    @RateLimiter(name = "gateRateLimiter")
    public ResponseEntity<CallSessionResponseDto> ringTheBell(HttpServletRequest request) {
        String guestIp = request.getRemoteAddr();
        log.info("HTTP POST request received at /ring from remote IP: {}", guestIp);

        CallSession session = manageCallUseCase.triggerRing(guestIp);//invoca la porta di ingresso (Inbound Port) chiamando il metodo del caso d'uso

        CallSessionResponseDto responseDto = new CallSessionResponseDto(
                session.getId(),
                session.getToken(),
                session.getStatus()
        );

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 🔥 Endpoint per accettare la chiamata dalla Home Dashboard.
     * Mappato su: /api/intercom/calls/{sessionId}/accept
     */
    @PostMapping("/calls/{sessionId}/accept")
    public ResponseEntity<Void> acceptCall(@PathVariable UUID sessionId) {
        log.info("HTTP POST request received to ACCEPT call session: {}", sessionId);

        // Esegue il caso d'uso dell'accettazione nel core domain
        manageCallUseCase.acceptCall(sessionId);

        return ResponseEntity.ok().build();
    }

    /**
     * 🔥 Endpoint per rifiutare o terminare la chiamata (sia da Dashboard che da Gate).
     * Mappato su: /api/intercom/calls/{sessionId}/terminate
     */
    @PostMapping("/calls/{sessionId}/terminate")
    public ResponseEntity<Void> terminateCall(
            @PathVariable UUID sessionId,
            @RequestParam(defaultValue = "TERMINATED") String reason) {
        log.info("HTTP POST request received to TERMINATE call session: {} due to: {}", sessionId, reason);

        // Esegue il caso d'uso della terminazione archiviando i log
        manageCallUseCase.terminateCall(sessionId, reason);

        return ResponseEntity.ok().build();
    }
}