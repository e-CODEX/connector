package eu.domibus.connector.persistence.testutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class OracleContainerTestDatabaseFactory extends AbstractContainerTestDatabaseFactory implements TestDatabaseFactory {
    private static final Logger LOGGER = LogManager.getLogger(OracleContainerTestDatabaseFactory.class);
    public static final String SID = "testsid";
    public static final String DB_DOMAIN = "example.com";
    public static final String DB_PASSWORD = "test";

    List<String> availableVersions = Stream.of("4.1.x", "3.5.x").collect(Collectors.toList());

    @Override
    public String getDatabaseType() {
        return "oracle";
    }

    @Override
    public String getName() {
        return "Oracle within Docker";
    }

    public TestDatabase createNewDatabase(String version) {
        ContainerTestDatabase testDatabase = new ContainerTestDatabase();
        JdbcDatabaseContainer dbContainer = getDatabaseContainer(version);
        dbContainer.start();

        testDatabase.jdbcDatabaseContainer = dbContainer;
        testDatabase.version = version;

        String driverClassName = dbContainer.getDriverClassName();

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

    protected JdbcDatabaseContainer getDatabaseContainer(String version) {
        OracleContainer oracle = new OracleContainer("oracleinanutshell/oracle-xe-11g:1.0.0");
        oracle.withUsername("system").withPassword("oracle");

        return oracle;
    }

    @Override
    public boolean isAvailable(String version) {
        return (availableVersions.contains(version) || version == null) && super.isDockerAndDriverAvailable(version);
    }
}
