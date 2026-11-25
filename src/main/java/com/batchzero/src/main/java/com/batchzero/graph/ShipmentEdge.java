package com.batchzero.graph;

import java.time.LocalDate;

/**
 * Directed edge representing a physical batch shipment from source to destination organization.
 */
public class ShipmentEdge {
    private final String shipmentId;
    private final String batchId;
    private final String sourceOrgId;
    private final String destinationOrgId;
    private final int quantity;
    private final LocalDate dispatchDate;

    public ShipmentEdge(String shipmentId, String batchId, String sourceOrgId, String destinationOrgId, int quantity, LocalDate dispatchDate) {
        this.shipmentId = shipmentId;
        this.batchId = batchId;
        this.sourceOrgId = sourceOrgId;
        this.destinationOrgId = destinationOrgId;
        this.quantity = quantity;
        this.dispatchDate = dispatchDate;
    }

    public String getShipmentId() { return shipmentId; }
    public String getBatchId() { return batchId; }
    public String getSourceOrgId() { return sourceOrgId; }
    public String getDestinationOrgId() { return destinationOrgId; }
    public int getQuantity() { return quantity; }
    public LocalDate getDispatchDate() { return dispatchDate; }

    @Override
    public String toString() {
        return sourceOrgId + " -> " + destinationOrgId + " (" + quantity + " units)";
    }
}
