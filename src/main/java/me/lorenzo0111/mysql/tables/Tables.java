package me.lorenzo0111.mysql.tables;

import me.lorenzo0111.mysql.Connection;

import java.sql.SQLException;

public final class Tables {

    public static Table fromName(String name, Connection connection) throws SQLException {
        return new Table(connection,name,false);
    }
}
