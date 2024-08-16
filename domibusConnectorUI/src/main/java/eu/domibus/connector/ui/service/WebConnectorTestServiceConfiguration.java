/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.service;

import eu.domibus.connector.c2ctests.config.ConnectorTestConfigurationProperties;
import eu.domibus.connector.test.service.DCConnector2ConnectorTestService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The WebConnectorTestServiceConfiguration class is a Spring configuration class that defines the
 * creation of the WebConnectorTestService bean. This bean is only created if the testbackend plugin
 * is enabled and both the DCConnector2ConnectorTestService and ConnectorTestConfigurationProperties
 * beans are available.
 *
 * <p>The WebConnectorTestService bean is responsible for providing methods to interact with the
 * Domibus Connector for submitting test messages and retrieving test backend information. It
 * requires an instance of DCConnector2ConnectorTestService and ConnectorTestConfigurationProperties
 * to be instantiated.
 *
 * @see DCConnector2ConnectorTestService
 * @see ConnectorTestConfigurationProperties
 */
@Configuration
public class WebConnectorTestServiceConfiguration {
    // it might be possible, if the testbackend plugin is not enabled, that the service
    // DCConnector2ConnectorTestService is not available!, in this case do not create
    // WebConnectorTestService bean.
    @Bean
    @ConditionalOnBean(
        {DCConnector2ConnectorTestService.class, ConnectorTestConfigurationProperties.class}
    )
    public WebConnectorTestService webConnectorTestService(
        DCConnector2ConnectorTestService testService,
        ConnectorTestConfigurationProperties config) {
        return new WebConnectorTestService(testService, config);
    }
}
