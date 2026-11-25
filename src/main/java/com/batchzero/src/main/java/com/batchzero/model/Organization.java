package com.batchzero.model;

/**
 * Represents a supply-chain participant node in the distribution graph.
 */
public class Organization {
    public enum OrgType {
        MANUFACTURER, WAREHOUSE, DISTRIBUTOR, RETAILER
    }

    private String organizationId;
    private String name;
    private OrgType type;
    private String address;
    private String contact;

    public Organization(String organizationId, String name, OrgType type, String address, String contact) {
        this.organizationId = organizationId;
        this.name = name;
        this.type = type;
        this.address = address;
        this.contact = contact;
    }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public OrgType getType() { return type; }
    public void setType(OrgType type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return name + " [" + type + " - " + organizationId + "]";
    }
}
