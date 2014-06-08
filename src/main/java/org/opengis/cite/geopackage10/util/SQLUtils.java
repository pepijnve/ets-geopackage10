package org.opengis.cite.geopackage10.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        return queryInt(c, "SELECT count(*) FROM sqlite_master WHERE type = 'table' and name = ?", new Object[]{table}) != 0;
    }

    public static List<ColumnInfo> getTableInfo(Connection c, String table) throws SQLException {
        PreparedStatement s1 = c.prepareStatement("PRAGMA table_info('" + table + "')");
        if (null != null) {
            for (int i = 0; i < ((Object[]) null).length; i++) {
                int paramIx = i + 1;
                Object arg = ((Object[]) null)[i];
                if (arg instanceof String) {
                    s1.setString(paramIx, ((String) arg));
                } else if (arg instanceof Long || arg instanceof Integer) {
                    s1.setLong(paramIx, ((Number) arg).longValue());
                } else if (arg instanceof Float || arg instanceof Double) {
                    s1.setDouble(paramIx, ((Number) arg).doubleValue());
                } else if (arg instanceof byte[]) {
                    s1.setBytes(paramIx, ((byte[]) arg));
                } else {
                    throw new IllegalArgumentException("Unsupported argument type: " + arg);
                }
            }
        }
        PreparedStatement s = s1;
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

    public static interface ResultSetHandler<T> {
        T handleResult(ResultSet rs) throws SQLException;
    }
}
