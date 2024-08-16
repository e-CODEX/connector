/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Web module specific configuration.
 */
@Configuration
@PropertySource(
    {
        "classpath:/eu/domibus/connector/web/spring/web-default-configuration.properties"
    }
)
public class WebConfiguration {
}
