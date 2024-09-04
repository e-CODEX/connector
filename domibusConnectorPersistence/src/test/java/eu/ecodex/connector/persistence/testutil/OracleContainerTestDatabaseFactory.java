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
import org.testcontainers.containers.OracleContainer;

/**
 * OracleContainerTestDatabaseFactory is a class that implements the TestDatabaseFactory interface
 * and provides a factory for creating Oracle database instances running within Docker containers.
 */
public class OracleContainerTestDatabaseFactory extends AbstractContainerTestDatabaseFactory
    implements TestDatabaseFactory {
    private static final Logger LOGGER =
        LogManager.getLogger(OracleContainerTestDatabaseFactory.class);
    List<String> availableVersions = Stream.of("4.1.x", "3.5.x").toList();
    public static final String SID = "testsid";
    public static final String DB_DOMAIN = "example.com";
    public static final String DB_PASSWORD = "test";

    @Override
    public String getDatabaseType() {
        return "oracle";
    }

    @Override
    public String getName() {
        return "Oracle within Docker";
    }

    protected JdbcDatabaseContainer getDatabaseContainer(String version) {
        OracleContainer oracle = new OracleContainer("oracleinanutshell/oracle-xe-11g:1.0.0");
        oracle.withUsername("system").withPassword("oracle");

        return oracle;
    }

    @Override
    public TestDatabase createNewDatabase(String version) {
        ContainerTestDatabase testDatabase = new ContainerTestDatabase();
        var dbContainer = getDatabaseContainer(version);
        dbContainer.start();

        testDatabase.jdbcDatabaseContainer = dbContainer;
        testDatabase.version = version;

        dbContainer.getDriverClassName();

        if (version != null) {
            String scriptFile = "/dbscripts/test/oracle/oracle_" + version + ".sql";
            LOGGER.info("Loading initial script from [{}]", scriptFile);
            try {
                Connection connection = testDatabase.getDataSource().getConnection();

                ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptFile));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return testDatabase;
    }

    @Override
    public boolean isAvailable(String version) {

        return (availableVersions.contains(version) || version == null)
            && super.isDockerAndDriverAvailable(version);
    }
}
