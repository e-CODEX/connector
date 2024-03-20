package eu.domibus.connector.persistence.liquibase;

import eu.domibus.connector.persistence.testutil.FromVersion;
import eu.domibus.connector.persistence.testutil.LiquibaseTemplateInvocationContextProvider;
import eu.domibus.connector.testutil.junit5.SetMdcContextExtension;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.configuration.ConfigurationProperty;
import liquibase.configuration.GlobalConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.logging.LogFactory;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@Disabled("defect")
@ExtendWith({LiquibaseTemplateInvocationContextProvider.class, SetMdcContextExtension.class})
public class LiquibaseUpgradeInitITCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseUpgradeInitITCase.class);

    //Test context which does load only spring and liquibase to test
    //liquibase initialization
    @SpringBootApplication(scanBasePackages = "not.existance.packages")
    public static class LiquibaseUpgradeTestConfiguration {

        @Configuration
        public class PropertiesLogger {
            private final Logger log = LoggerFactory.getLogger(PropertiesLogger.class);

            @Autowired
            private AbstractEnvironment environment;

            @PostConstruct
            public void printProperties() {

                log.info("**** APPLICATION PROPERTIES SOURCES ****");

                Set<String> properties = new TreeSet<>();
                for (PropertiesPropertySource p : findPropertiesPropertySources()) {
                    log.info(p.toString());
                    properties.addAll(Arrays.asList(p.getPropertyNames()));
                }

                log.info("**** APPLICATION PROPERTIES VALUES ****");
                print(properties);

            }

            private List<PropertiesPropertySource> findPropertiesPropertySources() {
                List<PropertiesPropertySource> propertiesPropertySources = new LinkedList<>();
                for (PropertySource<?> propertySource : environment.getPropertySources()) {
                    if (propertySource instanceof PropertiesPropertySource) {
                        propertiesPropertySources.add((PropertiesPropertySource) propertySource);
                    }
                }
                return propertiesPropertySources;
            }

            private void print(Set<String> properties) {
                for (String propertyName : properties) {
                    log.info("{}={}", propertyName, environment.getProperty(propertyName));
                }
            }

        }

    }

    @TestTemplate
    @FromVersion("")
    protected void checkInitialScripts(Properties props) {
        System.out.println("\n\n\n######################\nRUNNING TEST: checkInstallDB");
        props.put("spring.liquibase.change-log", "classpath:/db/changelog/install-4.3.xml");
        checkLiquibaseRuns(props);
    }

//    @TestTemplate
//    @FromVersion("3.5.x")
//    protected void checkUpgradeFrom3_5_x(Properties props) {
//        System.out.println("\n\n\n######################\nRUNNING TEST: checkInstallDB");
//        props.put("spring.liquibase.change-log", "classpath:/db/changelog/install-4.2.xml");
//        checkLiquibaseRuns(props);
//    }

    @TestTemplate
    @FromVersion("4.1.x")
    protected void checkUpgradeFrom4_1_x(Properties props) {
        System.out.println("\n\n\n######################\nRUNNING TEST: checkInstallDB");
        props.put("spring.liquibase.change-log", "classpath:/db/changelog/install-4.3.xml");
        checkLiquibaseRuns(props);
    }

    @TestTemplate
    @FromVersion("4.2.x")
    protected void checkUpgradeFrom4_2_x(Properties props) {
        System.out.println("\n\n\n######################\nRUNNING TEST: checkInstallDB");
        props.put("spring.liquibase.change-log", "classpath:/db/changelog/install-4.3.xml");
        checkLiquibaseRuns(props);
    }


    public void checkLiquibaseRuns(Properties p) {
        Assumptions.assumeTrue(p.get("testdb.name") != null, "Test database must be available!");

        p.setProperty("debug", "true");
//        p.setProperty("spring.datasource.url", "jdbc:mysql://localhost:32781/test");

        LOGGER.info("Running test with Properties: [{}]", p);

        List<String> args = new ArrayList<>();

        args.add("--spring.datasource.url=" + p.getProperty("spring.datasource.url"));
        args.add("--spring.datasource.username=" + p.getProperty("spring.datasource.username"));
        if (!StringUtils.isEmpty(p.getProperty("spring.datasource.password"))) {
            args.add("--spring.datasource.password=" + p.getProperty("spring.datasource.password"));
        }
        args.add("--spring.datasource.driver-class-name=" + p.getProperty("spring.datasource.driver-class-name"));
        args.add("--spring.liquibase.change-log=" + p.getProperty("spring.liquibase.change-log"));

        Assertions.assertTimeout(Duration.ofSeconds(90), () -> {

            ConfigurableApplicationContext ctx = new SpringApplicationBuilder(LiquibaseUpgradeInitITCase.LiquibaseUpgradeTestConfiguration.class)
                    .profiles("test")
                    .properties(p) //not working!
                    .run(args.toArray(new String[args.size()]));

            try {
                DataSource ds = ctx.getBean(DataSource.class);
                //TODO: test / verify DB
                Connection connection = ds.getConnection();
                Assertions.assertNotNull(connection);
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                ctx.close();
            }
        });
    }

}
