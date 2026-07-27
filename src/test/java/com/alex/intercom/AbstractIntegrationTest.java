package com.alex.intercom;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

    // Configura e avvia un vero container Postgres 15 isolato in Docker
    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("intercom_db_test")
            .withUsername("test_user")
            .withPassword("test_pass");

    // Inietta dinamicamente gli indirizzi del DB Docker nelle proprietà di Spring Boot
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Assicura che Hibernate aggiorni lo schema sul DB di test
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }
}