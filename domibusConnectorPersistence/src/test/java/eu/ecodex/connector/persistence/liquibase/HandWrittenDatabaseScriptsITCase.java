/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.liquibase;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import eu.ecodex.connector.persistence.testutil.MariaDbContainerTestDatabaseFactory;
import eu.ecodex.connector.persistence.testutil.MysqlContainerTestDatabaseFactory;
import eu.ecodex.connector.persistence.testutil.MysqlTestDatabaseFactory;
import eu.ecodex.connector.persistence.testutil.OracleContainerTestDatabaseFactory;
import eu.ecodex.connector.persistence.testutil.OracleTestDatabaseFactory;
import eu.ecodex.connector.persistence.testutil.TestDatabase;
import eu.ecodex.connector.persistence.testutil.TestDatabaseFactory;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * This class contains integration tests for the HandWrittenDatabaseScripts class. It tests the
 * migration and execution of database scripts using various TestDatabaseFactory implementations.
 * The tests are divided into two categories: testMigrateScripts and testInitialScripts.
 *
 * @see TestDatabaseFactory
 */
@Disabled
public class HandWrittenDatabaseScriptsITCase {
    private static final Logger LOGGER =
        LogManager.getLogger(HandWrittenDatabaseScriptsITCase.class);
    public static final String INITIAL_SCRIPTS_CLASSPATH = "/dbscripts/initial/";
    public static final String MIGRATE_SCRIPTS_CLASSPATH = "/dbscripts/migrate/";
    public static List<String> INITIAL_VERSIONS = Stream
        .of("4.1.0", "4.2.0")
        .toList();
    public static List<TestDatabaseFactory> TEST_DATABASE_FACTORIES_LIST = Stream
        .of(
            new OracleContainerTestDatabaseFactory(),
            //                    H2TestDatabaseFactory.h2Oracle(),
            new MysqlTestDatabaseFactory(),
            new OracleTestDatabaseFactory(),
            new MysqlContainerTestDatabaseFactory(),
            new MariaDbContainerTestDatabaseFactory()
        ).toList();
    public static final List<UpgradePath> UPGRADE_PATHS = Stream
        .of(upgradePath("3.5.x").to("4.0.0"), upgradePath("4.1.x").to("4.2.0"))
        .toList();

    static UpgradePath upgradePath(String fromVersion) {
        UpgradePath up = new UpgradePath();
        up.fromVersion = fromVersion;
        return up;
    }

    /**
     * The UpgradePath class represents an upgrade path from one version to multiple target
     * versions.
     */
    public static class UpgradePath {
        String fromVersion;
        List<String> toVersion;

        public UpgradePath to(String... version) {
            toVersion = Stream.of(version).toList();
            return this;
        }
    }

    @TestFactory
    Stream<DynamicContainer> testMigrateScripts() {
        return UPGRADE_PATHS.stream()
                            .map(upgradePath -> dynamicContainer(
                                "From " + upgradePath.fromVersion,
                                upgradePath.toVersion.stream().map(
                                    toVersion -> dynamicContainer(
                                        "migrate to " + toVersion,
                                        createMigrateToTest(
                                            upgradePath.fromVersion,
                                            toVersion
                                        )
                                    ))
                            ));
    }

    /**
     * Creates a stream of dynamic nodes for testing the migration of a database from one version to
     * another.
     *
     * @param from      The starting version of the database.
     * @param toVersion The target version of the database.
     * @return A stream of dynamic nodes representing the test cases for migrating the database.
     */
    public Stream<DynamicNode> createMigrateToTest(String from, String toVersion) {
        return TEST_DATABASE_FACTORIES_LIST.stream()
                                           .map(d -> dynamicTest(
                                               d.getName(),
                                               () -> migrateToScriptTest(
                                                   from,
                                                   toVersion,
                                                   d
                                               )
                                           ));
    }

    /**
     * Migrates a database from one version to another using a migration script.
     *
     * @param fromVersion       The starting version of the database.
     * @param toVersion         The target version of the database.
     * @param dataSourceFactory The TestDatabaseFactory used to create a new database.
     */
    public void migrateToScriptTest(
        String fromVersion, String toVersion, TestDatabaseFactory dataSourceFactory) {
        LOGGER.info(
            "Running migrateToScriptTest fromVersion [{}] to Version [{}]", fromVersion, toVersion);
        String migrateScriptLocation =
            MIGRATE_SCRIPTS_CLASSPATH + dataSourceFactory.getDatabaseType() + "_migrate_"
                + fromVersion + "_to_" + toVersion + ".sql";
        Resource migrateScriptResource = new ClassPathResource(migrateScriptLocation);

        Assertions.assertThat(migrateScriptResource.exists())
                  .as(
                      "The migrate database script mus be available under %s",
                      migrateScriptLocation
                  ).isTrue();
        Assumptions.assumeTrue(
            dataSourceFactory.isAvailable(fromVersion),
            "TestDatabase " + dataSourceFactory.getName() + " must be available in version "
                + fromVersion
        );
        try (TestDatabase newDatabase = dataSourceFactory.createNewDatabase(fromVersion)) {
            DataSource dataSource = newDatabase.getDataSource();
            Connection connection = dataSource.getConnection();
            ScriptUtils.executeSqlScript(connection, migrateScriptResource);
            LOGGER.info(
                "Successfully run migrateToScript [{}] fromVersion [{}] to Version [{}]",
                migrateScriptLocation, fromVersion, toVersion
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TestFactory
    Stream<DynamicContainer> testInitialScripts() {
        return INITIAL_VERSIONS.stream()
                               .map(version -> dynamicContainer(
                                   "Initial to " + version,
                                   checkInitialScriptTest(version)
                               ));
    }

    /**
     * Returns a stream of dynamic nodes for checking the initial script test.
     *
     * @param version The version of the initial script.
     * @return A stream of dynamic nodes.
     */
    public Stream<DynamicNode> checkInitialScriptTest(String version) {
        return TEST_DATABASE_FACTORIES_LIST.stream()
                                           .map(d -> dynamicTest(
                                               d.getName(),
                                               () -> checkInitialScriptTest(version, d)
                                           ));
    }

    /**
     * Checks the availability and execution of the initial database script test.
     *
     * @param initialScriptVersion The version of the initial script.
     * @param dataSourceFactory    The TestDatabaseFactory used to create a new database.
     */
    public void checkInitialScriptTest(
        String initialScriptVersion, TestDatabaseFactory dataSourceFactory) {

        String initialScriptLocation =
            INITIAL_SCRIPTS_CLASSPATH + dataSourceFactory.getDatabaseType() + "_"
                + initialScriptVersion + "_initial.sql";
        Resource initialScriptResource = new ClassPathResource(initialScriptLocation);

        Assertions.assertThat(initialScriptResource.exists())
                  .as(
                      "The initial database script mus be available under %s",
                      initialScriptLocation
                  ).isTrue();

        Assumptions.assumeTrue(
            dataSourceFactory.isAvailable(null), "TestDatabase " + dataSourceFactory.getName()
                + " must be available empty!");
        try (TestDatabase db = dataSourceFactory.createNewDatabase(null)) {
            DataSource dataSource = db.getDataSource();
            Connection connection = dataSource.getConnection();
            ScriptUtils.executeSqlScript(connection, initialScriptResource);
            LOGGER.info("Successfully run initialScript [{}]", initialScriptLocation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
