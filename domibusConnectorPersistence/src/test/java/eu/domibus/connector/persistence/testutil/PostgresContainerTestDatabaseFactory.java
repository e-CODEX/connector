package eu.domibus.connector.persistence.testutil;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * A factory class for creating Postgres test databases within a container.
 *
 * <p>This class extends the {@link AbstractContainerTestDatabaseFactory} class and provides
 * implementations for the abstract methods. It creates a PostgreSQL container using the specified
 * version and returns it as a {@link JdbcDatabaseContainer}.
 *
 * <p>The database type is set to "postgres" and the name is set to "Postgres within container".
 */
public class PostgresContainerTestDatabaseFactory extends AbstractContainerTestDatabaseFactory {
    @Override
    protected JdbcDatabaseContainer getDatabaseContainer(String version) {
        return new PostgreSQLContainer("postgres:9");
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
