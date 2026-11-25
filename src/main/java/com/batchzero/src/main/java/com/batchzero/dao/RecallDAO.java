package com.batchzero.dao;

import com.batchzero.model.Recall;
import com.batchzero.model.RecallResponse;

import java.sql.*;
import java.util.List;

/**
 * Data Access Object for atomic, multi-table recall campaigns.
 * Utilizes JDBC transaction boundaries (commit/rollback) to guarantee ACID integrity.
 */
public class RecallDAO {

    private static final String INSERT_RECALL =
            "INSERT INTO recalls (recall_id, incident_id, start_date, reason, status) VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_RESPONSE =
            "INSERT INTO recall_responses (response_id, recall_id, organization_id, notified_at, quarantine_status, completion_status) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    public boolean createRecallWithResponses(Connection conn, Recall recall, List<RecallResponse> responses) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Insert master recall record
            try (PreparedStatement stmtRecall = conn.prepareStatement(INSERT_RECALL)) {
                stmtRecall.setString(1, recall.getRecallId());
                stmtRecall.setString(2, recall.getIncidentId());
                stmtRecall.setTimestamp(3, Timestamp.valueOf(recall.getStartDate()));
                stmtRecall.setString(4, recall.getReason());
                stmtRecall.setString(5, recall.getStatus());
                stmtRecall.executeUpdate();
            }

            // 2. Batch insert destination-level response tasks
            try (PreparedStatement stmtResp = conn.prepareStatement(INSERT_RESPONSE)) {
                for (RecallResponse r : responses) {
                    stmtResp.setString(1, r.getResponseId());
                    stmtResp.setString(2, r.getRecallId());
                    stmtResp.setString(3, r.getOrganizationId());
                    stmtResp.setTimestamp(4, Timestamp.valueOf(r.getNotifiedAt()));
                    stmtResp.setString(5, r.getQuarantineStatus().name());
                    stmtResp.setString(6, r.getCompletionStatus().name());
                    stmtResp.addBatch();
                }
                stmtResp.executeBatch();
            }

            conn.commit(); // Commit Transaction atomically
            return true;
        } catch (SQLException ex) {
            conn.rollback(); // Rollback on failure
            throw ex;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }
}
