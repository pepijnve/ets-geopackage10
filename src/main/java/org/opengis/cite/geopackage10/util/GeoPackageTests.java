package org.opengis.cite.geopackage10.util;

import org.opengis.cite.geopackage10.SuiteAttribute;
import org.sqlite.JDBC;
import org.sqlite.SQLiteOpenMode;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.collections.CollectionUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class GeoPackageTests {
    private static final Map<String, TableInfo> TABLE_SPECS = new HashMap<>();

    static {
        addTable(
                "gpkg_spatial_ref_sys",
                set(
                        column("srs_name", "TEXT", true, null, 0),
                        column("srs_id", "INTEGER", true, null, 1),
                        column("organization", "TEXT", true, null, 0),
                        column("organization_coordsys_id", "INTEGER", true, null, 0),
                        column("definition", "TEXT", true, null, 0),
                        column("description", "TEXT", false, null, 0)
                )
        );

        addTable(
                "gpkg_contents",
                set(
                        column("table_name", "TEXT", true, null, 1),
                        column("data_type", "TEXT", true, null, 0),
                        column("identifier", "TEXT", false, null, 0),
                        column("description", "TEXT", false, "''", 0),
                        column("last_change", "DATETIME", true, "strftime('%Y-%m-%dT%H:%M:%fZ',CURRENT_TIMESTAMP)", 0),
                        column("min_x", "DOUBLE", false, null, 0),
                        column("max_x", "DOUBLE", false, null, 0),
                        column("min_y", "DOUBLE", false, null, 0),
                        column("max_y", "DOUBLE", false, null, 0),
                        column("srs_id", "INTEGER", false, null, 0)
                ),
                set(
                        foreignKey( list("srs_id"), "gpkg_spatial_ref_sys", list("srs_id") )
                )
        );

        addTable(
                "gpkg_geometry_columns",
                set(
                        column("table_name", "TEXT", true, null, 1),
                        column("column_name", "TEXT", true, null, 1),
                        column("geometry_type_name", "TEXT", true, null, 0),
                        column("srs_id", "INTEGER", true, null, 0),
                        column("z", "TINYINT", true, null, 0),
                        column("m", "TINYINT", true, null, 0)
                ),
                set(
                        foreignKey( list("srs_id"), "gpkg_spatial_ref_sys", list("srs_id") ),
                        foreignKey( list("table_name"), "gpkg_contents", list("table_name") )
                )
        );

        addTable(
                "gpkg_tile_matrix_set",
                set(
                        column("table_name", "TEXT", true, null, 1),
                        column("srs_id", "INTEGER", true, null, 0),
                        column("min_x", "DOUBLE", true, null, 0),
                        column("max_x", "DOUBLE", true, null, 0),
                        column("min_y", "DOUBLE", true, null, 0),
                        column("max_y", "DOUBLE", true, null, 0)
                ),
                set(
                        foreignKey( list("srs_id"), "gpkg_spatial_ref_sys", list("srs_id") ),
                        foreignKey( list("table_name"), "gpkg_contents", list("table_name") )
                )
        );

        addTable(
                "gpkg_tile_matrix",
                set(
                        column("table_name", "TEXT", true, null, 1),
                        column("zoom_level", "INTEGER", true, null, 1),
                        column("matrix_width", "INTEGER", true, null, 0),
                        column("matrix_height", "INTEGER", true, null, 0),
                        column("tile_width", "INTEGER", true, null, 0),
                        column("tile_height", "INTEGER", true, null, 0),
                        column("pixel_x_size", "DOUBLE", true, null, 0),
                        column("pixel_y_size", "DOUBLE", true, null, 0)
                ),
                set(
                        foreignKey( list("table_name"), "gpkg_contents", list("table_name") )
                )
        );

        addTable(
                "gpkg_data_columns",
                set(
                        column("table_name", "TEXT", true, null, 1),
                        column("column_name", "TEXT", true, null, 1),
                        column("name", "TEXT", false, null, 0),
                        column("title", "TEXT", false, null, 0),
                        column("description", "TEXT", false, null, 0),
                        column("mime_type", "TEXT", false, null, 0),
                        column("constraint_name", "TEXT", false, null, 0)
                ),
                set(
                        foreignKey( list("table_name"), "gpkg_contents", list("table_name") )
                )
        );

        addTable(
                "gpkg_data_column_constraints",
                set(
                        column("constraint_name", "TEXT", true, null, 0),
                        column("constraint_type", "TEXT", true, null, 0),
                        column("value", "TEXT", false, null, 0),
                        column("min", "NUMERIC", false, null, 0),
                        column("minIsInclusive", "BOOLEAN", false, null, 0),
                        column("max", "NUMERIC", false, null, 0),
                        column("maxIsInclusive", "BOOLEAN", false, null, 0),
                        column("description", "TEXT", false, null, 0)
                )
        );

        addTable(
                "gpkg_metadata",
                set(
                        column("id", "INTEGER", true, null, 1),
                        column("md_scope", "TEXT", true, "'dataset'", 0),
                        column("md_standard_uri", "TEXT", true, null, 0),
                        column("mime_type", "TEXT", true, "'text/xml'", 0),
                        column("metadata", "TEXT", true, null, 0)
                )
        );

        addTable(
                "gpkg_metadata_reference",
                set(
                        column("reference_scope", "TEXT", true, null, 0),
                        column("table_name", "TEXT", false, null, 0),
                        column("column_name", "TEXT", false, null, 0),
                        column("row_id_value", "INTEGER", false, null, 0),
                        column("timestamp", "DATETIME", true, "strftime('%Y-%m-%dT%H:%M:%fZ',CURRENT_TIMESTAMP)", 0),
                        column("md_file_id", "INTEGER", true, null, 0),
                        column("md_parent_id", "INTEGER", false, null, 0)
                ),
                set(
                        foreignKey( list("md_file_id"), "gpkg_metadata", list("id") ),
                        foreignKey( list("md_parent_id"), "gpkg_metadata", list("id") )
                )
        );

        addTable(
                "gpkg_extensions",
                set(
                        column("table_name", "TEXT", false, null, 0),
                        column("column_name", "TEXT", false, null, 0),
                        column("extension_name", "TEXT", true, null, 0),
                        column("definition", "TEXT", true, null, 0),
                        column("scope", "TEXT", true, null, 0)
                )
        );
    }

    private static void addTable(String name, Set<ColumnInfo> columns) {
        addTable(name, columns, Collections.<ForeignKeyInfo>emptySet());
    }

    private static void addTable(String name, Set<ColumnInfo> columns, Set<ForeignKeyInfo> foreignKeys) {
        TABLE_SPECS.put(name, new TableInfo(name, columns, foreignKeys));
    }

    public static TableInfo getTableSpecification(String tableName) {
        return TABLE_SPECS.get(tableName.toLowerCase());
    }

    private static ColumnInfo column(String name, String type, boolean notNull, String defaultValue, int primaryKeyIndex) {
        return new ColumnInfo(name, type, notNull, defaultValue, primaryKeyIndex);
    }

    private static ForeignKeyInfo foreignKey(List<String> fromCols, String name, List<String> toCols) {
        return new ForeignKeyInfo(fromCols, name, toCols);
    }

    private static <T> Set<T> set(T... elements) {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }

    private static <T> List<T> list(T... elements) {
        return Arrays.asList(elements);
    }

    public static void checkTableSchema(String table, TableInfo actualTableInfo) {
        TableInfo spec = getTableSpecification(table);
        // TODO refine error message
        Assert.assertEquals(actualTableInfo, spec);
    }

    public static CRS parseCRS(String wkt) {
        return new CRS();
    }

    private File testSubject;
    private Connection connection;

    /**
     * Obtains the test subject from the ISuite context. The suite attribute
     * {@link org.opengis.cite.geopackage10.SuiteAttribute#TEST_SUBJECT} should
     * evaluate to a DOM Document node.
     *
     * @param testContext The test (group) context.
     */
    @BeforeClass
    public void obtainTestSubject(ITestContext testContext) {
        testSubject = SuiteAttribute.TEST_SUBJECT.getValue(testContext);
    }

    @BeforeMethod
    public void openDatabaseConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.setProperty("open_mode", Integer.toString(SQLiteOpenMode.READONLY.flag));

        connection = JDBC.createConnection(
                "jdbc:sqlite:" + testSubject.getAbsolutePath(),
                connectionProps
        );
    }

    @AfterMethod
    public void closeDatabaseConnection() throws SQLException {
        connection.close();
        connection = null;
    }

    /**
     * Sets the test subject. This method is intended to facilitate unit
     * testing.
     *
     * @param testSubject A Document node representing the test subject or
     *                    metadata about it.
     */
    public void setTestSubject(File testSubject) {
        this.testSubject = testSubject;
    }

    public File getTestSubject() {
        return testSubject;
    }

    public Connection getDatabase() throws SQLException {
        return connection;
    }
}
