package com.alex.intercom.adapters.persistence;

import com.alex.intercom.AbstractIntegrationTest;
import com.alex.intercom.domain.CallSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CallSessionPersistenceAdapterTest extends AbstractIntegrationTest {

    @Autowired
    private CallSessionPersistenceAdapter persistenceAdapter;

    @Test
    @DisplayName("Dovrebbe salvare correttamente una sessione nel DB PostgreSQL reale e recuperarla per ID")
    void shouldSaveAndFindCallSessionInRealPostgres() {
        // GIVEN: Una nuova sessione di dominio pura
        UUID sessionId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        CallSession newSession = new CallSession(
                sessionId,
                "192.168.1.100",
                "test-secure-token",
                "ACTIVE",
                now,
                now.plusMinutes(5)
        );

        // WHEN: Salviamo tramite l'Outbound Adapter
        CallSession savedSession = persistenceAdapter.save(newSession);

        // THEN: Verifichiamo che i dati restituiti coincidano
        assertThat(savedSession).isNotNull();
        assertThat(savedSession.getId()).isEqualTo(sessionId);
        assertThat(savedSession.getStatus()).isEqualTo("ACTIVE");

        // AND WHEN: Cerchiamo la sessione dal DB reale
        Optional<CallSession> foundSession = persistenceAdapter.findById(sessionId);

        // THEN: La sessione deve essere presente nel Database
        assertThat(foundSession).isPresent();
        assertThat(foundSession.get().getGuestIp()).isEqualTo("192.168.1.100");
    }
}