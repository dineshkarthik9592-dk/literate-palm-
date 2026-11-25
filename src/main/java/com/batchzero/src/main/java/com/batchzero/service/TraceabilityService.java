package com.batchzero.service;

import com.batchzero.graph.ShipmentEdge;
import com.batchzero.graph.ShipmentGraph;

import java.util.*;

/**
 * Core algorithmic engine of BATCHZERO. Executes graph traversal
 * to discover all downstream recipients of an affected product batch.
 */
public class TraceabilityService {

    /**
     * Executes Breadth-First Search (BFS) to identify all downstream organizations
     * reachable from the starting origin for the specified batch.
     *
     * Complexity: O(V + E) where V is the number of organizations and E is the number of shipment edges.
     *
     * @param graph The batch-specific shipment graph
     * @param startOrgId The origin manufacturer or source facility
     * @param batchId The batch identifier under investigation
     * @return An ImpactReport containing ordered destinations, hop distances, and paths
     */
    public ImpactReport traceDownstreamBFS(ShipmentGraph graph, String startOrgId, String batchId) {
        long startTime = System.nanoTime();
        ImpactReport report = new ImpactReport(batchId, startOrgId);

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, List<String>> pathToNode = new HashMap<>();
        Map<String, Integer> distanceMap = new HashMap<>();

        // Enqueue and mark the starting origin
        queue.add(startOrgId);
        visited.add(startOrgId);
        pathToNode.put(startOrgId, Collections.singletonList(startOrgId));
        distanceMap.put(startOrgId, 0);

        report.logStep("Initiated BFS from root node: " + startOrgId + " for Batch: " + batchId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDist = distanceMap.get(current);
            List<String> currentPath = pathToNode.get(current);

            List<ShipmentEdge> outgoing = graph.getOutgoingEdges(current);
            report.logStep(String.format("Visiting Node [%s] (Level %d) - Found %d outgoing shipment(s)",
                    current, currentDist, outgoing.size()));

            for (ShipmentEdge edge : outgoing) {
                String destination = edge.getDestinationOrgId();

                if (!visited.contains(destination)) {
                    visited.add(destination);
                    queue.add(destination);

                    int newDist = currentDist + 1;
                    distanceMap.put(destination, newDist);

                    List<String> newPath = new ArrayList<>(currentPath);
                    newPath.add(destination);
                    pathToNode.put(destination, newPath);

                    report.addDestination(destination, newDist, newPath, edge.getQuantity());
                    report.logStep(String.format("  -> Discovered new downstream node [%s] via edge (%s -> %s, qty: %d)",
                            destination, current, destination, edge.getQuantity()));
                } else {
                    // Record multiple shipment links to an already visited destination
                    report.addDestination(destination, distanceMap.get(destination), pathToNode.get(destination), edge.getQuantity());
                    report.logStep(String.format("  -> Additional shipment edge to existing node [%s] (qty: %d)",
                            destination, edge.getQuantity()));
                }
            }
        }

        report.setExecutionTimeNanos(System.nanoTime() - startTime);
        report.logStep(String.format("BFS Complete. Total downstream organizations identified: %d. Total units impacted: %d.",
                report.getReachableNodes().size(), report.getTotalUnitsImpacted()));

        return report;
    }

    /**
     * Executes Depth-First Search (DFS) for comparative deep-path traversal.
     */
    public List<String> traceDownstreamDFS(ShipmentGraph graph, String startOrgId) {
        List<String> traversalOrder = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfsHelper(graph, startOrgId, visited, traversalOrder);
        return traversalOrder;
    }

    private void dfsHelper(ShipmentGraph graph, String current, Set<String> visited, List<String> order) {
        visited.add(current);
        order.add(current);
        for (ShipmentEdge edge : graph.getOutgoingEdges(current)) {
            String next = edge.getDestinationOrgId();
            if (!visited.contains(next)) {
                dfsHelper(graph, next, visited, order);
            }
        }
    }
}
