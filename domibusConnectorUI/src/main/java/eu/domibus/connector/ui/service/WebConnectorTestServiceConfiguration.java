/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
