package me.lorenzo0111.mysql.tables.items;

public final class Item {
    private final String name;
    private final Object value;

    /**
     * @param name Column item name
     * @param value Value
     */
    public Item(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return Column item name
     */
    public String getName() {
        return name;
    }

    /**
     * @return Value
     */
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
