package eu.domibus.connector.persistence.testutil;

/**
 * The MysqlTestDatabaseFactory class is a concrete implementation of the TestDatabaseFactory
 * interface. It provides functionality for creating a new MySQL database for testing purposes.
 */
public class MysqlTestDatabaseFactory extends AbstractTestDatabaseFactory
    implements TestDatabaseFactory {
    @Override
    public String getDatabaseType() {
        return "mysql";
    }

    @Override
    public String getName() {
        return "Native Mysql";
    }

    @Override
    protected String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }
}
