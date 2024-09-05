/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration class for common properties used by the connector.
 */
@Component
@ConfigurationProperties(prefix = CommonProperties.COMMON_PROPERTIES_CONFIG_PREFIX)
// @PropertySource("classpath:/eu/ecodex/connector/security/spring/
// security-default-configuration.properties")
@Validated
@Data
public class CommonProperties {
    public static final String COMMON_PROPERTIES_CONFIG_PREFIX = "connector.common";
    /**
     * Should the startup initializing fail if an invalid property is detected? default is true
     * CAVE: even if false, the startup may fail if the property is required for initializing.
     */
    private boolean failOnInvalidProperty = true;
}
