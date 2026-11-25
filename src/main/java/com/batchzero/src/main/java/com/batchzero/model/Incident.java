package com.batchzero.model;

import java.time.LocalDateTime;

/**
 * Represents a user-reported quality, packaging, or safety incident.
 */
public class Incident {
    private String incidentId;
    private String batchId;
    private LocalDateTime reportDate;
    private String description;
    private String reportedBy;
    private String status; // OPEN, UNDER_INVESTIGATION, RESOLVED, DISMISSED

    public Incident(String incidentId, String batchId, LocalDateTime reportDate, String description, String reportedBy, String status) {
        this.incidentId = incidentId;
        this.batchId = batchId;
        this.reportDate = reportDate;
        this.description = description;
        this.reportedBy = reportedBy;
        this.status = status;
    }

    public String getIncidentId() { return incidentId; }
    public String getBatchId() { return batchId; }
    public LocalDateTime getReportDate() { return reportDate; }
    public String getDescription() { return description; }
    public String getReportedBy() { return reportedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Incident{" + "incidentId='" + incidentId + '\'' + ", batchId='" + batchId + '\'' + ", status='" + status + '\'' + '}';
    }
}
