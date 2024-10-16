/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.spring;

import eu.ecodex.connector.lib.spring.DomibusConnectorDuration;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for content deletion in the connector controller.
 */
@Data
@Component(ContentDeletionConfigurationProperties.BEAN_NAME)
@ConfigurationProperties(prefix = ContentDeletionConfigurationProperties.PREFIX)
@Validated
public class ContentDeletionConfigurationProperties {
    public static final String BEAN_NAME = "ContentDeletionConfigurationProperties";
    public static final String PREFIX = "connector.controller.content";
    private boolean checkTimeoutEnabled = true;
    private DomibusConnectorDuration checkTimeout;

    /**
     * This method validates the configuration properties for content deletion in the connector
     * controller.
     * It checks if checkTimeoutEnabled is true and checkTimeout is null, and throws an
     * IllegalArgumentException if it is.
     * The error message will indicate that if checkTimeoutEnabled is true, then checkTimeout
     * must be set to a duration (eg: 24h).
     *
     * @throws IllegalArgumentException if checkTimeoutEnabled is true and checkTimeout is null
     */
    @PostConstruct
    public void validate() {
        if (checkTimeoutEnabled && checkTimeout == null) {
            var error = String.format(
                "If %s.check-timeout-enabled is true, then %s.check-timeout must be set to a "
                    + "duration (eg. 24h)",
                PREFIX, PREFIX
            );
            throw new IllegalArgumentException(error);
        }
    }
}
