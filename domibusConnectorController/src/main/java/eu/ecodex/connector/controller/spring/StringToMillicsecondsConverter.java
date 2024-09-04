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
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * This class provides a conversion service from String to DomibusConnectorDuration.
 */
@Component
@ConfigurationPropertiesBinding
public class StringToMillicsecondsConverter implements Converter<String, DomibusConnectorDuration> {
    @Override
    public DomibusConnectorDuration convert(String source) {
        return DomibusConnectorDuration.valueOf(source);
    }
}
