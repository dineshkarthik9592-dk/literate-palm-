package com.batchzero.service;

import java.util.*;

/**
 * Encapsulates the complete downstream blast-radius analysis computed by graph traversal.
 */
public class ImpactReport {
    private final String batchId;
    private final String originOrgId;
    private final List<String> reachableNodes;
    private final Map<String, Integer> hopDistances;
    private final Map<String, List<String>> paths;
    private final Map<String, Integer> receivedQuantities;
    private final List<String> traversalSteps;
    private long executionTimeNanos;

    public ImpactReport(String batchId, String originOrgId) {
        this.batchId = batchId;
        this.originOrgId = originOrgId;
        this.reachableNodes = new ArrayList<>();
        this.hopDistances = new HashMap<>();
        this.paths = new HashMap<>();
        this.receivedQuantities = new HashMap<>();
        this.traversalSteps = new ArrayList<>();
    }

    public void addDestination(String orgId, int distance, List<String> path, int quantity) {
        if (!reachableNodes.contains(orgId)) {
            reachableNodes.add(orgId);
        }
        hopDistances.put(orgId, distance);
        paths.put(orgId, new ArrayList<>(path));
        receivedQuantities.merge(orgId, quantity, Integer::sum);
    }

    public void logStep(String step) {
        traversalSteps.add(step);
    }

    public void setExecutionTimeNanos(long nanos) {
        this.executionTimeNanos = nanos;
    }

    public String getBatchId() { return batchId; }
    public String getOriginOrgId() { return originOrgId; }
    public List<String> getReachableNodes() { return Collections.unmodifiableList(reachableNodes); }
    public Map<String, Integer> getHopDistances() { return Collections.unmodifiableMap(hopDistances); }
    public Map<String, List<String>> getPaths() { return Collections.unmodifiableMap(paths); }
    public Map<String, Integer> getReceivedQuantities() { return Collections.unmodifiableMap(receivedQuantities); }
    public List<String> getTraversalSteps() { return Collections.unmodifiableList(traversalSteps); }
    public long getExecutionTimeNanos() { return executionTimeNanos; }

    public int getTotalUnitsImpacted() {
        return receivedQuantities.values().stream().mapToInt(Integer::intValue).sum();
    }
}
