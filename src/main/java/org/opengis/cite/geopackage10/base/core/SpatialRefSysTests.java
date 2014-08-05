package org.opengis.cite.geopackage10.base.core;

import org.opengis.cite.geopackage10.util.CRS;
import org.opengis.cite.geopackage10.util.GeoPackageTests;
import org.opengis.cite.geopackage10.util.SQLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.opengis.cite.geopackage10.util.SQLUtils.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SpatialRefSysTests extends GeoPackageTests {
    /**
     * Verify that the gpkg_spatial_ref_sys table exists and has the correct definition.
     */
    @Test(description = "/base/core/gpkg_spatial_ref_sys/data/table_def")
    public void testTableDef() throws IOException, SQLException {
        Connection c = getDatabase();

        SQLUtils.assertTableExists(c, "gpkg_spatial_ref_sys");

        GeoPackageTests.checkTableSchema(
                "gpkg_spatial_ref_sys",
                SQLUtils.getTableInfo(c, "gpkg_spatial_ref_sys")
        );
    }

    /**
     * Verify that the spatial_ref_sys table contains rows to define all srs_id values used by features and tiles in a
     * GeoPackage.
     */
    @Test(description = "/base/core/gpkg_spatial_ref_sys/data_values_default")
    public void testDataValuesDefault() throws IOException, SQLException {
        assertEquals(
                queryInt(getDatabase(), "SELECT count(*) FROM gpkg_spatial_ref_sys WHERE srs_id = -1"),
                1,
                "SRID -1 is missing"
        );

        assertEquals(
                queryInt(getDatabase(), "SELECT count(*) FROM gpkg_spatial_ref_sys WHERE srs_id = -1 and organization like 'none' and organization_coordsys_id = -1 and definition like 'undefined'"),
                1,
                "SRID -1 is incorrect"
        );

        assertEquals(
                queryInt(getDatabase(), "SELECT count(*) FROM gpkg_spatial_ref_sys WHERE srs_id = 0"),
                1,
                "SRID 0 is missing"
        );

        assertEquals(
                queryInt(getDatabase(), "SELECT count(*) FROM gpkg_spatial_ref_sys WHERE srs_id = 0 and organization like 'none' and organization_coordsys_id = 0 and definition like 'undefined'"),
                1,
                "SRID 0 is incorrect"
        );

        assertEquals(
                queryInt(getDatabase(), "SELECT count(*) FROM gpkg_spatial_ref_sys WHERE organization like 'epsg' AND organization_coordsys_id = 4326"),
                1,
                "SRS EPSG:4326 is missing"
        );

        CRS expected = parseCRS("GEOGCS [\"WGS 84\", DATUM [\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137, 298.257223563 , AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0 , AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943278, AUTHORITY[\"EPSG\",\"9102\"]], AUTHORITY[\"EPSG\",\"4326\"]");
        CRS actual = parseCRS(queryString(getDatabase(), "SELECT definition FROM gpkg_spatial_ref_sys WHERE organization like 'epsg' AND organization_coordsys_id = 4326"));
        assertTrue(expected.isEquivalent(actual));
    }

    /**
     * Verify that the spatial_ref_sys table contains rows to define all srs_id values used by features and tiles in a
     * GeoPackage.
     */
    @Test(description = "/base/core/spatial_ref_sys/data_values_required")
    public void testDataValuesRequired() throws IOException, SQLException {
        // TODO this duplicates /base/core/contents/data/data_values_srs_id
        String error = foreignKeyCheck(getDatabase(), "gpkg_contents");
        assertTrue(error == null, error);
    }
}
