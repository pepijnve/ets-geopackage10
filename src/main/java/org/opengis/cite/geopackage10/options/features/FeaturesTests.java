package org.opengis.cite.geopackage10.options.features;

import org.opengis.cite.geopackage10.util.GeoPackageTests;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Includes various tests of Features.
 */
public class FeaturesTests extends GeoPackageTests {
    @Test(description = "/opt/features/vector_features/data/feature_table_integer_primary_key")
    public void testFeatureTableIntegerPrimaryKey() throws SQLException {
        testFeatureTableIntegerPrimaryKey(getDatabase());
    }

    public static void testFeatureTableIntegerPrimaryKey(Connection c) {
        // TODO implement
    }

    @Test(description = "/opt/features/vector_features/data/feature_table_one_geometry_column")
    public void testFeatureTableOneGeometryColumn() throws SQLException {
        testFeatureTableOneGeometryColumn(getDatabase());
    }

    public static void testFeatureTableOneGeometryColumn(Connection c) {
        // TODO implement
    }
}
