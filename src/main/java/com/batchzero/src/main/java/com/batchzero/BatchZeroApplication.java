package com.batchzero;

import com.batchzero.graph.ShipmentEdge;
import com.batchzero.graph.ShipmentGraph;
import com.batchzero.model.*;
import com.batchzero.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Main application entry point for the BATCHZERO Traceability and Recall Management System.
 * Simulates the complete end-to-end verification scenario.
 */
public class BatchZeroApplication {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("               BATCHZERO: PRODUCT TRACEABILITY & RECALL ENGINE                 ");
        System.out.println("               Course: CSE 213 - Advanced Java Programming                     ");
        System.out.println("               SRM University-AP | Academic Year: 2026-27                      ");
        System.out.println("================================================================================\n");

        // 1. Initialize Services
        AuditService auditService = new AuditService();
        TraceabilityService traceService = new TraceabilityService();
        RecallService recallService = new RecallService(auditService);

        // 2. Setup Master Data (Product, Batch, Organizations)
        Product amoxicillin = new Product("PROD-001", "Amoxicillin 500mg Capsules", "Antibiotic", "500mg oral capsules");
        Batch batch101 = new Batch("BATCH-101", amoxicillin.getProductId(),
                LocalDate.of(2026, 8, 15), LocalDate.of(2027, 8, 15), "ACTIVE");

        Map<String, Organization> orgRegistry = new LinkedHashMap<>();
        orgRegistry.put("M1", new Organization("M1", "Apex Pharmaceuticals Facility", Organization.OrgType.MANUFACTURER, "Hyderabad, TS", "+91-40-23001122"));
        orgRegistry.put("W1", new Organization("W1", "Central Pharma Hub Guntur", Organization.OrgType.WAREHOUSE, "Guntur, AP", "+91-863-221100"));
        orgRegistry.put("W2", new Organization("W2", "Coastal Depot Vijayawada", Organization.OrgType.WAREHOUSE, "Vijayawada, AP", "+91-866-234567"));
        orgRegistry.put("D1", new Organization("D1", "MedLife Distributors Amaravati", Organization.OrgType.DISTRIBUTOR, "Amaravati, AP", "+91-863-445566"));
        orgRegistry.put("D2", new Organization("D2", "Vikas Wholesale Tirupati", Organization.OrgType.DISTRIBUTOR, "Tirupati, AP", "+91-877-556677"));
        orgRegistry.put("R1", new Organization("R1", "Apollo Pharmacy Store #12", Organization.OrgType.RETAILER, "Guntur, AP", "+91-863-998877"));
        orgRegistry.put("R2", new Organization("R2", "MedPlus Chemist #45", Organization.OrgType.RETAILER, "Vijayawada, AP", "+91-866-778899"));
        orgRegistry.put("R3", new Organization("R3", "City Care Health Mart", Organization.OrgType.RETAILER, "Mangalagiri, AP", "+91-8645-334455"));

        System.out.println("[STEP 1] Master Data Configured:");
        System.out.println("  Product: " + amoxicillin.getName() + " (ID: " + amoxicillin.getProductId() + ")");
        System.out.println("  Batch  : " + batch101.getBatchId() + " (Mfg: " + batch101.getManufactureDate() + ", Exp: " + batch101.getExpiryDate() + ")");
        System.out.println("  Registered Supply-Chain Nodes: " + orgRegistry.size() + " facilities.");
        auditService.recordEvent("SYS_ADMIN", "BATCH", batch101.getBatchId(), "REGISTER", "Batch BATCH-101 registered in registry.");

        // 3. Register Multi-Echelon Shipment Transactions
        List<ShipmentEdge> shipments = new ArrayList<>();
        shipments.add(new ShipmentEdge("SHP-001", "BATCH-101", "M1", "W1", 10000, LocalDate.of(2026, 8, 17)));
        shipments.add(new ShipmentEdge("SHP-002", "BATCH-101", "W1", "D1", 5000, LocalDate.of(2026, 8, 20)));
        shipments.add(new ShipmentEdge("SHP-003", "BATCH-101", "W1", "D2", 4000, LocalDate.of(2026, 8, 21)));
        shipments.add(new ShipmentEdge("SHP-004", "BATCH-101", "D1", "R1", 3000, LocalDate.of(2026, 8, 23)));
        shipments.add(new ShipmentEdge("SHP-005", "BATCH-101", "D1", "R2", 2000, LocalDate.of(2026, 8, 24)));
        shipments.add(new ShipmentEdge("SHP-006", "BATCH-101", "D2", "R3", 2500, LocalDate.of(2026, 8, 25)));

        System.out.println("\n[STEP 2] Shipment Network Logged (" + shipments.size() + " movements):");
        for (ShipmentEdge s : shipments) {
            System.out.printf("  [%s] %s (%s) ---> %s (%s) | Qty: %,d units | Date: %s%n",
                    s.getShipmentId(), s.getSourceOrgId(), orgRegistry.get(s.getSourceOrgId()).getType(),
                    s.getDestinationOrgId(), orgRegistry.get(s.getDestinationOrgId()).getType(),
                    s.getQuantity(), s.getDispatchDate());
        }

        // 4. Construct Batch-Specific Directed Graph
        ShipmentGraph batchGraph = ShipmentGraph.buildFilteredGraph(shipments, "BATCH-101");
        System.out.println("\n[STEP 3] Supply Chain Directed Graph Constructed:");
        System.out.println("  Total Vertices (Organizations) : " + batchGraph.getNodeCount());
        System.out.println("  Total Directed Edges (Shipments): " + batchGraph.getEdgeCount());

