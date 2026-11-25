package com.batchzero.service;

import com.batchzero.model.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Coordinates recall initiation, response tracking, and status aggregation.
 */
public class RecallService {
    private final Map<String, Recall> recallRegistry = new HashMap<>();
    private final Map<String, List<RecallResponse>> responseRegistry = new HashMap<>();
    private final AuditService auditService;
    private int recallCounter = 2000;
    private int responseCounter = 5000;

    public RecallService(AuditService auditService) {
        this.auditService = auditService;
    }

    public Recall initiateRecall(Incident incident, String reason, List<String> affectedOrgIds, String initiatorUserId) {
        String recallId = "RC-" + (++recallCounter);
        Recall recall = new Recall(recallId, incident.getIncidentId(), incident.getBatchId(),
                LocalDateTime.now(), reason, "ACTIVE");

        recallRegistry.put(recallId, recall);
        List<RecallResponse> responses = new ArrayList<>();

        for (String orgId : affectedOrgIds) {
            String respId = "RESP-" + (++responseCounter);
            RecallResponse response = new RecallResponse(respId, recallId, orgId, LocalDateTime.now());
            responses.add(response);
        }
        responseRegistry.put(recallId, responses);

        auditService.recordEvent(initiatorUserId, "RECALL", recallId, "INITIATE",
                "Initiated recall for batch " + incident.getBatchId() + " targeting " + affectedOrgIds.size() + " destinations.");

        return recall;
    }

    public void acknowledgeRecall(String recallId, String orgId, String userId) {
        List<RecallResponse> responses = responseRegistry.get(recallId);
        if (responses != null) {
            for (RecallResponse r : responses) {
                if (r.getOrganizationId().equals(orgId)) {
                    r.setAcknowledgedAt(LocalDateTime.now());
                    r.setCompletionStatus(RecallResponse.ResponseStatus.ACKNOWLEDGED);
                    auditService.recordEvent(userId, "RECALL_RESPONSE", r.getResponseId(), "ACKNOWLEDGE",
                            "Organization " + orgId + " acknowledged recall notification.");
                    return;
                }
            }
        }
    }

    public void quarantineInventory(String recallId, String orgId, int quantity, String userId) {
        List<RecallResponse> responses = responseRegistry.get(recallId);
        if (responses != null) {
            for (RecallResponse r : responses) {
                if (r.getOrganizationId().equals(orgId)) {
                    r.setQuarantineStatus(RecallResponse.QuarantineStatus.QUARANTINED);
                    r.setQuarantinedQuantity(quantity);
                    r.setCompletionStatus(RecallResponse.ResponseStatus.ACTIONED);
                    auditService.recordEvent(userId, "RECALL_RESPONSE", r.getResponseId(), "QUARANTINE",
                            "Quarantined " + quantity + " units at organization " + orgId + ".");
                    return;
                }
            }
        }
    }

    public List<RecallResponse> getResponses(String recallId) {
        return responseRegistry.getOrDefault(recallId, Collections.emptyList());
    }

    public Map<String, Object> getRecallMetrics(String recallId) {
        List<RecallResponse> responses = getResponses(recallId);
        int total = responses.size();
        if (total == 0) return Collections.emptyMap();

        long acknowledgedCount = responses.stream()
                .filter(r -> r.getCompletionStatus() != RecallResponse.ResponseStatus.NOTIFIED).count();
        long quarantinedCount = responses.stream()
                .filter(r -> r.getQuarantineStatus() == RecallResponse.QuarantineStatus.QUARANTINED).count();
        int totalQuarantinedUnits = responses.stream()
                .mapToInt(RecallResponse::getQuarantinedQuantity).sum();

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalDestinations", total);
        metrics.put("acknowledgedCount", acknowledgedCount);
        metrics.put("acknowledgedPercentage", (acknowledgedCount * 100.0) / total);
        metrics.put("quarantinedCount", quarantinedCount);
        metrics.put("quarantinedPercentage", (quarantinedCount * 100.0) / total);
        metrics.put("totalQuarantinedUnits", totalQuarantinedUnits);
        return metrics;
    }
}
