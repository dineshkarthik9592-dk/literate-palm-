package com.batchzero.model;

import java.time.LocalDateTime;

/**
 * Represents a formal recall campaign triggered by an incident.
 */
public class Recall {
    private String recallId;
    private String incidentId;
    private String batchId;
    private LocalDateTime startDate;
    private String reason;
    private String status; // INITIATED, ACTIVE, COMPLETED, CANCELLED

    public Recall(String recallId, String incidentId, String batchId, LocalDateTime startDate, String reason, String status) {
        this.recallId = recallId;
        this.incidentId = incidentId;
        this.batchId = batchId;
        this.startDate = startDate;
        this.reason = reason;
        this.status = status;
    }

    public String getRecallId() { return recallId; }
    public String getIncidentId() { return incidentId; }
    public String getBatchId() { return batchId; }
    public LocalDateTime getStartDate() { return startDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Recall{" + "recallId='" + recallId + '\'' + ", batchId='" + batchId + '\'' + ", status='" + status + '\'' + '}';
    }
}
