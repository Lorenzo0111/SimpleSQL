package me.lorenzo0111.mysql.tables;

import me.lorenzo0111.mysql.Connection;
import me.lorenzo0111.mysql.tables.items.ItemType;
import me.lorenzo0111.mysql.tables.items.TableItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public final class Table {
    private final Connection connection;
    private final String name;
    private final TableItem[] items;

    /**
     * @param connection MySQL connection
     * @param name Table name
     * @param items Table items
     */
    public Table(@NotNull Connection connection, @NotNull String name, @Nullable TableItem... items) throws SQLException {
        this.connection = connection;
        this.name = name;
        this.items = items;

        this.createTable();
    }

    public Table(@NotNull Connection connection, @NotNull String name, boolean create) throws SQLException {
        this.connection = connection;
        this.name = name;
        this.items = null;

        if (create) {
            this.createTable();
        }
    }

    private void createTable() throws SQLException {
        final PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + name + " (" +
                "`id` int(11) NOT NULL AUTO_INCREMENT," +
                "PRIMARY KEY (`id`));");

        statement.executeUpdate();

        for (TableItem item : items) {

            this.processItemStatement(item);

        }
    }

    private void processItemStatement(TableItem item) throws SQLException {
        final PreparedStatement editStatement;

        if (item.getType().equals(ItemType.INT)) {
            editStatement = connection.prepareStatement("ALTER TABLE " + name +
                    " ADD " + item.getName() + " int(11);");
        } else {
            editStatement = connection.prepareStatement("ALTER TABLE " + name +
                    " ADD " + item.getName() + " TEXT;");
        }

        editStatement.executeUpdate();
    }

    /**
     * Add a column to the table
     * @param item Item to add
     */
    public void addColumn(TableItem item) throws SQLException {
        this.processItemStatement(item);
    }

    /**
     * @return Content of the table
     */
    public ResultSet selectAll() throws SQLException {
        final PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + name + ";");
        return statement.executeQuery();
    }

    /**
     * @param elements Items to get
     * @return Selected elements from the table
     */
    public ResultSet select(TableItem... elements) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder();

        for (TableItem item : elements) {
            stringBuilder.append(item.getName()).append(",");
        }

        final String items = new StringBuffer(stringBuilder).deleteCharAt(stringBuilder.length() - 1).toString();

        PreparedStatement statement = this.connection.prepareStatement("SELECT " + items + " FROM " + name + ";");

        return statement.executeQuery();
    }
}
