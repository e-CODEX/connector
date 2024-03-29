package eu.domibus.connector.persistence.testutil;

public class MysqlTestDatabaseFactory extends AbstractTestDatabaseFactory implements TestDatabaseFactory {
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
