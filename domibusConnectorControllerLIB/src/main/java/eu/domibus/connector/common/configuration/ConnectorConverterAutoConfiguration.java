/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.configuration;

import eu.domibus.connector.common.annotations.ConnectorPropertyConverter;
import eu.domibus.connector.common.converters.BusinessDomainIdConverter;
import eu.domibus.connector.common.converters.ClassToStringConverter;
import eu.domibus.connector.common.converters.ClasspathResourceToStringConverter;
import eu.domibus.connector.common.converters.EvidenceActionConverter;
import eu.domibus.connector.common.converters.FileResourceToStringConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * A configuration class that provides bean definitions for the connectors converters.
 */
@AutoConfiguration
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
