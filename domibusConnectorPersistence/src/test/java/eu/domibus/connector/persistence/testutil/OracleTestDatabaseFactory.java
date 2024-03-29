package eu.domibus.connector.persistence.testutil;

public class OracleTestDatabaseFactory extends AbstractTestDatabaseFactory implements TestDatabaseFactory {
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
