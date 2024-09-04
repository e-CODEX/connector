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
