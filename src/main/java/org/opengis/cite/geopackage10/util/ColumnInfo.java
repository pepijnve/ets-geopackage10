package org.opengis.cite.geopackage10.util;


public class ColumnInfo {
    public final String name;
    public final String type;
    public final boolean notNull;
    public final String defaultValue;
    public final int primaryKeyIndex;

    public ColumnInfo(String name, String type, boolean notNull, String defaultValue, int primaryKeyIndex) {
        this.name = name;
        this.type = type;
        this.notNull = notNull;
        this.defaultValue = defaultValue;
        this.primaryKeyIndex = primaryKeyIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnInfo that = (ColumnInfo) o;

        if (notNull != that.notNull) return false;
        if (primaryKeyIndex != that.primaryKeyIndex) return false;
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) return false;
        if (!name.equalsIgnoreCase(that.name)) return false;
        if (!type.equalsIgnoreCase(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.toUpperCase().hashCode();
        result = 31 * result + type.toUpperCase().hashCode();
        result = 31 * result + (notNull ? 1 : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + primaryKeyIndex;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append(name);

        b.append(' ').append(type);

        if (notNull) {
            b.append(" NOT NULL");
        }

        if (defaultValue != null) {
            b.append(" DEFAULT ").append(defaultValue);
        }

        if (primaryKeyIndex > 0) {
            b.append(" PRIMARY KEY");
        }

        return b.toString();
    }
}
