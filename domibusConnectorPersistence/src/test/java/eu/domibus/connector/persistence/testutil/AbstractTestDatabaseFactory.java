package eu.domibus.connector.persistence.testutil;

import org.junit.jupiter.api.Assumptions;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.util.Properties;


public abstract class AbstractTestDatabaseFactory implements TestDatabaseFactory {
    protected abstract String getDriverClassName();

    @Override
    public boolean isAvailable(String version) {
        if (version != null) {
            //            throw new RuntimeException("Cannot provide db with data in version " + version);
            Assumptions.assumeTrue(true, "Cannot provide db with data in version " + version);
        }
        Assumptions.assumeTrue(
                "true".equalsIgnoreCase(System.getProperty("test.db.mysql.enabled")),
                String.format("\nNative Mysql not available! Enable by setting following system properties" +
                                      "\ntest.db.%1$s.enabled=true" +
                                      "\ntest.db.%1$s.driverclassname=<driverClassName>" +
                                      "\ntest.db.%1$s.url=<driver url>" +
                                      "\ntest.db.%1$s.username=<username>" +
                                      "\ntest.db.%1$s.password=<password>", getDatabaseType())
        );
        return true;
    }

    @Override
    public MyTestDatabase createNewDatabase(String version) {
        isAvailable(version);

        MyTestDatabase mysqlTestDatabase = new MyTestDatabase();

        mysqlTestDatabase.driverClassName =
                System.getProperty("test.db." + getDatabaseType() + ".driverclassname", getDriverClassName());
        mysqlTestDatabase.jdbcUrl = System.getProperty("test.db." + getDatabaseType() + ".url");
        mysqlTestDatabase.username = System.getProperty("test.db." + getDatabaseType() + ".username");
        mysqlTestDatabase.password = System.getProperty("test.db." + getDatabaseType() + ".password");
        mysqlTestDatabase.version = version;

        try {
            Class.forName(mysqlTestDatabase.driverClassName);
        } catch (ClassNotFoundException e) {
            Assumptions.assumeTrue(false,
                                   String.format(
                                           "Cannot load " + getDatabaseType() + " driver %s",
                                           mysqlTestDatabase.driverClassName
                                   )
            );
        }

        return mysqlTestDatabase;
    }

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
            return String.format(getDatabaseType() + " %s data: [%s]", version == null ? "empty" : version);
        }

        @Override
        public void close() throws Exception {
            // cannot really shutdown remote db...
        }
    }
}
