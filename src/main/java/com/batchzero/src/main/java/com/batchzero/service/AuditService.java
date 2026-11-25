package com.batchzero.service;

import com.batchzero.model.AuditEvent;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages chronological, tamper-evident audit history records.
 */
public class AuditService {
    private final List<AuditEvent> auditLog = new ArrayList<>();
    private int eventCounter = 1000;

    public synchronized void recordEvent(String userId, String entityType, String entityId, String action, String notes) {
        String eventId = "EVT-" + (++eventCounter);
        AuditEvent event = new AuditEvent(eventId, userId, entityType, entityId, action, LocalDateTime.now(), notes);
        auditLog.add(event);
    }

    public List<AuditEvent> getAllEvents() {
        return Collections.unmodifiableList(auditLog);
    }

    public List<AuditEvent> getEventsByEntity(String entityType, String entityId) {
        List<AuditEvent> filtered = new ArrayList<>();
        for (AuditEvent ev : auditLog) {
            if (ev.getEntityType().equalsIgnoreCase(entityType) && ev.getEntityId().equalsIgnoreCase(entityId)) {
                filtered.add(ev);
            }
        }
        return filtered;
    }
}
