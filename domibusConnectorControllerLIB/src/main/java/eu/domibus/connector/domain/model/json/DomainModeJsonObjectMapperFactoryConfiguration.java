/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.domibus.connector.common.annotations.DomainModelJsonObjectMapper;
import eu.domibus.connector.domain.model.LargeFileReference;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Configuration class for creating an ObjectMapper bean with specific configurations and modules.
 */
@AutoConfiguration
public class DomainModeJsonObjectMapperFactoryConfiguration {
    /**
     * Returns an ObjectMapper bean for domain model to JSON object mapping.
     *
     * <p>This method retrieves the ObjectMapper bean configured with specific settings and modules
     * for mapping domain model objects to JSON objects. The bean is retrieved from the Spring IoC
     * container using the @Bean annotation and the bean name specified in the
     * DomainModelJsonObjectMapper annotation.
     *
     * <p>The returned ObjectMapper is configured with the following settings: -
     * DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES set to false, so that unknown properties in
     * the JSON object are ignored during deserialization.
     *
     * <p>The returned ObjectMapper is also registered with the following modules: -
     * LargeFileReferenceModule, which is a custom module for serializing and deserializing
     * LargeFileReference objects. - JavaTimeModule, which is a module for serializing and
     * deserializing Java 8 Date/Time API types.
     *
     * @return The ObjectMapper bean for domain model to JSON object mapping.
     */
    @Bean(name = DomainModelJsonObjectMapper.VALUE)
    public ObjectMapper getMapper() {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var largeFileReferenceModule = new SimpleModule();
        largeFileReferenceModule.addSerializer(
            LargeFileReference.class, new LargeFileReferenceSerializer(LargeFileReference.class));
        largeFileReferenceModule.addDeserializer(
            LargeFileReference.class, new LargeFileDeserializer(LargeFileReference.class));

        mapper.registerModule(largeFileReferenceModule);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
