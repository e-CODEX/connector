package eu.domibus.connector.persistence.testutil;

import java.util.Properties;

public interface TestDatabaseFactory {
    /**
     * @return type of the database
     */
    String getDatabaseType();

    String getName();

    boolean isAvailable(String version);

    /**
     * should create on each call a new fresh database
     */
    TestDatabase createNewDatabase(String version);

}
