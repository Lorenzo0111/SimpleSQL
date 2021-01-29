package me.lorenzo0111.mysql;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings("unused")
public final class Connection {
    private final java.sql.Connection connection;

    /**
     * @param connection java.sql Connection to me.lorenzo0111 Connection
     */
    public Connection(java.sql.Connection connection) {
        this.connection = connection;
    }

    /**
     * @param host MySQL server host
     * @param port MySQL server port
     * @param database MySQL database
     */
    public Connection(String host, int port, String database) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database);
    }

    /**
     * @param host MySQL server host
     * @param port MySQL server port
     * @param database MySQL database
     * @param username MySQL server username
     * @param password MySQL server password
     */
    public Connection(String host, int port, String database, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
    }

    /**
     * @return java.sql Connection
     */
    public java.sql.Connection getConnection() {
        return connection;
    }

    /**
     * @param query MySQL query
     * @return prepared statement
     */
    public PreparedStatement prepareStatement(String query) throws SQLException {
        return this.connection.prepareStatement(query);
    }
}
