/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * This class is the configuration class for DCMessageRouting.
 * It is responsible for creating the necessary beans for message routing configuration.
 */
@Configuration
@EnableConfigurationProperties(DCMessageRoutingConfigurationProperties.class)
public class DCMessageRoutingConfiguration {
    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, RoutingRulePattern> routingRulePatternConverter() {
        return new RoutingRulePatternConverter();
    }
}