        // 5. User-Reported Incident
        System.out.println("\n[STEP 4] Logging Quality Incident Report:");
        Incident incident = new Incident("INC-301", "BATCH-101", LocalDateTime.now(),
                "Particulate discoloration reported during pharmacy shelf-inspection", "QA_INSPECTOR_04", "OPEN");
        System.out.println("  Incident ID : " + incident.getIncidentId());
        System.out.println("  Target Batch: " + incident.getBatchId());
        System.out.println("  Description : " + incident.getDescription());
        auditService.recordEvent("QA_INSPECTOR_04", "INCIDENT", incident.getIncidentId(), "REPORT", incident.getDescription());

        // 6. Execute BFS Downstream Traceability
        System.out.println("\n[STEP 5] Executing Breadth-First Search (BFS) Downstream Traceability Engine...");
        ImpactReport impact = traceService.traceDownstreamBFS(batchGraph, "M1", "BATCH-101");

        System.out.println("  --- Traversal Exploration Log ---");
        for (String step : impact.getTraversalSteps()) {
            System.out.println("   " + step);
        }

        System.out.println("\n  --- Downstream Impact Assessment Summary ---");
        System.out.printf("  Origin Facility       : %s (%s)%n", "M1", orgRegistry.get("M1").getName());
        System.out.printf("  Total Impacted Sites  : %d organizations%n", impact.getReachableNodes().size());
        System.out.printf("  Total Quantity Traced : %,d units%n", impact.getTotalUnitsImpacted());
        System.out.printf("  Graph Traversal Time  : %.3f ms%n", impact.getExecutionTimeNanos() / 1_000_000.0);
        System.out.println("\n  Reachable Destination Breakdown:");
        for (String orgId : impact.getReachableNodes()) {
            Organization org = orgRegistry.get(orgId);
            int hops = impact.getHopDistances().get(orgId);
            List<String> path = impact.getPaths().get(orgId);
            int qty = impact.getReceivedQuantities().get(orgId);
            System.out.printf("   • Node [%s] %-32s | Type: %-12s | Hops: %d | Qty: %,5d | Path: %s%n",
                    orgId, org.getName(), org.getType(), hops, qty, String.join(" -> ", path));
        }

        // 7. Initiate Formal Recall Campaign
        System.out.println("\n[STEP 6] Initiating Recall Campaign via RecallService:");
        Recall recall = recallService.initiateRecall(incident,
                "Voluntary recall due to reported particulate contamination risk",
                impact.getReachableNodes(), "REGULATORY_OFFICER_01");
        System.out.println("  Recall ID    : " + recall.getRecallId());
        System.out.println("  Reason       : " + recall.getReason());
        System.out.println("  Status       : " + recall.getStatus());
        System.out.println("  Generated Response Directives: " + recallService.getResponses(recall.getRecallId()).size() + " facilities.");

        // 8. Simulate Responses & Quarantining
        System.out.println("\n[STEP 7] Processing Facility Acknowledgments & Inventory Quarantines:");
        recallService.acknowledgeRecall(recall.getRecallId(), "W1", "MANAGER_W1");
        recallService.quarantineInventory(recall.getRecallId(), "W1", 1000, "MANAGER_W1");
        System.out.println("  -> Facility W1: ACKNOWLEDGED & QUARANTINED (1,000 units on hand).");

        recallService.acknowledgeRecall(recall.getRecallId(), "D1", "MANAGER_D1");
        recallService.quarantineInventory(recall.getRecallId(), "D1", 500, "MANAGER_D1");
        System.out.println("  -> Facility D1: ACKNOWLEDGED & QUARANTINED (500 units on hand).");

        recallService.acknowledgeRecall(recall.getRecallId(), "R1", "PHARMACIST_R1");
        recallService.quarantineInventory(recall.getRecallId(), "R1", 2800, "PHARMACIST_R1");
        System.out.println("  -> Facility R1: ACKNOWLEDGED & QUARANTINED (2,800 units sealed).");

        recallService.acknowledgeRecall(recall.getRecallId(), "R2", "PHARMACIST_R2");
        System.out.println("  -> Facility R2: ACKNOWLEDGED (Quarantine pending physical count).");

        // 9. Recall Command Center Live Metrics
        System.out.println("\n[STEP 8] Recall Command Center Operational Metrics:");
        Map<String, Object> metrics = recallService.getRecallMetrics(recall.getRecallId());
        System.out.printf("  Total Target Destinations : %s%n", metrics.get("totalDestinations"));
        System.out.printf("  Acknowledged Facilities   : %s (%.1f%%)%n", metrics.get("acknowledgedCount"), metrics.get("acknowledgedPercentage"));
        System.out.printf("  Quarantined Facilities    : %s (%.1f%%)%n", metrics.get("quarantinedCount"), metrics.get("quarantinedPercentage"));
        System.out.printf("  Total Units Quarantined   : %,d units%n", (int) metrics.get("totalQuarantinedUnits"));

        // 10. Audit Trail Review
        System.out.println("\n[STEP 9] Immutable Audit Log Verification (" + auditService.getAllEvents().size() + " events captured):");
        for (AuditEvent ev : auditService.getAllEvents()) {
            System.out.printf("  %s%n", ev);
        }

        System.out.println("\n================================================================================");
        System.out.println("               BATCHZERO EXECUTION & VERIFICATION SUCCESSFUL                    ");
        System.out.println("================================================================================");
    }
}
