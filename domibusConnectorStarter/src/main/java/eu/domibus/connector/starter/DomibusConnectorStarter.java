/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.starter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.SystemPropertyUtils;

/**
 * The DomibusConnectorStarter class is the entry point for starting the Domibus Connector
 * application.
 * It is responsible for configuring the Spring application context and starting the application.
 *
 *<p>The following properties can be configured:
 * - spring cloud bootstrap name: spring.cloud.bootstrap.name
 * - spring cloud bootstrap location: spring.cloud.bootstrap.location
 * - spring config location: spring.config.location
 * - spring config name: spring.config.name
 * - connector config file: connector.config.file
 * - connector config location: connector.config.location
 */
@SpringBootApplication(scanBasePackages = "eu.domibus.connector")
@EnableTransactionManagement
@PropertySource({"classpath:/config/connector.properties"})
public class DomibusConnectorStarter extends SpringBootServletInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(DomibusConnectorStarter.class);
    public static final String SPRING_CLOUD_BOOTSTRAP_NAME_PROPERTY_NAME =
        "spring.cloud.bootstrap.name";
    public static final String SPRING_CLOUD_BOOTSTRAP_LOCATION_PROPERTY_NAME =
        "spring.cloud.bootstrap.location";
    public static final String SPRING_CONFIG_LOCATION_PROPERTY_NAME = "spring.config.location";
    public static final String SPRING_CONFIG_NAME_PROPERTY_NAME = "spring.config.name";
    public static final String CONNECTOR_CONFIG_FILE_PROPERTY_NAME = "connector.config.file";
    public static final String CONNECTOR_CONFIG_LOCATION_PROPERTY_NAME =
        "connector.config.location";
    public static final String DEFAULT_SPRING_CONFIG_NAME = "connector";
    public static final String DEFAULT_SPRING_CONFIG_LOCATION =
        "optional:classpath:/config/,optional:file:./config/,optional:file:./config/connector/";
    private static final String FILE_PREFIX = "file:";
    String springConfigLocation = DEFAULT_SPRING_CONFIG_LOCATION;
    String springConfigName = DEFAULT_SPRING_CONFIG_NAME;
    String bootstrapConfigLocation = DEFAULT_SPRING_CONFIG_LOCATION;
    String bootStrapConfigName = DEFAULT_SPRING_CONFIG_NAME;
    Properties springApplicationProperties = new Properties();

    public static void main(String[] args) {
        runSpringApplication(args);
    }

    /**
     * Runs the Spring application using the provided command line arguments.
     *
     * @param args the command line arguments
     * @return the running Spring application context
     */
    public static ConfigurableApplicationContext runSpringApplication(String[] args) {
        var starter = new DomibusConnectorStarter();
        return starter.run(args);
    }

    private ConfigurableApplicationContext run(String[] args) {
        var builder = new SpringApplicationBuilder();
        builder = configureApplicationContext(builder);
        var springApplication = builder.build();
        return springApplication.run(args);
    }

    /**
     * Loads the connector configuration properties from the specified file.
     *
     * @param connectorConfigFile the path to the connector configuration file
     * @return the loaded connector configuration properties
     * @throws RuntimeException if the specified file does not exist or cannot be loaded
     */
    public static Properties loadConnectorConfigProperties(String connectorConfigFile) {
        var properties = new Properties();
        if (connectorConfigFile != null) {
            if (connectorConfigFile.startsWith(FILE_PREFIX)) {
                connectorConfigFile = connectorConfigFile.substring(FILE_PREFIX.length() - 1);
            }
            var connectorConfigFilePath = Paths.get(connectorConfigFile);
            if (!Files.exists(connectorConfigFilePath)) {
                var errorString = String.format(
                    "Cannot start because the via System Property [%s] provided config file [%s] "
                        + "mapped to path [%s] does not exist!",
                    CONNECTOR_CONFIG_FILE_PROPERTY_NAME, connectorConfigFile,
                    connectorConfigFilePath
                );
                LOG.error(errorString);
                throw new RuntimeException(errorString);
            }
            try {
                properties.load(new FileInputStream(connectorConfigFilePath.toFile()));
                return properties;
            } catch (IOException e) {
                throw new RuntimeException(String.format(
                    "Cannot load properties from file [%s], is it a valid and readable properties "
                        + "file?",
                    connectorConfigFilePath
                ), e);
            }
        }
        return properties;
    }

    /**
     * Returns the property name for the connector configuration file. If the property is set in the
     * system properties, it returns the resolved value after resolving any placeholders. If the
     * property is not set, it returns null.
     *
     * @return the property name for the connector configuration file, or null if not set
     */
    public static @Nullable String getConnectorConfigFilePropertyName() {
        String connectorConfigFile = System.getProperty(CONNECTOR_CONFIG_FILE_PROPERTY_NAME);
        if (connectorConfigFile != null) {
            connectorConfigFile = SystemPropertyUtils.resolvePlaceholders(connectorConfigFile);
            return connectorConfigFile;
        }
        return null;
    }

    /**
     * Configures the application context for the Spring application.
     *
     * @param application the SpringApplicationBuilder to configure
     * @return the configured SpringApplicationBuilder
     */
    public SpringApplicationBuilder configureApplicationContext(
        SpringApplicationBuilder application) {
        String connectorConfigFile = getConnectorConfigFilePropertyName();
        if (connectorConfigFile != null) {

            if (!connectorConfigFile.startsWith(FILE_PREFIX)) {
                connectorConfigFile = FILE_PREFIX + connectorConfigFile;
            }

            springApplicationProperties.setProperty(
                SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile);
        } else {
            springApplicationProperties.setProperty(
                SPRING_CLOUD_BOOTSTRAP_LOCATION_PROPERTY_NAME, bootstrapConfigLocation);
            springApplicationProperties.setProperty(
                SPRING_CLOUD_BOOTSTRAP_NAME_PROPERTY_NAME, bootStrapConfigName);
            springApplicationProperties.setProperty(
                SPRING_CONFIG_LOCATION_PROPERTY_NAME, springConfigLocation);
            springApplicationProperties.setProperty(
                SPRING_CONFIG_NAME_PROPERTY_NAME, springConfigName);
            LOG.warn(
                "SystemProperty \"{}\" not given or not resolvable! Startup using default spring "
                    + "external configuration!",
                CONNECTOR_CONFIG_FILE_PROPERTY_NAME
            );
        }
        application.properties(
            springApplicationProperties
        ); // pass the mapped CONNECTOR_CONFIG_FILE to the spring properties...
        return application.sources(DomibusConnectorStarter.class);
    }

    /**
     * Will only be called if the Application is deployed within a web application server adds to
     * the boostrap and spring config location search path a web application context dependent
     * search path: app deployed under context /connector will look also for config under
     * [workingpath]/config/[webcontext]/, [workingpath]/conf/[webcontext]/.
     *
     * @param servletContext the servlet context
     * @throws ServletException in case of an error @see
     *                          {@link SpringBootServletInitializer#onStartup(ServletContext)}
     *                          {@inheritDoc}
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (servletContext != null) {
            bootstrapConfigLocation = bootstrapConfigLocation
                + ",optional:file:./config/" + servletContext.getContextPath() + "/"
                + ",optional:file:./conf/" + servletContext.getContextPath() + "/";
            springApplicationProperties.setProperty(
                SPRING_CLOUD_BOOTSTRAP_LOCATION_PROPERTY_NAME, bootstrapConfigLocation);

            springConfigLocation = springConfigLocation
                + ",optional:file:./config/" + servletContext.getContextPath() + "/"
                + ",optional:file:./conf/" + servletContext.getContextPath() + "/";
            springApplicationProperties.setProperty(
                SPRING_CONFIG_LOCATION_PROPERTY_NAME, springConfigLocation);
        }

        // read logging.config from connector properties and set it before the application context
        // ist started so it is already available for the spring logging servlet initializer
        // to configure logging!
        String connectorConfigFile = getConnectorConfigFilePropertyName();
        if (connectorConfigFile != null) {
            var properties = loadConnectorConfigProperties(connectorConfigFile);
            String loggingConfig = properties.getProperty("logging.config");
            if (loggingConfig != null) {
                servletContext.setInitParameter("logging.config", loggingConfig);
            }
        }
        super.onStartup(servletContext);
    }

    /***
     * {@inheritDoc}
     *
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return configureApplicationContext(application);
    }
}
