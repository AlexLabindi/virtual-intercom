package com.alex.intercom.adapters.web;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Controller Advice to catch infrastructure and security exceptions globally.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Catches Resilience4j Rate Limiter blocks and maps them to HTTP 429 Too Many Requests.
     */
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Map<String, String>> handleRateLimit(RequestNotPermitted ex) {
        log.warn("Rate limit triggered! Blocking suspicious flood activity from the gate.");

        Map<String, String> errorResponse = Map.of(
                "error", "Too Many Requests",
                "message", "You have rung the bell too many times. Please wait a minute before trying again."
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }
}