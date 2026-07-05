package com.alex.intercom.adapters.persistence.repositories;

import com.alex.intercom.adapters.persistence.entities.CallLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository for CallLogEntity.
 */
public interface JpaCallLogRepository extends JpaRepository<CallLogEntity, Long> {
}