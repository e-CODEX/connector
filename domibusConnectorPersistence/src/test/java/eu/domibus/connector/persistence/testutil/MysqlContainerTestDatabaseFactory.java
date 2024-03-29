package eu.domibus.connector.persistence.testutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MysqlContainerTestDatabaseFactory extends AbstractContainerTestDatabaseFactory implements TestDatabaseFactory {
    private static final Logger LOGGER = LogManager.getLogger(MysqlContainerTestDatabaseFactory.class);

    List<String> availableVersions = Stream.of("4.1.x", "3.5.x").collect(Collectors.toList());

    @Override
    public String getDatabaseType() {
        return "mysql";
    }

    @Override
    public String getName() {
        return "Mysql within Docker";
    }

    public TestDatabase createNewDatabase(String version) {
        TestDatabase newDatabase = super.createNewDatabase(version);

        if (version != null) {
            String scriptFile = "/dbscripts/test/mysql/mysql_" + version + ".sql";
            LOGGER.info("Loading initial script from [{}]", scriptFile);
            try {
                //            Connection connection = newDatabase.getDataSource().getConnection("test", "test");
                Connection connection = newDatabase.getDataSource().getConnection();

                ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptFile));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return newDatabase;
    }

    protected JdbcDatabaseContainer getDatabaseContainer(String version) {

        //        URL url = getClass().getResource("/dbscripts/test/mysql_4.1.x.sql");
        //        assertThat(url).isNotNull();

        MySQLContainer mysql = new MySQLContainer();
        //        if ("4.1.x".equals(version)) {
        //            mysql.withInitScript("/dbscripts/test/mysql_4.1.x.sql");
        //        }

        return mysql;
    }

    @Override
    public boolean isAvailable(String version) {

        return (availableVersions.contains(version) || version == null) && super.isDockerAndDriverAvailable(version);
    }
}
