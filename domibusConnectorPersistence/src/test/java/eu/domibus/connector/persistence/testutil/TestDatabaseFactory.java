package eu.domibus.connector.persistence.testutil;

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
