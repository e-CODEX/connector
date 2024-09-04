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
