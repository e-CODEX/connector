package eu.domibus.connector.ui.service;

import eu.domibus.connector.c2ctests.config.ConnectorTestConfigurationProperties;
import eu.domibus.connector.test.service.DCConnector2ConnectorTestService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WebConnectorTestServiceConfiguration {
    // it might be possible, if the testbackend plugin is not enabled, that the service
    // DCConnector2ConnectorTestService is not available!, in this case do not create WebConnectorTestService bean.
    @Bean
    @ConditionalOnBean({DCConnector2ConnectorTestService.class, ConnectorTestConfigurationProperties.class})
    public WebConnectorTestService webConnectorTestService(
            DCConnector2ConnectorTestService testService,
            ConnectorTestConfigurationProperties config) {
        return new WebConnectorTestService(testService, config);
    }
}
