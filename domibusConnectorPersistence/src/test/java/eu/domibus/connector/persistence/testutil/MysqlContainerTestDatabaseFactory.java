package eu.domibus.connector.persistence.testutil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

/**
 * MysqlContainerTestDatabaseFactory is a concrete implementation of the TestDatabaseFactory
 * interface, providing functionalities for creating and managing MySQL databases within Docker
 * containers.
 *
 * <p>This class extends the AbstractContainerTestDatabaseFactory class and implements the
 * TestDatabaseFactory interface.
 *
 * <p>The class provides methods for obtaining the database type, name, creating a new database,
 * checking availability, and getting the corresponding MySQL container for a specific version.
 */
public class MysqlContainerTestDatabaseFactory extends AbstractContainerTestDatabaseFactory
    implements TestDatabaseFactory {
    private static final Logger LOGGER =
        LogManager.getLogger(MysqlContainerTestDatabaseFactory.class);
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
        return new MySQLContainer();
    }

    @Override
    public TestDatabase createNewDatabase(String version) {
        TestDatabase newDatabase = super.createNewDatabase(version);

        if (version != null) {
            String scriptFile = "/dbscripts/test/mysql/mysql_" + version + ".sql";
            LOGGER.info("Loading initial script from [{}]", scriptFile);
            try {
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
