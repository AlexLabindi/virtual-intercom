package com.alex.intercom.adapters.persistence.entities;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "row_id") // Chiave primaria della riga autoincrementale
    private Long rowId;
    @Column(name = "id", nullable = false)
    private UUID sessionId;
    @Column(name = "guest_ip")
    private String guestIp;
    private String token;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}