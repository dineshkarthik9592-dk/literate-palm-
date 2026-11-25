package com.batchzero.model;

import java.time.LocalDate;

/**
 * Represents a recorded movement of a batch between two organizations.
 */
public class Shipment {
    private String shipmentId;
    private String batchId;
    private String sourceOrgId;
    private String destinationOrgId;
    private int quantity;
    private LocalDate dispatchDate;

    public Shipment(String shipmentId, String batchId, String sourceOrgId, String destinationOrgId, int quantity, LocalDate dispatchDate) {
        if (sourceOrgId.equals(destinationOrgId)) {
            throw new IllegalArgumentException("Source and destination organization cannot be identical.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Shipment quantity must be strictly positive.");
        }
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
        return String.format("Shipment[%s: %s -> %s | Qty: %d | Date: %s]",
                shipmentId, sourceOrgId, destinationOrgId, quantity, dispatchDate);
    }
}
