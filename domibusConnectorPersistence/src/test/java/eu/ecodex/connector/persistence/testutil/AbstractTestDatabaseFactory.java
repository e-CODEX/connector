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
import org.junit.jupiter.api.Assumptions;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * Abstract class that implements the TestDatabaseFactory interface. This class provides common
 * functionality for creating a new database for testing purposes.
 */
public abstract class AbstractTestDatabaseFactory implements TestDatabaseFactory {
    class MyTestDatabase implements TestDatabase {
        String driverClassName;
        String jdbcUrl;
        String username;
        String password;
        String version = null;

        @Override
        public DataSource getDataSource() {
            return DataSourceBuilder
                .create()
                .driverClassName(driverClassName)
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .build();
        }

        @Override
        public Properties getProperties() {
            Properties p = new Properties();
            p.setProperty("spring.datasource.driver-class-name", driverClassName);
            p.setProperty("spring.datasource.url", jdbcUrl);
            p.setProperty("spring.datasource.username", username);
            p.setProperty("spring.datasource.password", password);
            return p;
        }

        @Override
        public String getName() {
            return String.format(
                "%s data: [%s]", getDatabaseType(), version == null ? "empty" : version
            );
        }

        @Override
        public void close() throws Exception {
            // cannot really shut down remote db...
        }
    }

    @Override
    public MyTestDatabase createNewDatabase(String version) {
        isAvailable(version);

        MyTestDatabase mysqlTestDatabase = new MyTestDatabase();

        mysqlTestDatabase.driverClassName =
            System.getProperty(
                "test.db." + getDatabaseType() + ".driverclassname",
                getDriverClassName()
            );
        mysqlTestDatabase.jdbcUrl = System.getProperty("test.db." + getDatabaseType() + ".url");
        mysqlTestDatabase.username =
            System.getProperty("test.db." + getDatabaseType() + ".username");
        mysqlTestDatabase.password =
            System.getProperty("test.db." + getDatabaseType() + ".password");
        mysqlTestDatabase.version = version;

        try {
            Class.forName(mysqlTestDatabase.driverClassName);
        } catch (ClassNotFoundException e) {
            Assumptions.assumeTrue(
                false, String.format(
                    "Cannot load %s driver %s", getDatabaseType(), mysqlTestDatabase.driverClassName
                ));
        }

        return mysqlTestDatabase;
    }

    protected abstract String getDriverClassName();

    @Override
    public boolean isAvailable(String version) {
        if (version != null) {
            Assumptions.assumeTrue(true, "Cannot provide db with data in version " + version);
        }
        Assumptions.assumeTrue(
            "true".equalsIgnoreCase(System.getProperty("test.db.mysql.enabled")),
            String.format(
                """
                    Native Mysql not available! Enable by setting following system properties
                    test.db.%1$s.enabled=true
                    test.db.%1$s.driverclassname=<driverClassName>
                    test.db.%1$s.url=<driver url>
                    test.db.%1$s.username=<username>
                    test.db.%1$s.password=<password>""", getDatabaseType())
        );
        return true;
    }
}
