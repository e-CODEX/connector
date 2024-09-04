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

/**
 * TestDatabaseFactory is an interface that represents a factory to create test databases.
 */
public interface TestDatabaseFactory {
    /**
     * Retrieves the type of the database.
     *
     * @return The type of the database as a string.
     */
    String getDatabaseType();

    /**
     * Retrieves the name of the database.
     *
     * @return The name of the database as a string.
     */
    String getName();

    /**
     * Checks the availability of a specific database version.
     *
     * @param version The version of the database.
     * @return {@code true} if the specified version is available, {@code false} otherwise.
     */
    boolean isAvailable(String version);

    /**
     * Should create on each call a new fresh database.
     */
    TestDatabase createNewDatabase(String version);
}
