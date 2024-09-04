/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.routing;

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
