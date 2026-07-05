package com.alex.intercom.adapters.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity mapping the 'call_sessions' table in PostgreSQL.
 */
@Entity
@Table(name = "call_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallSessionEntity {

    @Id
    private UUID id;

    private String guestIp;
    private String token;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}