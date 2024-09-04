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

import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * H2TestDatabaseFactory is a factory class for creating H2TestDatabase instances.
 */
public class H2TestDatabaseFactory implements TestDatabaseFactory {
    public static final String INITIAL_TEST_SCRIPTS_LOCATION = "dbscripts/test/h2/";
    String dbType;

    /**
     * Creates and returns a new instance of H2TestDatabaseFactory with the dbType set to "oracle".
     *
     * @return a new instance of H2TestDatabaseFactory with the dbType set to "oracle"
     */
    public static H2TestDatabaseFactory h2Oracle() {
        H2TestDatabaseFactory h2DataSourceProvider = new H2TestDatabaseFactory();
        h2DataSourceProvider.dbType = "oracle";
        return h2DataSourceProvider;
    }

    /**
     * Returns a new instance of H2TestDatabaseFactory with the dbType set to "mysql".
     *
     * @return a new instance of H2TestDatabaseFactory with the dbType set to "mysql"
     */
    public static H2TestDatabaseFactory h2Mysql() {
        H2TestDatabaseFactory h2DataSourceProvider = new H2TestDatabaseFactory();
        h2DataSourceProvider.dbType = "mysql";
        return h2DataSourceProvider;
    }

    @Override
    public String getDatabaseType() {
        return dbType;
    }

    @Override
    public String getName() {
        return String.format("H2 %s", dbType);
    }

    class H2TestDatabase implements TestDatabase {
        JdbcDataSource ds;
        String version;

        @Override
        public DataSource getDataSource() {
            return ds;
        }

        @Override
        public Properties getProperties() {
            Properties p = new Properties();
            p.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
            p.setProperty("spring.datasource.url", ds.getURL());
            p.setProperty("spring.datasource.username", ds.getUser());
            p.setProperty("spring.datasource.password", ds.getPassword());
            p.setProperty("testdb.name", getName());
            return p;
        }

        @Override
        public String getName() {
            return String.format("H2 %s data: [%s]", dbType, version == null ? "empty" : version);
        }

        @Override
        public void close() throws Exception {
            RunScript.execute(
                ds.getURL(), ds.getUser(), ds.getPassword(),
                "classpath:/dbscripts/test/h2/shutdown.sql", null, false
            );
        }
    }

    @Override
    public boolean isAvailable(String version) {
        return true;
    }

    @Override
    public TestDatabase createNewDatabase(String version) {
        H2TestDatabase testDatabase = new H2TestDatabase();

        JdbcDataSource ds = new JdbcDataSource();
        testDatabase.ds = ds;
        testDatabase.version = version;

        String jdbcUrl =
            "jdbc:h2:file:./target/testdb/" + UUID.randomUUID().toString().substring(0, 6)
                + ";MODE=" + dbType;

        ds.setURL(jdbcUrl);
        ds.setUser("sa");

        if (version != null) {
            String initialScript =
                INITIAL_TEST_SCRIPTS_LOCATION + "h2_" + dbType + "_" + version + ".sql";
            ClassPathResource classPathResource = new ClassPathResource(initialScript);
            Assertions.assertThat(classPathResource.exists()).as("A initial db script must exist!")
                      .isTrue();
            try {
                ScriptUtils.executeSqlScript(ds.getConnection(), classPathResource);
            } catch (SQLException e) {
                throw new RuntimeException("Test preparation failed", e);
            }
        }

        return testDatabase;
    }
}
