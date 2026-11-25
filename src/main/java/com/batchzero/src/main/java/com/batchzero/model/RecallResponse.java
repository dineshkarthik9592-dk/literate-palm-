package com.batchzero.model;

import java.time.LocalDateTime;

/**
 * Tracks the response and quarantine execution at an individual affected downstream organization.
 */
public class RecallResponse {
    public enum QuarantineStatus {
        NOT_QUARANTINED, IN_PROGRESS, QUARANTINED
    }

    public enum ResponseStatus {
        NOTIFIED, ACKNOWLEDGED, ACTIONED, CLOSED
    }

    private String responseId;
    private String recallId;
    private String organizationId;
    private LocalDateTime notifiedAt;
    private LocalDateTime acknowledgedAt;
    private QuarantineStatus quarantineStatus;
    private ResponseStatus completionStatus;
    private int quarantinedQuantity;

    public RecallResponse(String responseId, String recallId, String organizationId, LocalDateTime notifiedAt) {
        this.responseId = responseId;
        this.recallId = recallId;
        this.organizationId = organizationId;
        this.notifiedAt = notifiedAt;
        this.quarantineStatus = QuarantineStatus.NOT_QUARANTINED;
        this.completionStatus = ResponseStatus.NOTIFIED;
        this.quarantinedQuantity = 0;
    }

    public String getResponseId() { return responseId; }
    public String getRecallId() { return recallId; }
    public String getOrganizationId() { return organizationId; }
    public LocalDateTime getNotifiedAt() { return notifiedAt; }
    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
    public QuarantineStatus getQuarantineStatus() { return quarantineStatus; }
    public void setQuarantineStatus(QuarantineStatus quarantineStatus) { this.quarantineStatus = quarantineStatus; }
    public ResponseStatus getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(ResponseStatus completionStatus) { this.completionStatus = completionStatus; }
    public int getQuarantinedQuantity() { return quarantinedQuantity; }
    public void setQuarantinedQuantity(int quarantinedQuantity) { this.quarantinedQuantity = quarantinedQuantity; }

    @Override
    public String toString() {
        return String.format("RecallResponse[Org: %s | Status: %s | Quarantine: %s | Qty: %d]",
                organizationId, completionStatus, quarantineStatus, quarantinedQuantity);
    }
}
