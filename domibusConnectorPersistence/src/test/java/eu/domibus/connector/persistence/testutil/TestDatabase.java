package eu.domibus.connector.persistence.testutil;

import java.util.Properties;
import javax.sql.DataSource;

/**
 * TestDatabase is an interface that represents a database used for testing purposes. It extends the
 * AutoCloseable interface, allowing resources to be automatically released after use.
 */
public interface TestDatabase extends AutoCloseable {
    DataSource getDataSource();

    /**
     * returns a set of spring properties to access the database with the data in the given version
     * spring.datasource.url spring.datasource.username spring.datasource.password
     * spring.datasource.driver-class-name
     *
     * @return - spring properties
     */
    Properties getProperties();

    String getName();
}
