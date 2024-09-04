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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;

/**
 * MariaDbContainerTestDatabaseFactory is a subclass of AbstractContainerTestDatabaseFactory and
 * implements the TestDatabaseFactory interface. It provides functionality to create a new database
 * using MariaDB container within Docker. The class internally uses the MariaDBContainer class from
 * Testcontainers library to manage the MariaDB container instance.
 */
public class MariaDbContainerTestDatabaseFactory extends AbstractContainerTestDatabaseFactory
    implements TestDatabaseFactory {
    private static final Logger LOGGER =
        LogManager.getLogger(MariaDbContainerTestDatabaseFactory.class);
    List<String> availableVersions = Stream.of("4.1.x", "3.5.x").toList();

    @Override
    public String getDatabaseType() {
        return "mysql";
    }

    @Override
    public String getName() {
        return "Mysql within Docker";
    }

    protected JdbcDatabaseContainer getDatabaseContainer(String version) {

        return new MariaDBContainer();
    }

    @Override
    public TestDatabase createNewDatabase(String version) {
        TestDatabase newDatabase = super.createNewDatabase(version);

        if (version != null) {
            String scriptFile = "/dbscripts/test/mysql/mysql_" + version + ".sql";
            LOGGER.info("Loading initial script from [{}]", scriptFile);
            try {
                // Connection connection = newDatabase.getDataSource()
                // .getConnection("test", "test");
                Connection connection = newDatabase.getDataSource().getConnection();

                ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptFile));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return newDatabase;
    }

    @Override
    public boolean isAvailable(String version) {

        return (availableVersions.contains(version) || version == null)
            && super.isDockerAndDriverAvailable(version);
    }
}
