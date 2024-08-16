/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.common.spring;

import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;

public class RegisterBusinessDomainPropertySource implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger LOGGER = LogManager.getLogger(RegisterBusinessDomainPropertySource.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LOGGER.debug(LoggingMarker.Log4jMarker.CONFIG, "Registering business scoped property source as first property source");
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        propertySources.addFirst(new BusinessScopedPropertySource(applicationContext));
    }
}
