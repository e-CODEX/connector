/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.testutil;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.testcontainers.containers.JdbcDatabaseContainer;

/**
 * Abstract base class for container-based test database factories.
 *
 * <p>This class implements the {@link TestDatabaseFactory} interface and provides default
 * implementations for some of the methods. Concrete subclasses are expected to provide the
 * implementation for the {@link #getDatabaseContainer(String)} method.
 *
 * <p>The class internally uses a nested class {@link ContainerTestDatabase} to represent the test
 * database. This class provides an implementation of the {@link TestDatabase} interface and
 * encapsulates the logic for creating a {@link DataSource}, {@link Properties}, and closing the
 * database.
 */
public abstract class AbstractContainerTestDatabaseFactory implements TestDatabaseFactory {
    private static final Logger LOGGER =
        LogManager.getLogger(AbstractContainerTestDatabaseFactory.class);

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
            p.setProperty(
                "spring.datasource.driver-class-name", jdbcDatabaseContainer.getDriverClassName());
            p.setProperty("spring.datasource.url", jdbcDatabaseContainer.getJdbcUrl());
            p.setProperty("spring.datasource.username", jdbcDatabaseContainer.getUsername());
            p.setProperty("spring.datasource.password", jdbcDatabaseContainer.getPassword());
            return p;
        }

        @Override
        public String getName() {
            return String.format(
                "%s within docker data: [%s]", getDatabaseType(),
                version == null ? "empty" : version
            );
        }

        @Override
        public void close() throws Exception {
            jdbcDatabaseContainer.stop();
        }
    }

    @Override
    public TestDatabase createNewDatabase(String version) {
        var dbContainer = getDatabaseContainer(version);
        try {
            dbContainer.withDatabaseName("test");
        } catch (UnsupportedOperationException e) {
            // ignore it, if not supported...
        }
        dbContainer.withUsername("test");
        dbContainer.withPassword("test");
        dbContainer.start();

        var testDatabase = new ContainerTestDatabase();
        testDatabase.jdbcDatabaseContainer = dbContainer;
        testDatabase.version = version;

        return testDatabase;
    }

    protected abstract JdbcDatabaseContainer getDatabaseContainer(String version);

    @Override
    public boolean isAvailable(String version) {

        boolean available = isDockerAndDriverAvailable(version);
        if (!available) {
            return false;
        }

        if (version != null) {
            LOGGER.warn("Cannot provide db with data in version {}", version);
            return false;
        }
        return true;
    }

    protected boolean isDockerAndDriverAvailable(String version) {
        String command = "docker ps";
        try {
            Process child = Runtime.getRuntime().exec(command);
            child.waitFor();
            if (child.exitValue() != 0) {
                LOGGER.warn(
                    "Docker not available!, calling 'docker ps' failed with exit code != 0");
                return false;
            }
        } catch (IOException e) {
            LOGGER.warn("Docker not available!, calling 'docker ps' failed", e);
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var databaseContainer = getDatabaseContainer(version);
        String driverClassName = databaseContainer.getDriverClassName();

        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.warn("SQL Driver [{}] is not available on classpath!", driverClassName);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
