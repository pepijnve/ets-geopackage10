package org.opengis.cite.geopackage10.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLUtils {
    public static int queryInt(Connection c, String query, Object... args) throws SQLException {
        return query(c, query, args, new ResultSetHandler<Number>() {
            @Override
            public Number handleResult(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("");
                }
            }
        }).intValue();
    }

    public static String queryString(Connection c, String query, Object... args) throws SQLException {
        return query(c, query, args, new ResultSetHandler<String>() {
            @Override
            public String handleResult(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    throw new SQLException("");
                }
            }
        });
    }

    public static List<String> queryStrings(Connection c, String query, Object... args) throws SQLException {
        return query(c, query, args, new ResultSetHandler<List<String>>() {
            @Override
            public List<String> handleResult(ResultSet rs) throws SQLException {
                List<String> res = new ArrayList<>();
                while (rs.next()) {
                    res.add(rs.getString(1));
                }
                return res;
            }
        });
    }

    public static <T> T query(Connection c, String query, Object[] args, ResultSetHandler<T> rsHandler) throws SQLException {
        PreparedStatement s = c.prepareStatement(query);

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                int paramIx = i + 1;
                Object arg = args[i];
                if (arg instanceof String) {
                    s.setString(paramIx, ((String) arg));
                } else if (arg instanceof Long || arg instanceof Integer) {
                    s.setLong(paramIx, ((Number) arg).longValue());
                } else if (arg instanceof Float || arg instanceof Double) {
                    s.setDouble(paramIx, ((Number) arg).doubleValue());
                } else if (arg instanceof byte[]) {
                    s.setBytes(paramIx, ((byte[]) arg));
                } else {
                    throw new IllegalArgumentException("Unsupported argument type: " + arg);
                }
            }
        }

        try {
            boolean hasResult = s.execute();
            if (hasResult) {
                ResultSet rs = s.getResultSet();
                try {
                    return rsHandler.handleResult(rs);
                } finally {
                    rs.close();
                }
            } else {
                return null;
            }
        } finally {
            s.close();
        }
    }

    public static boolean tableExists(Connection c, String table) throws SQLException {
        return queryInt(c, "SELECT count(*) FROM sqlite_master WHERE type = 'table' and name = ?", table) != 0;
    }

    public static TableInfo getTableInfo(Connection c, String table) throws SQLException {
        Set<ColumnInfo> columns = getColumnInfo(c, table);
        Set<ForeignKeyInfo> foreignKeys = getForeignKeyInfo(c, table);
        return new TableInfo(table, columns, foreignKeys);
    }

    private static Set<ForeignKeyInfo> getForeignKeyInfo(Connection c, String table) throws SQLException {
        Set<ForeignKeyInfo> result = query(c, "PRAGMA foreign_key_list('" + table + "')", null, new ResultSetHandler<Set<ForeignKeyInfo>>() {
            @Override
            public Set<ForeignKeyInfo> handleResult(ResultSet rs) throws SQLException {
                Map<Integer, String> targets = new LinkedHashMap<>();
                Map<Integer, List<String>> froms = new HashMap<>();
                Map<Integer, List<String>> tos = new HashMap<>();

                while (rs.next()) {
                    int id = rs.getInt(1);
                    String table = rs.getString(3);
                    String from = rs.getString(4);
                    String to = rs.getString(5);
                    if (!targets.containsKey(id)) {
                        targets.put(id, table);
                        froms.put(id, new ArrayList<String>());
                        tos.put(id, new ArrayList<String>());
                    }
                    froms.get(id).add(from);
                    tos.get(id).add(to);
                }

                Set<ForeignKeyInfo> res = new LinkedHashSet<>();

                for (Integer id : targets.keySet()) {
                    res.add(new ForeignKeyInfo(
                            froms.get(id),
                            targets.get(id),
                            tos.get(id)
                    ));
                }

                return res;
            }
        });
        return result != null ? result : Collections.<ForeignKeyInfo>emptySet();
    }

    private static Set<ColumnInfo> getColumnInfo(Connection c, String table) throws SQLException {
        return query(c, "PRAGMA table_info('" + table + "')", null, new ResultSetHandler<Set<ColumnInfo>>() {
            @Override
            public Set<ColumnInfo> handleResult(ResultSet rs) throws SQLException {
                Set<ColumnInfo> res = new LinkedHashSet<>();
                while (rs.next()) {
                    String defaultValue = rs.getString(5);

                    res.add(new ColumnInfo(
                            rs.getString(2),
                            rs.getString(3),
                            rs.getBoolean(4),
                            defaultValue,
                            rs.getInt(6)
                    ));
                }
                return res;
            }
        });
    }

    public static String foreignKeyCheck(Connection connection) throws SQLException {
        return foreignKeyCheck(connection, null);
    }

    public static String foreignKeyCheck(Connection connection, String table) throws SQLException {
        String query = "PRAGMA foreign_key_check";
        if (table != null) {
            query += "(" + table + ")";
        }

        return query(connection, query, new Object[0], new ResultSetHandler<String>() {
            @Override
            public String handleResult(ResultSet rs) throws SQLException {
                StringBuilder b = new StringBuilder();
                while (rs.next()) {
                    String tableName = rs.getString(1);
                    long rowId = rs.getLong(2);
                    String otherTableName = rs.getString(3);

                    b.append(tableName + " row " + rowId + " contains invalid reference to " + otherTableName);
                }

                return b.length() == 0 ? null : b.toString();
            }
        });
    }

    public static interface ResultSetHandler<T> {
        T handleResult(ResultSet rs) throws SQLException;
    }
}
