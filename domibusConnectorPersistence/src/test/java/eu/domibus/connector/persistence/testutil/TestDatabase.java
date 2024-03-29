package eu.domibus.connector.persistence.testutil;

import javax.sql.DataSource;
import java.util.Properties;


public interface TestDatabase extends AutoCloseable {
    DataSource getDataSource();

    /**
     * returns a set of spring properties to access
     * the database with the data in the given version
     * spring.datasource.url
     * spring.datasource.username
     * spring.datasource.password
     * spring.datasource.driver-class-name
     *
     * @return - spring properties
     */
    Properties getProperties();

    String getName();
}
