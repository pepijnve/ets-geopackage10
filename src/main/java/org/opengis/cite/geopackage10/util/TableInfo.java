package org.opengis.cite.geopackage10.util;

import java.util.Set;

public class TableInfo {
    public final String name;
    public final Set<ColumnInfo> columns;
    public final Set<ForeignKeyInfo> foreignKeys;

    public TableInfo(String name, Set<ColumnInfo> columns, Set<ForeignKeyInfo> foreignKeys) {
        this.name = name;
        this.columns = columns;
        this.foreignKeys = foreignKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableInfo tableInfo = (TableInfo) o;

        if (!columns.equals(tableInfo.columns)) return false;
        if (!foreignKeys.equals(tableInfo.foreignKeys)) return false;
        if (!name.equalsIgnoreCase(tableInfo.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.toUpperCase().hashCode();
        result = 31 * result + columns.hashCode();
        result = 31 * result + foreignKeys.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(name).append("\n");
        b.append("columns\n");
        for(ColumnInfo col : columns) {
            b.append("  ").append(col).append("\n");
        }
        b.append("foreign keys\n");
        for(ForeignKeyInfo key : foreignKeys) {
            b.append("  ").append(key).append("\n");
        }
        return b.toString();
    }
}
