package eu.domibus.connector.persistence.testutil;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.common.service.ConfigurationPropertyLoaderServiceImpl;
import eu.domibus.connector.common.service.DCBusinessDomainManagerImpl;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;


@SpringBootApplication(
        scanBasePackages = {"eu.domibus.connector.utils", "eu.domibus.connector.persistence", "eu.domibus.connector" +
                ".common.persistence"}
)
@EnableConfigurationProperties(ConnectorConfigurationProperties.class)
@Import(
        {ConfigurationPropertyLoaderServiceImpl.class,
                DCBusinessDomainManagerImpl.class,
                BeanToPropertyMapConverter.class}
)
public class SetupPersistenceContext {
    static ConfigurableApplicationContext APPLICATION_CONTEXT;
    private DatabaseDataSourceConnection dbUnitConnection;

    public static Properties getDefaultProperties() {
        Properties props = new Properties();
        String dbName =
                UUID.randomUUID().toString().substring(0, 10); // create random db name to avoid conflicts between tests
        props.put("dbname", dbName);
        props.put("connector.persistence.big-data-impl-class",
                  "eu.domibus.connector.persistence.service.impl.DomibusConnectorBigDataPersistenceServiceJpaImpl");
        props.put("spring.liquibase.change-log", "db/changelog/test/testdata.xml");
        props.put("spring.datasource.url", "jdbc:h2:mem:" + dbName);
        props.put("spring.active.profiles", "connector,db-storage");
        return props;
    }

    public static Set<String> getDefaultProfiles() {
        Set<String> defaultProfiles = new HashSet<String>();
        defaultProfiles.addAll(Arrays.asList("test", "db_h2", STORAGE_DB_PROFILE_NAME));
        return defaultProfiles;
    }

    public static ConfigurableApplicationContext startApplicationContext() {
        return startApplicationContext(getDefaultProperties());
    }

    public static ConfigurableApplicationContext startApplicationContext(Class<?>... sources) {
        return startApplicationContext(getDefaultProperties(), sources);
    }

    public static ConfigurableApplicationContext startApplicationContext(Properties props, Set<String> profiles) {
        return startApplicationContext(props, profiles, SetupPersistenceContext.class);
    }

    public static ConfigurableApplicationContext startApplicationContext(Properties props) {
        return startApplicationContext(props, SetupPersistenceContext.class);
    }

    public static ConfigurableApplicationContext startApplicationContext(Properties props, Class<?>... sources) {
        Set<String> profiles = getDefaultProfiles();
        return startApplicationContext(props, profiles, sources);
    }

    public static ConfigurableApplicationContext startApplicationContext(
            Properties props,
            Set<String> profiles,
            Class<?>... sources) {
        return startApplicationContext(props, profiles, sources, new String[]{});
    }

    public static ConfigurableApplicationContext startApplicationContext(
            Properties props,
            Set<String> profiles,
            Class<?>[] sources,
            String[] args) {
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

    public static void main(String... args) {
        startApplicationContext(
                getDefaultProperties(),
                getDefaultProfiles(),
                new Class<?>[]{SetupPersistenceContext.class},
                args
        );
    }

    @Bean
    public DatabaseDataSourceConnection getDbUnitConnection(DataSource ds) {
        if (this.dbUnitConnection != null) {
            return dbUnitConnection;
        }
        try {
            DatabaseDataSourceConnection conn = null;
            conn = new DatabaseDataSourceConnection(ds);
            DatabaseConfig config = conn.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new org.dbunit.ext.h2.H2DataTypeFactory());
            this.dbUnitConnection = conn;
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
