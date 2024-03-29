package eu.domibus.connector.persistence.testutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;


public abstract class AbstractContainerTestDatabaseFactory implements TestDatabaseFactory {
    private static final Logger LOGGER = LogManager.getLogger(AbstractContainerTestDatabaseFactory.class);

    protected abstract JdbcDatabaseContainer getDatabaseContainer(String version);

    @Override
    public boolean isAvailable(String version) {
        boolean available = isDockerAndDriverAvailable(version);
        if (!available) {
            return false;
        }

        if (version != null) {
            //            throw new RuntimeException("Cannot provide db with data in version " + version);
            LOGGER.warn("Cannot provide db with data in version " + version);
            //            Assumptions.assumeTrue(false, "Cannot provide db with data in version " + version);
            return false;
        }
        return true;
    }

    @Override
    public TestDatabase createNewDatabase(String version) {
        ContainerTestDatabase testDatabase = new ContainerTestDatabase();
        JdbcDatabaseContainer dbContainer = getDatabaseContainer(version);
        try {
            dbContainer.withDatabaseName("test");
        } catch (UnsupportedOperationException e) {
            // ignore it, if not supported...
        }
        dbContainer.withUsername("test");
        dbContainer.withPassword("test");
        dbContainer.start();

        testDatabase.jdbcDatabaseContainer = dbContainer;
        testDatabase.version = version;

        String driverClassName = dbContainer.getDriverClassName();

        return testDatabase;
    }

    protected boolean isDockerAndDriverAvailable(String version) {
        boolean docker = true;
        String command = "docker ps";
        try {
            Process child = Runtime.getRuntime().exec(command);
            child.waitFor();
            if (child.exitValue() != 0) {
                LOGGER.warn("Docker not available!, calling 'docker ps' failed with exit code != 0");
                return false;
            }
        } catch (IOException e) {
            //            Assumptions.assumeTrue(false, "Docker not available!, calling 'docker ps' failed");
            LOGGER.warn("Docker not available!, calling 'docker ps' failed", e);
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        JdbcDatabaseContainer databaseContainer = getDatabaseContainer(version);
        String driverClassName = databaseContainer.getDriverClassName();

        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.warn("SQL Driver [{}] is not available on classpath!", driverClassName);
            return false;
        }
        return true;
    }

    public String toString() {
        return this.getName();
    }

    class ContainerTestDatabase implements TestDatabase {
        JdbcDatabaseContainer jdbcDatabaseContainer;
        String version = null;

        @Override
        public DataSource getDataSource() {
            return DataSourceBuilder
                    .create()
                    .driverClassName(jdbcDatabaseContainer.getDriverClassName())
                    .url(jdbcDatabaseContainer.getJdbcUrl())
                    .username(jdbcDatabaseContainer.getUsername())
                    .password(jdbcDatabaseContainer.getPassword())
                    .build();
        }

        @Override
        public Properties getProperties() {
            Properties p = new Properties();
            p.setProperty("testdb.name", getName());
            p.setProperty("spring.datasource.driver-class-name", jdbcDatabaseContainer.getDriverClassName());
            p.setProperty("spring.datasource.url", jdbcDatabaseContainer.getJdbcUrl());
            p.setProperty("spring.datasource.username", jdbcDatabaseContainer.getUsername());
            p.setProperty("spring.datasource.password", jdbcDatabaseContainer.getPassword());
            return p;
        }

        @Override
        public String getName() {
            return String.format("%s within docker data: [%s]", getDatabaseType(), version == null ? "empty" : version);
        }

        @Override
        public void close() throws Exception {
            jdbcDatabaseContainer.stop();
        }
    }
}
