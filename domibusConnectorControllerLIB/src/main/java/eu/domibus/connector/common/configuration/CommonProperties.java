/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration class for common properties used by the connector.
 */
@Component
@ConfigurationProperties(prefix = CommonProperties.COMMON_PROPERTIES_CONFIG_PREFIX)
// @PropertySource("classpath:/eu/domibus/connector/security/spring/
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
