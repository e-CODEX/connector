package eu.domibus.connector.persistence.testutil;

/**
 * OracleTestDatabaseFactory is a class that extends the AbstractTestDatabaseFactory class and
 * implements the TestDatabaseFactory interface. It provides functionality for creating a new Oracle
 * database for testing purposes.
 */
public class OracleTestDatabaseFactory extends AbstractTestDatabaseFactory
    implements TestDatabaseFactory {
    @Override
    public String getDatabaseType() {
        return "oracle";
    }

    @Override
    public String getName() {
        return "Native Oracle";
    }

    @Override
    protected String getDriverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }
}
