package com.batchzero.model;

import java.time.LocalDate;

/**
 * Represents a catalog product entity in the BATCHZERO platform.
 */
public class Product {
    private String productId;
    private String name;
    private String category;
    private String description;

    public Product(String productId, String name, String category, String description) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Product{" + "productId='" + productId + '\'' + ", name='" + name + '\'' + ", category='" + category + '\'' + '}';
    }
}
