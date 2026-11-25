package com.batchzero.graph;

import java.util.*;

/**
 * Adjacency-list directed graph representing supply-chain organization vertices
 * and batch shipment directed edges.
 */
public class ShipmentGraph {
    private final Map<String, List<ShipmentEdge>> adjacencyList;
    private final Set<String> nodes;

    public ShipmentGraph() {
        this.adjacencyList = new HashMap<>();
        this.nodes = new HashSet<>();
    }

    public void addNode(String organizationId) {
        nodes.add(organizationId);
        adjacencyList.putIfAbsent(organizationId, new ArrayList<>());
    }

    public void addEdge(ShipmentEdge edge) {
        addNode(edge.getSourceOrgId());
        addNode(edge.getDestinationOrgId());
        adjacencyList.get(edge.getSourceOrgId()).add(edge);
    }

    public List<ShipmentEdge> getOutgoingEdges(String organizationId) {
        return adjacencyList.getOrDefault(organizationId, Collections.emptyList());
    }

    public Set<String> getAllNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        int count = 0;
        for (List<ShipmentEdge> edges : adjacencyList.values()) {
            count += edges.size();
        }
        return count;
    }

    /**
     * Builds a batch-filtered subgraph containing only shipments of a specific batch.
     */
    public static ShipmentGraph buildFilteredGraph(List<ShipmentEdge> allEdges, String targetBatchId) {
        ShipmentGraph subgraph = new ShipmentGraph();
        for (ShipmentEdge edge : allEdges) {
            if (edge.getBatchId().equalsIgnoreCase(targetBatchId)) {
                subgraph.addEdge(edge);
            }
        }
        return subgraph;
    }
}
