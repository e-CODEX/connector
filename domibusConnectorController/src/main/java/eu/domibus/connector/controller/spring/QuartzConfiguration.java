/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
