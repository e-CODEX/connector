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

import eu.ecodex.connector.persistence.testutil.FromVersion;
import eu.ecodex.connector.persistence.testutil.LiquibaseTemplateInvocationContextProvider;
import eu.ecodex.connector.testutil.junit5.SetMdcContextExtension;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

@SuppressWarnings("squid:S1135")
@Disabled("defect")
@ExtendWith({LiquibaseTemplateInvocationContextProvider.class, SetMdcContextExtension.class})
class LiquibaseUpgradeInitITCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseUpgradeInitITCase.class);

    // Test context which does load only spring and liquibase to test
    // liquibase initialization
    @SpringBootApplication(scanBasePackages = "not.existance.packages")
    public static class LiquibaseUpgradeTestConfiguration {
        @Configuration
        public static class PropertiesLogger {
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
    void checkInitialScripts(Properties props) {
        System.out.println("\n\n\n######################\nRUNNING TEST: checkInstallDB");
        props.put("spring.liquibase.change-log", "classpath:/db/changelog/install-4.3.xml");
        checkLiquibaseRuns(props);
    }

    @TestTemplate
    @FromVersion("4.1.x")
    void checkUpgradeFrom4_1_x(Properties props) {
        System.out.println("\n\n\n######################\nRUNNING TEST: checkInstallDB");
        props.put("spring.liquibase.change-log", "classpath:/db/changelog/install-4.3.xml");
        checkLiquibaseRuns(props);
    }

    @TestTemplate
    @FromVersion("4.2.x")
    void checkUpgradeFrom4_2_x(Properties props) {
        System.out.println("\n\n\n######################\nRUNNING TEST: checkInstallDB");
        props.put("spring.liquibase.change-log", "classpath:/db/changelog/install-4.3.xml");
        checkLiquibaseRuns(props);
    }

    public void checkLiquibaseRuns(Properties p) {
        Assumptions.assumeTrue(p.get("testdb.name") != null, "Test database must be available!");

        p.setProperty("debug", "true");

        LOGGER.info("Running test with Properties: [{}]", p);

        List<String> args = new ArrayList<>();

        args.add("--spring.datasource.url=" + p.getProperty("spring.datasource.url"));
        args.add("--spring.datasource.username=" + p.getProperty("spring.datasource.username"));
        if (StringUtils.hasLength(p.getProperty("spring.datasource.password"))) {
            args.add("--spring.datasource.password=" + p.getProperty("spring.datasource.password"));
        }
        args.add("--spring.datasource.driver-class-name=" + p.getProperty(
            "spring.datasource.driver-class-name"));
        args.add("--spring.liquibase.change-log=" + p.getProperty("spring.liquibase.change-log"));

        Assertions.assertTimeout(Duration.ofSeconds(90), () -> {

            // not working!

            try (ConfigurableApplicationContext ctx = new SpringApplicationBuilder(
                LiquibaseUpgradeTestConfiguration.class)
                .profiles("test")
                .properties(p) // not working!
                .run(args.toArray(new String[args.size()]))) {
                DataSource ds = ctx.getBean(DataSource.class);
                // TODO: test / verify DB
                Connection connection = ds.getConnection();
                Assertions.assertNotNull(connection);
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
