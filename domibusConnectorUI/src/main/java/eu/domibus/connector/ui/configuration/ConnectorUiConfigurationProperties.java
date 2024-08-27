/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
