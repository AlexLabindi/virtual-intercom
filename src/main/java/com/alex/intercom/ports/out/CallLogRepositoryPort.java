package com.alex.intercom.ports.out;

import com.alex.intercom.domain.CallLog;
import java.util.List;

/**
 * Outbound port for managing CallLog persistence operations.
 */
public interface CallLogRepositoryPort {
    CallLog save(CallLog callLog);
    List<CallLog> findAll();
}