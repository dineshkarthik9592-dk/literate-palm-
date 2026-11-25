package com.batchzero.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages JDBC connections to MySQL database with parameterized security.
 */
public class DBConnection {
    // Database credentials configured via environment or external properties
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/batchzero_db");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "batchzero_user");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASS", "secure_password");

    private static Connection connection = null;

    private DBConnection() {}

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                // If MySQL driver not on classpath during standalone CLI testing, fallback gracefully
                throw new SQLException("MySQL JDBC Driver not found on classpath: " + e.getMessage());
            }
        }
        return connection;
    }
}
