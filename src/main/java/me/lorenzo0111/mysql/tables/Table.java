package me.lorenzo0111.mysql.tables;

import me.lorenzo0111.mysql.Connection;
import me.lorenzo0111.mysql.tables.items.Item;
import me.lorenzo0111.mysql.tables.items.ItemType;
import me.lorenzo0111.mysql.tables.items.TableItem;
import me.lorenzo0111.mysql.tables.query.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class Table {
    private final Connection connection;
    private final String name;
    private final List<TableItem> items;

    /**
     * @param connection MySQL connection
     * @param name Table name
     * @param items Table items
     */
    public Table(@NotNull Connection connection, @NotNull String name, @Nullable TableItem... items) throws SQLException {
        this.connection = connection;
        this.name = name;
        this.items = Arrays.asList(items);

        this.createTable();
    }

    /**
     * @param connection MySQL connection
     * @param name Table name
     * @param create Create table
     */
    public Table(@NotNull Connection connection, @NotNull String name, boolean create) throws SQLException {
        this.connection = connection;
        this.name = name;

        if (create) {
            this.createTable();
        }

        final List<TableItem> items = new ArrayList<>();
        final PreparedStatement statement = this.connection.prepareStatement("DESCRIBE " + this.name + ";");
        final ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            if (!resultSet.getString("field").equals("id")) {
                ItemType type = ItemType.TEXT;
                if (resultSet.getString("type").contains("int")) {
                    type = ItemType.INT;
                }

                items.add(new TableItem(resultSet.getString("field"), type));
            }
        }

        this.items = items;
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
        final StringBuilder stringBuilder = new StringBuilder();

        for (TableItem item : elements) {
            stringBuilder.append(item.getName()).append(",");
        }

        final String items = new StringBuffer(stringBuilder).deleteCharAt(stringBuilder.length() - 1).toString();

        final PreparedStatement statement = this.connection.prepareStatement("SELECT " + items + " FROM " + name + ";");

        return statement.executeQuery();
    }

    /**
     * @param conditions List of conditions
     * @return Result
     */
    public ResultSet selectWhere(Item... conditions) throws SQLException {
        final StringBuilder stringBuilder = new StringBuilder("SELECT * FROM " + this.name + " WHERE ");

        for (Item item : conditions) {
            stringBuilder.append(item.getName()).append(" = '").append(item.getValue()).append("' AND ");
        }

        final PreparedStatement statement = this.connection.prepareStatement(new StringBuffer(stringBuilder).delete(stringBuilder.length() - 5, stringBuilder.length()) + ";");

        return statement.executeQuery();
    }

    /**
     * @return List of TableItems of the table
     */
    public List<TableItem> getStructure() {
        return items;
    }

    /**
     * @param elements Elements to add in the columns order
     * @return Result enum
     */
    public Result add(Item... elements) throws SQLException {
        final StringBuilder stringBuilder = new StringBuilder();

        if (elements.length != this.items.size()) {
            if (elements.length < this.items.size()) {
                return Result.TOO_FEW_ITEMS;
            }

            return Result.TOO_MANY_ITEMS;
        }

        for (TableItem item : this.items) {
            stringBuilder.append("`").append(item.getName()).append("`").append(",");
        }

        final StringBuilder valuesBuilder = new StringBuilder();

        for (int i = 0; i < this.items.size(); i++) {
            valuesBuilder.append("?").append(",");
        }

        final String items = new StringBuffer(stringBuilder).deleteCharAt(stringBuilder.length() - 1).toString();
        String values = "";

        if (!valuesBuilder.toString().equals("")) {
            values = new StringBuffer(valuesBuilder).deleteCharAt(valuesBuilder.length() - 1).toString();
        }

        final PreparedStatement statement = this.connection.prepareStatement("INSERT INTO " + this.name + " (" + items + ")" +
                "VALUES (" + values + "); ");

        for (int i = 0; i < this.items.size(); i++) {
            if (!elements[i].getName().equals(this.items.get(i).getName())) {
                return Result.INVALID_ORDER;
            }

            statement.setObject(i+1, elements[i].getValue());
        }

        if (statement.executeUpdate() == 1) {
            return Result.SUCCESS;
        } else {
            return Result.ERROR;
        }
    }
}
