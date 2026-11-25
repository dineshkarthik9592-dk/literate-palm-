package com.batchzero.dao;

import com.batchzero.graph.ShipmentEdge;
import com.batchzero.model.Shipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Shipment persistence and graph edge retrieval.
 */
public class ShipmentDAO {

    private static final String INSERT_SHIPMENT =
            "INSERT INTO shipments (shipment_id, batch_id, source_org_id, destination_org_id, quantity, dispatch_date) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_BATCH =
            "SELECT shipment_id, batch_id, source_org_id, destination_org_id, quantity, dispatch_date " +
            "FROM shipments WHERE batch_id = ? ORDER BY dispatch_date ASC";

    public boolean insertShipment(Connection conn, Shipment shipment) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SHIPMENT)) {
            stmt.setString(1, shipment.getShipmentId());
            stmt.setString(2, shipment.getBatchId());
            stmt.setString(3, shipment.getSourceOrgId());
            stmt.setString(4, shipment.getDestinationOrgId());
            stmt.setInt(5, shipment.getQuantity());
            stmt.setDate(6, Date.valueOf(shipment.getDispatchDate()));

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public List<ShipmentEdge> getEdgesForBatch(Connection conn, String batchId) throws SQLException {
        List<ShipmentEdge> edges = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_BATCH)) {
            stmt.setString(1, batchId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    edges.add(new ShipmentEdge(
                            rs.getString("shipment_id"),
                            rs.getString("batch_id"),
                            rs.getString("source_org_id"),
                            rs.getString("destination_org_id"),
                            rs.getInt("quantity"),
                            rs.getDate("dispatch_date").toLocalDate()
                    ));
                }
            }
        }
        return edges;
    }
}
