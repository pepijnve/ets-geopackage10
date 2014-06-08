package org.opengis.cite.geopackage10.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLUtils {
    public static int queryInt(Connection c, String query) throws SQLException {
        return queryInt(c, query, null);
    }

    public static int queryInt(Connection c, String query, Object[] args) throws SQLException {
        PreparedStatement s = prepare(c, query, args);

        try {
            ResultSet rs = s.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("");
                }
            } finally {
                rs.close();
            }
        } finally {
            s.close();
        }
    }

    public static List<String> queryStrings(Connection c, String query) throws SQLException {
        return queryStrings(c, query, null);
    }

    public static List<String> queryStrings(Connection c, String query, Object[] args) throws SQLException {
        PreparedStatement s = prepare(c, query, args);

        try {
            ResultSet rs = s.executeQuery();
            try {
                List<String> res = new ArrayList<>();
                while (rs.next()) {
                    res.add(rs.getString(1));
                }
                return res;
            } finally {
                rs.close();
            }
        } finally {
            s.close();
        }
    }

    private static PreparedStatement prepare(Connection c, String query, Object[] args) throws SQLException {
        PreparedStatement s = c.prepareStatement(query);
        bindArguments(s, args);
        return s;
    }

    private static void bindArguments(PreparedStatement s, Object[] args) throws SQLException {
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
    }

    public static boolean tableExists(Connection c, String table) throws SQLException {
        return queryInt(c, "SELECT count(*) FROM sqlite_master WHERE type = 'table' and name = ?", new Object[]{table}) != 0;
    }

    public static List<ColumnInfo> getTableInfo(Connection c, String table) throws SQLException {
        PreparedStatement s = prepare(c, "PRAGMA table_info('" + table + "')", null);
        try {
            ResultSet rs = s.executeQuery();
            try {
                List<ColumnInfo> res = new ArrayList<>();
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
            } finally {
                rs.close();
            }
        } finally {
            s.close();
        }
    }
}
