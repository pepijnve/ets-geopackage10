package org.opengis.cite.geopackage10;

import org.testng.ITestContext;

import java.io.File;

/**
 * An enumerated type defining ISuite attributes that may be set to constitute a
 * shared test fixture.
 */
public class SuiteAttribute<T> {

    /**
     * A File object referencing a GeoPackage database.
     */
    public static final SuiteAttribute<File> TEST_SUBJECT = new SuiteAttribute<File>("testSubject", File.class);

    private final Class<T> attrType;
    private final String attrName;

    private SuiteAttribute(String attrName, Class<T> attrType) {
        this.attrName = attrName;
        this.attrType = attrType;
    }

    public T getValue(ITestContext context) {
        Object value = context.getSuite().getAttribute(attrName);
        if (attrType.isInstance(value)) {
            return attrType.cast(value);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Class<T> getType() {
        return attrType;
    }

    public String getName() {
        return attrName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(attrName);
        sb.append('(').append(attrType.getName()).append(')');
        return sb.toString();
    }
}
