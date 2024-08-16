/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This class represents the configuration properties for the Connector UI. It is annotated with
 * `@Component` and `@ConfigurationProperties`, and is used to define the configuration properties
 * for the Connector UI.
 */
@Data
@Component
@ConfigurationProperties(prefix = "connector.ui")
public class ConnectorUiConfigurationProperties {
    /**
     * Should the user be automatically logged in should only be used for development.
     */
    boolean autoLoginEnabled = false;
    /**
     * Which user should be automatically logged in? note: user must exist in database.
     */
    String autoLoginUser = "";
    String autoLoginPassword = "";
    String defaultRoute = null;
}
