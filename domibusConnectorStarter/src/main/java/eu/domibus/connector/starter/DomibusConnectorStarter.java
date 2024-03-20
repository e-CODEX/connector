package eu.domibus.connector.starter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.SystemPropertyUtils;

import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication(scanBasePackages = "eu.domibus.connector")
@EnableTransactionManagement
@PropertySource({"classpath:/config/connector.properties"})
public class DomibusConnectorStarter extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorStarter.class);

    public static final String SPRING_CLOUD_BOOTSTRAP_NAME_PROPERTY_NAME = "spring.cloud.bootstrap.name";
    public static final String SPRING_CLOUD_BOOTSTRAP_LOCATION_PROPERTY_NAME = "spring.cloud.bootstrap.location";
    public static final String SPRING_CONFIG_LOCATION_PROPERTY_NAME = "spring.config.location";
    public static final String SPRING_CONFIG_NAME_PROPERTY_NAME = "spring.config.name";

    public static final String CONNECTOR_CONFIG_FILE_PROPERTY_NAME = "connector.config.file";
    public static final String CONNECTOR_CONFIG_LOCATION_PROPERTY_NAME = "connector.config.location";

    public static final String DEFAULT_SPRING_CONFIG_NAME = "connector";
    public static final String DEFAULT_SPRING_CONFIG_LOCATION = "optional:classpath:/config/,optional:file:./config/,optional:file:./config/connector/";

    private static final String FILE_PREFIX = "file:";
    private ServletContext servletContext;

    String springConfigLocation = DEFAULT_SPRING_CONFIG_LOCATION;
    String springConfigName = DEFAULT_SPRING_CONFIG_NAME;

    String bootstrapConfigLocation = DEFAULT_SPRING_CONFIG_LOCATION;
    String bootStrapConfigName = DEFAULT_SPRING_CONFIG_NAME;

    Properties springApplicationProperties = new Properties();

    public static void main(String[] args) {
        runSpringApplication(args);
    }

    public static ConfigurableApplicationContext runSpringApplication(String[] args) {
        DomibusConnectorStarter starter = new DomibusConnectorStarter();
        return starter.run(args);
    }

    private ConfigurableApplicationContext run(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        builder = configureApplicationContext(builder);
        SpringApplication springApplication = builder.build();
        ConfigurableApplicationContext appContext = springApplication.run(args);
        return appContext;
    }


    public static Properties loadConnectorConfigProperties(String connectorConfigFile) {
        Properties p = new Properties();
        if (connectorConfigFile != null) {
            if (connectorConfigFile.startsWith(FILE_PREFIX)) {
                connectorConfigFile = connectorConfigFile.substring(FILE_PREFIX.length() - 1);
            }
            Path connectorConfigFilePath = Paths.get(connectorConfigFile);
            if (!Files.exists(connectorConfigFilePath)) {
                String errorString = String.format("Cannot start because the via System Property [%s] provided config file [%s] mapped to path [%s] does not exist!", CONNECTOR_CONFIG_FILE_PROPERTY_NAME, connectorConfigFile, connectorConfigFilePath);
                LOGGER.error(errorString);
                throw new RuntimeException(errorString);
            }
            try {
                p.load(new FileInputStream(connectorConfigFilePath.toFile()));
                return p;
            } catch (IOException e) {
                throw new RuntimeException(String.format("Cannot load properties from file [%s], is it a valid and readable properties file?", connectorConfigFilePath), e);
            }
        }
        return p;
    }

    public static @Nullable
    String getConnectorConfigFilePropertyName() {
        String connectorConfigFile = System.getProperty(CONNECTOR_CONFIG_FILE_PROPERTY_NAME);
        if (connectorConfigFile != null) {
            connectorConfigFile = SystemPropertyUtils.resolvePlaceholders(connectorConfigFile);
            return connectorConfigFile;
        }
        return null;
    }


    public SpringApplicationBuilder configureApplicationContext(SpringApplicationBuilder application) {
        String connectorConfigFile = getConnectorConfigFilePropertyName();
        if (connectorConfigFile != null) {

            if (!connectorConfigFile.startsWith(FILE_PREFIX)) {
                connectorConfigFile = FILE_PREFIX + connectorConfigFile;
            }

            springApplicationProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigFile);

        } else {
            springApplicationProperties.setProperty(SPRING_CLOUD_BOOTSTRAP_LOCATION_PROPERTY_NAME, bootstrapConfigLocation);
            springApplicationProperties.setProperty(SPRING_CLOUD_BOOTSTRAP_NAME_PROPERTY_NAME, bootStrapConfigName);
            springApplicationProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, springConfigLocation);
            springApplicationProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, springConfigName);
            LOGGER.warn("SystemProperty \"{}\" not given or not resolvable! Startup using default spring external configuration!", CONNECTOR_CONFIG_FILE_PROPERTY_NAME);
        }
        application.properties(springApplicationProperties); //pass the mapped CONNECTOR_CONFIG_FILE to the spring properties...
        return application.sources(DomibusConnectorStarter.class);
    }

    /**
     * Will only be called if the Application is deployed within an web application server
     * adds to the boostrap and spring config location search path a web application context
     * dependent search path:
     *  app deployed under context /connector will look also for config under [workingpath]/config/[webcontext]/,
     *  [workingpath]/conf/[webcontext]/
     *
     * @param servletContext the servlet context
     * @throws ServletException in case of an error @see {@link SpringBootServletInitializer#onStartup(ServletContext)} 
     *
     * {@inheritDoc}
     *
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        this.servletContext = servletContext;
        if (servletContext != null) {

            bootstrapConfigLocation = bootstrapConfigLocation +
                    ",optional:file:./config/" + servletContext.getContextPath() + "/" +
                    ",optional:file:./conf/" + servletContext.getContextPath() + "/";
            springApplicationProperties.setProperty(SPRING_CLOUD_BOOTSTRAP_LOCATION_PROPERTY_NAME, bootstrapConfigLocation);

            springConfigLocation = springConfigLocation +
                    ",optional:file:./config/" + servletContext.getContextPath() + "/" +
                    ",optional:file:./conf/" + servletContext.getContextPath() + "/";
            springApplicationProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, springConfigLocation);

        }

        //read logging.config from connector properties and set it before the application context ist started
        //so its already available for the spring logging servlet initializer to configure logging!
        String connectorConfigFile = getConnectorConfigFilePropertyName();
        if (connectorConfigFile != null) {
            Properties p = loadConnectorConfigProperties(connectorConfigFile);
            String loggingConfig = p.getProperty("logging.config");
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
