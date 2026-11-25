package com.batchzero.model;

import java.time.LocalDateTime;

/**
 * Immutable audit trail event preserving system accountability.
 */
public class AuditEvent {
    private String eventId;
    private String userId;
    private String entityType;
    private String entityId;
    private String action;
    private LocalDateTime eventTime;
    private String notes;

    public AuditEvent(String eventId, String userId, String entityType, String entityId, String action, LocalDateTime eventTime, String notes) {
        this.eventId = eventId;
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.eventTime = eventTime;
        this.notes = notes;
    }

    public String getEventId() { return eventId; }
    public String getUserId() { return userId; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getAction() { return action; }
    public LocalDateTime getEventTime() { return eventTime; }
    public String getNotes() { return notes; }

    @Override
    public String toString() {
        return String.format("[%s] User '%s' performed '%s' on %s:%s - %s",
                eventTime, userId, action, entityType, entityId, notes);
    }
}
