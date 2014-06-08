package org.opengis.cite.geopackage10.options;

import org.opengis.cite.geopackage10.util.GeoPackageTests;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Includes various tests of Tiles.
 */
public class TilesTests extends GeoPackageTests {
    @Test(description = "/opt/tiles/contents/data/tiles_row")
    public void testTilesRow() throws SQLException {
        testTilesRow(getDatabase());
    }

    public static void testTilesRow(Connection c) {

    }
}
