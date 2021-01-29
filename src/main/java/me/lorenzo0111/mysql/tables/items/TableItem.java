package me.lorenzo0111.mysql.tables.items;

@SuppressWarnings("unused")
public final class TableItem {
    private final String name;
    private final ItemType type;

    /**
     * @param name Item name
     * @param type Item type
     */
    public TableItem(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @return Item name
     */
    public String getName() {
        return name;
    }

    /**
     * @return Item type
     */
    public ItemType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TableItem{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
