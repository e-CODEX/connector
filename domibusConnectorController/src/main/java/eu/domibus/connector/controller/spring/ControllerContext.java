package eu.domibus.connector.controller.spring;


import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Configures Controller Context
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
@EnableJms
@EnableScheduling
@EnableConfigurationProperties(ConnectorConfigurationProperties.class)
@PropertySource("classpath:/eu/domibus/connector/controller/spring/default-connector.properties")
public class ControllerContext {
}
