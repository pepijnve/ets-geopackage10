package org.opengis.cite.geopackage10.base.core;

import org.opengis.cite.geopackage10.util.GeoPackageTests;
import org.opengis.cite.geopackage10.util.SQLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.opengis.cite.geopackage10.util.SQLUtils.foreignKeyCheck;
import static org.testng.Assert.assertTrue;

public class SpatialRefSysTests extends GeoPackageTests {
    /**
     * Verify that the gpkg_spatial_ref_sys table exists and has the correct definition.
     */
    @Test(description = "/base/core/gpkg_spatial_ref_sys/data/table_def")
    public void testTableDef() throws IOException, SQLException {
        Connection c = getDatabase();

        Assert.assertTrue(SQLUtils.tableExists(c, "gpkg_spatial_ref_sys"), "Table gpkg_spatial_ref_sys does not exist");

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
        // TODO
    }

    /**
     * Verify that the spatial_ref_sys table contains rows to define all srs_id values used by features and tiles in a
     * GeoPackage.
     */
    @Test(description = "/base/core/spatial_ref_sys/data_values_required")
    public void testDataValuesRequired() throws IOException, SQLException {
        String error = foreignKeyCheck(getDatabase(), "gpkg_contents");
        assertTrue(error == null, error);
    }
}
