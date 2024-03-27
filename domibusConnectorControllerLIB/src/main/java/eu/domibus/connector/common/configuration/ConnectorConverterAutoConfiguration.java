package eu.domibus.connector.common.configuration;

import eu.domibus.connector.common.annotations.ConnectorPropertyConverter;
import eu.domibus.connector.common.converters.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
