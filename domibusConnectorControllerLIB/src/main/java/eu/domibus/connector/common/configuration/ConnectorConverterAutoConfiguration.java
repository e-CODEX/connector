/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.common.configuration;

import eu.domibus.connector.common.annotations.ConnectorPropertyConverter;
import eu.domibus.connector.common.converters.BusinessDomainIdConverter;
import eu.domibus.connector.common.converters.ClassToStringConverter;
import eu.domibus.connector.common.converters.ClasspathResourceToStringConverter;
import eu.domibus.connector.common.converters.EvidenceActionConverter;
import eu.domibus.connector.common.converters.FileResourceToStringConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration class that provides bean definitions for the connectors converters.
 */
@Configuration
public class ConnectorConverterAutoConfiguration {
    @Bean
    @ConnectorPropertyConverter
    public EvidenceActionConverter stringToEvidenceActionConverter() {
        return new EvidenceActionConverter();
    }

    @Bean
    @ConnectorPropertyConverter
    public BusinessDomainIdConverter stringToBusinessDomainId() {
        return new BusinessDomainIdConverter();
    }

    @Bean
    @ConnectorPropertyConverter
    public ClasspathResourceToStringConverter classpathResourceToStringConverter() {
        return new ClasspathResourceToStringConverter();
    }

    @Bean
    @ConnectorPropertyConverter
    public FileResourceToStringConverter fileResourceToStringConverter() {
        return new FileResourceToStringConverter();
    }

    @Bean
    @ConnectorPropertyConverter
    public ClassToStringConverter classToStringConverter() {
        return new ClassToStringConverter();
    }
}
