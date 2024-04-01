package eu.domibus.connector.ui.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * Web module specific configuration
 */
@Configuration
@PropertySource({"classpath:/eu/domibus/connector/web/spring/web-default-configuration.properties"})
public class WebConfiguration {
}
