package org.opengis.cite.geopackage10.util;

import java.util.List;
import java.util.ListIterator;

public class ForeignKeyInfo {
    public final List<String> fromColumns;
    public final String table;
    public final List<String> toColumns;

    public ForeignKeyInfo(List<String> fromColumns, String table, List<String> toColumns) {
        this.fromColumns = fromColumns;
        this.table = table;
        this.toColumns = toColumns;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append("(");
        for (int i = 0; i < fromColumns.size(); i++) {
            if (i > 0) {
                b.append(", ");
            }
            b.append(fromColumns.get(i));
        }
        b.append(") references ");
        b.append(table);
        b.append("(");
        for (int i = 0; i < toColumns.size(); i++) {
            if (i > 0) {
                b.append(", ");
            }
            b.append(toColumns.get(i));
        }
        b.append(")");

        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignKeyInfo that = (ForeignKeyInfo) o;

        if (!equals(fromColumns, that.fromColumns)) return false;
        if (!table.equals(that.table)) return false;
        if (!equals(toColumns, that.toColumns)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hashCode(fromColumns);
        result = 31 * result + table.toUpperCase().hashCode();
        result = 31 * result + hashCode(toColumns);
        return result;
    }

    private static int hashCode(List<String> strings) {
        int hashCode = 1;
        for (String s : strings)
            hashCode = 31 * hashCode + (s == null ? 0 : s.toUpperCase().hashCode());
        return hashCode;
    }

    public boolean equals(List<String> strings1, List<String> strings2) {
        if (strings1 == strings2)
            return true;

        ListIterator<String> e1 = strings1.listIterator();
        ListIterator<String> e2 = strings2.listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            String o1 = e1.next();
            String o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equalsIgnoreCase(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }
}
