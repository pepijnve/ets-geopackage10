package org.opengis.cite.geopackage10.base.core;

import org.opengis.cite.geopackage10.util.GeoPackageTests;
import org.opengis.cite.geopackage10.util.SQLUtils;
import org.opengis.cite.geopackage10.util.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import static org.opengis.cite.geopackage10.util.SQLUtils.foreignKeyCheck;
import static org.opengis.cite.geopackage10.util.SQLUtils.queryStrings;
import static org.testng.Assert.assertTrue;

public class ContentsTests extends GeoPackageTests {
    /**
     * Verify that the gpkg_contents table exists and has the correct definition.
     */
    @Test(description = "/base/core/contents/data/table_def")
    public void testTableDef() throws IOException, SQLException {
        Connection c = getDatabase();

        SQLUtils.assertTableExists(c, "gpkg_contents");

        GeoPackageTests.checkTableSchema(
                "gpkg_spatial_ref_sys",
                SQLUtils.getTableInfo(c, "gpkg_spatial_ref_sys")
        );
    }

    /**
     * Verify that the table_name column values in the gpkg_contents table are valid.
     */
    @Test(description = "/base/core/contents/data/data_values_table_name")
    public void testDataValuesTableName() throws IOException, SQLException {
        // TODO different than what ATS prescribes, but checks the same thing
        List<String> nonExistantTables = SQLUtils.queryStrings(
                getDatabase(),
                "select table_name from gpkg_contents where table_name not in (select table_name from sqlite_master where type = 'table');"
        );

        if (!nonExistantTables.isEmpty()) {
            Assert.fail("Tables " + StringUtils.join(", ", nonExistantTables) + " are listed in gpkg_contents but do not exist");
        }
    }

    /**
     * Verify that the gpkg_contents table last_change column values are in ISO 8601 format containing a complete
     * date plus UTC hours, minutes, seconds and a decimal fraction of a second, with a ‘Z’ (‘zulu’) suffix indicating UTC.
     */
    @Test(description = "/base/core/contents/data/data_values_last_change")
    public void testDataValuesLastChange() throws IOException, SQLException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        StringBuilder b = new StringBuilder();

        List<String> changes = queryStrings(getDatabase(), "SELECT last_change FROM gpkg_contents");
        Iterator<String> changesIt = changes.iterator();
        while (changesIt.hasNext()) {
            String actual = changesIt.next();
            try {
                String expected = format.format(format.parse(actual));
                if (!expected.equals(actual)) {
                    b.append("Incorrect timestamp: " + actual + "\n");
                }
            } catch (ParseException e) {
                b.append("Unparseable timestamp: " + actual + "\n");
            }
        }

        String msg = b.toString();

        assertTrue(msg.length() == 0, msg);
    }

    /**
     * Verify that the gpkg_contents table srs_id column values reference gpkg_spatial_ref_sys srs_id column values.
     */
    @Test(description = "/base/core/contents/data/data_values_srs_id")
    public void testDataValuesRequired() throws IOException, SQLException {
        String error = foreignKeyCheck(getDatabase(), "gpkg_contents");
        assertTrue(error == null, error);
    }
}
