package com.batchzero.model;

import java.time.LocalDate;

/**
 * Represents a discrete manufacturing production batch.
 */
public class Batch {
    private String batchId;
    private String productId;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private String status; // ACTIVE, QUARANTINED, RECALLED, EXPIRED

    public Batch(String batchId, String productId, LocalDate manufactureDate, LocalDate expiryDate, String status) {
        this.batchId = batchId;
        this.productId = productId;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Batch{" + "batchId='" + batchId + '\'' + ", productId='" + productId + '\'' + ", status='" + status + '\'' + '}';
    }
}
