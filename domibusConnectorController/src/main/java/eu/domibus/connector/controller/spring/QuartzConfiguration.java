/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.spring;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import org.quartz.spi.InstanceIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for Quartz scheduler.
 */
@Configuration
@PropertySource("classpath:connector-default-quartz.properties")
public class QuartzConfiguration {
    @Autowired
    ConnectorConfigurationProperties connectorConfigurationProperties;

    @Bean
    QuartzInstanceIdGenerator quartzInstanceIdGenerator() {
        return new QuartzInstanceIdGenerator();
    }

    class QuartzInstanceIdGenerator implements InstanceIdGenerator {
        @Override
        public String generateInstanceId() {
            return connectorConfigurationProperties.getInstanceName();
        }
    }
}
