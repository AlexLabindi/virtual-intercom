package com.alex.intercom.adapters.web;

import com.alex.intercom.adapters.web.dto.CallSessionResponseDto;
import com.alex.intercom.domain.CallSession;
import com.alex.intercom.ports.in.ManageCallUseCase;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller representing the Web Adapter driving port interactions from the gate.
 */
@RestController
@RequestMapping("/api/v1/intercom")
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
        // Retrieve the guest IP address from the incoming request
        String guestIp = request.getRemoteAddr();
        log.info("HTTP POST request received at /ring from remote IP: {}", guestIp);

        // Core business logic execution through Inbound Port
        CallSession session = manageCallUseCase.triggerRing(guestIp);

        // Return network optimized DTO
        CallSessionResponseDto responseDto = new CallSessionResponseDto(
                session.getId(),
                session.getToken(),
                session.getStatus()
        );

        return ResponseEntity.ok(responseDto);
    }
}