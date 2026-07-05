package com.alex.intercom.adapters.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Data Transfer Object representing the network response after a successful ring.
 */
@Getter
@AllArgsConstructor
public class CallSessionResponseDto {
    private final UUID sessionId;
    private final String token;
    private final String status;
}