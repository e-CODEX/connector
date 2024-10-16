/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.ecodex.connector.common.annotations.DomainModelJsonObjectMapper;
import eu.ecodex.connector.domain.model.LargeFileReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for creating an ObjectMapper bean with specific configurations and modules.
 */
@Configuration
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
