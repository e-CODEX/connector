package eu.domibus.connector.persistence.testutil;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.common.service.ConfigurationPropertyLoaderServiceImpl;
import eu.domibus.connector.common.service.DCBusinessDomainManagerImpl;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import javax.sql.DataSource;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * The SetupPersistenceContext class is responsible for setting up the Spring application context
 * for the persistence module of the connector. It defines methods for starting the application
 * context, retrieving a DatabaseDataSourceConnection, and getting default properties and profiles.
 *
 * <p>This class also includes a main method for starting the application context with default
 * properties and profiles.
 */
@SpringBootApplication(
    scanBasePackages = {"eu.domibus.connector.utils", "eu.domibus.connector.persistence",
        "eu.domibus.connector.common.persistence"}
)
@EnableConfigurationProperties(ConnectorConfigurationProperties.class)
@Import(
    {ConfigurationPropertyLoaderServiceImpl.class,
        DCBusinessDomainManagerImpl.class,
        BeanToPropertyMapConverter.class}
)
public class SetupPersistenceContext {
    private DatabaseDataSourceConnection dbUnitConnection;

    /**
     * Retrieves a DbUnit connection using the given DataSource.
     *
     * @param ds The DataSource object used to establish the connection.
     * @return The DbUnit connection.
     * @throws RuntimeException if there is an error while establishing the connection.
     */
    @Bean
    public DatabaseDataSourceConnection getDbUnitConnection(DataSource ds) {
        if (this.dbUnitConnection != null) {
            return dbUnitConnection;
        }
        try {
            DatabaseDataSourceConnection conn;
            conn = new DatabaseDataSourceConnection(ds);
            DatabaseConfig config = conn.getConfig();
            config.setProperty(
                DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                new org.dbunit.ext.h2.H2DataTypeFactory()
            );
            this.dbUnitConnection = conn;
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("squid:S3008")
    static ConfigurableApplicationContext APPLICATION_CONTEXT;

    /**
     * Returns a {@link Properties} object that contains the default properties for
     * setting up the persistence context.
     *
     * <p>The properties include:
     * <ul>
     *     <li>"dbname": A randomly generated database name to avoid conflicts between tests.
     *     <li>"connector.persistence.big-data-impl-class": The implementation class for
     *     the connector's big data persistence service.
     *     <li>"spring.liquibase.change-log": The change log file for Liquibase.
     *     <li>"spring.datasource.url": The URL for the H2 in-memory database.
     *     <li>"spring.active.profiles": The active profiles for the application context.
     * </ul>
     *
     * @return The default properties for setting up the persistence context.
     */
    @SuppressWarnings("checkstyle:LineLength")
    public static Properties getDefaultProperties() {
        Properties props = new Properties();
        String dbName = UUID.randomUUID().toString().substring(
            0,
            10
        ); // create random db name to avoid conflicts between tests
        props.put("dbname", dbName);
        props.put(
            "connector.persistence.big-data-impl-class",
            "eu.domibus.connector.persistence.service.impl.DomibusConnectorBigDataPersistenceServiceJpaImpl"
        );
        props.put("spring.liquibase.change-log", "db/changelog/test/testdata.xml");
        props.put("spring.datasource.url", "jdbc:h2:mem:" + dbName);
        props.put("spring.active.profiles", "connector,db-storage");
        return props;
    }

    /**
     * Returns a Set of default profiles.
     *
     * <p>The default profiles include "test", "db_h2", and the profile "STORAGE_DB_PROFILE_NAME".
     *
     * @return a Set of default profiles
     */
    public static Set<String> getDefaultProfiles() {
        Set<String> defaultProfiles = new HashSet<>();
        defaultProfiles.addAll(
            Arrays.asList("test", "db_h2", STORAGE_DB_PROFILE_NAME));
        return defaultProfiles;
    }

    public static ConfigurableApplicationContext startApplicationContext() {
        return startApplicationContext(getDefaultProperties());
    }

    public static ConfigurableApplicationContext startApplicationContext(Class<?>... sources) {
        return startApplicationContext(getDefaultProperties(), sources);
    }

    public static ConfigurableApplicationContext startApplicationContext(
        Properties props, Set<String> profiles) {
        return startApplicationContext(props, profiles, SetupPersistenceContext.class);
    }

    public static ConfigurableApplicationContext startApplicationContext(Properties props) {
        return startApplicationContext(props, SetupPersistenceContext.class);
    }

    public static ConfigurableApplicationContext startApplicationContext(
        Properties props, Class<?>... sources) {
        Set<String> profiles = getDefaultProfiles();
        return startApplicationContext(props, profiles, sources);
    }

    public static ConfigurableApplicationContext startApplicationContext(
        Properties props, Set<String> profiles, Class<?>... sources) {
        return startApplicationContext(props, profiles, sources, new String[] {});
    }

    /**
     * This method starts a Spring ApplicationContext using the given properties, profiles, sources,
     * and arguments.
     *
     * @param props    The properties for setting up the Spring ApplicationContext.
     * @param profiles The active profiles for the Spring ApplicationContext.
     * @param sources  The sources of the Spring ApplicationContext.
     * @param args     The additional arguments for starting the Spring ApplicationContext.
     * @return The started Spring ApplicationContext.
     */
    public static ConfigurableApplicationContext startApplicationContext(
        Properties props, Set<String> profiles, Class<?>[] sources, String[] args) {
        ConfigurableApplicationContext applicationContext;

        SpringApplicationBuilder springAppBuilder = new SpringApplicationBuilder()
            .sources(sources)
            .web(WebApplicationType.NONE)
            .profiles(profiles.toArray(new String[profiles.size()]))
            .properties(props);
        applicationContext = springAppBuilder.run(args);
        APPLICATION_CONTEXT = applicationContext;
        System.out.println("APPCONTEXT IS STARTED...:" + applicationContext.isRunning());
        return applicationContext;
    }

    /**
     * This is the main method of the program.
     *
     * @param args The command line arguments.
     */
    public static void main(String... args) {
        startApplicationContext(
            getDefaultProperties(), getDefaultProfiles(),
            new Class<?>[] {SetupPersistenceContext.class}, args
        );
    }
}
