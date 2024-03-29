package eu.domibus.connector.persistence.testutil;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;


public class PostgresContainerTestDatabaseFactory extends AbstractContainerTestDatabaseFactory {
    @Override
    protected JdbcDatabaseContainer getDatabaseContainer(String version) {
        PostgreSQLContainer pgsql = new PostgreSQLContainer("postgres:9");
        return pgsql;
    }

    @Override
    public String getDatabaseType() {
        return "postgres";
    }

    @Override
    public String getName() {
        return "Postgres within container";
    }
}
