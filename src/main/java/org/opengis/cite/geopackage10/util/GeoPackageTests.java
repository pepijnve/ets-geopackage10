package org.opengis.cite.geopackage10.util;

import org.opengis.cite.geopackage10.SuiteAttribute;
import org.sqlite.JDBC;
import org.sqlite.SQLiteOpenMode;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Includes various tests of capability 1.
 */
public class GeoPackageTests {
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
