package org.opengis.cite.geopackage10.base;

import org.opengis.cite.geopackage10.options.FeaturesTests;
import org.opengis.cite.geopackage10.options.TilesTests;
import org.opengis.cite.geopackage10.util.ColumnInfo;
import org.opengis.cite.geopackage10.util.GeoPackageTests;
import org.opengis.cite.geopackage10.util.SQLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.opengis.cite.geopackage10.util.SQLUtils.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Includes various tests of Core.
 */
public class SQLiteContainerTests extends GeoPackageTests {
    /**
     * Verify that the Geopackage is an SQLite version_3 database
     * <p/>
     * Pass if the first 16 bytes of the file contain “SQLite format 3” in ASCII.
     */
    @Test(description = "/base/core/container/data/file_format")
    public void testFileFormat() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(getTestSubject(), "r");
        try {
            byte[] header = new byte[16];
            raf.readFully(header);

            assertEquals(new String(header, "US-ASCII"), "SQLite format 3\0");
        } finally {
            raf.close();
        }
    }

    /**
     * Verify that the SQLite database header application id field indicates GeoPackage version 1.0
     * <p/>
     * Pass if the application id field of the SQLite database header contains “GP10” in ASCII.
     */
    @Test(description = "/base/core/container/data/application_id")
    public void testApplicationId() throws SQLException {
        Connection c = getDatabase();
//        TODO needs more recent version of SQLite
//        int applicationId = SQLUtils.queryInt(c, "PRAGMA application_id");
//        Assert.assertEquals(applicationId, 0x47503130);
    }

    /**
     * Verify that the geopackage extension is ".gpkg"
     * <p/>
     * Pass if the geopackage file extension is ".gpkg"
     * <p/>
     */
    @Test(description = "/base/core/container/data/file_extension_name")
    public void testFileExtensionName() throws IOException, SQLException {
        Connection c = getDatabase();

        // TODO ATS test should take extended geopackage into account
//        boolean isExtended = tableExists(c, "gpkg_extensions") &&
//                queryInt(c, "SELECT count(*) FROM gpkg_extensions WHERE extension_name not like 'gpkg_%'") > 0;
        boolean isExtended = false;

        String expectedExtension = isExtended ? ".gpkx" : ".gpkg";

        String fileName = getTestSubject().getName().toLowerCase();

        Assert.assertTrue(fileName.endsWith(expectedExtension));
    }

    /**
     * Verify that the Geopackage only contains specified contents
     */
    @Test(description = "/base/core/container/data/file_contents")
    public void testFileContents() throws IOException, SQLException {
        Connection c = getDatabase();

        // Step 1
        List<String> gpkgTables = queryStrings(c, "SELECT name FROM sqlite_master WHERE type = 'table' AND name LIKE 'gpkg_%'");
        for (int i = 0; i < gpkgTables.size(); i++) {
            String tableName = gpkgTables.get(i);
            List<ColumnInfo> actualTableInfo = getTableInfo(c, tableName);
            List<ColumnInfo> expectedTableInfo = getTableSpecification(tableName);
            checkTableSchema(actualTableInfo, expectedTableInfo);
        }

        // Step 2
        FeaturesTests.testFeatureTableIntegerPrimaryKey(c);

        // Step 3
        FeaturesTests.testFeatureTableOneGeometryColumn(c);

        // Step 4
        TilesTests.testTilesRow(c);

        // Step 5, 6, 7
        assertEquals(
                0,
                queryInt(c, "SELECT count(*) FROM gpkg_extensions WHERE extension_name not like = 'gpkg_%'")
        );
    }

    /**
     * Verify that the geopackage passes the SQLite integrity check.
     */
    @Test(description = "/base/core/container/data/file_integrity")
    public void testFileIntegrity() throws SQLException {
        assertEquals("ok", queryString(getDatabase(), "PRAGMA integrity_check"));
    }

    /**
     * Verify that the geopackage passes the SQLite foreign_key_check.
     */
    @Test(description = "/base/core/container/data/foreign_key_integrity")
    public void testForeignKeyIntegrity() throws SQLException {
        String error = query(getDatabase(), "PRAGMA foreign_key_check", new Object[0], new ResultSetHandler<String>() {
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

        assertTrue(error == null, error);
    }
}
