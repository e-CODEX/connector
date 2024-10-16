/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.common.service;

import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;

/**
 * This service loads with @ConfigurationProperties annotated classes and also binds the
 * configuration.
 */
public interface ConfigurationPropertyManagerService {
    /**
     * determines the prefix from the clazz which must be annotated with @see
     * {@link org.springframework.boot.context.properties.ConfigurationProperties}
     *
     * <p>then
     * {@link #loadConfiguration(DomibusConnectorBusinessDomain.BusinessDomainId, Class, String)} is
     * called.
     */
    <T> T loadConfiguration(
        @Nullable DomibusConnectorBusinessDomain.BusinessDomainId laneId,
        @NotNull Class<T> clazz);

    /**
     * Initializes the clazz from the property source the properties are taken from the message
     * lane, if not provided the default application environment is used.
     *
     * @param laneId - the lane id
     * @param clazz  - the clazz to init
     * @param prefix - the prefix for the properties
     * @param <T>    - type of the clazz
     * @return the initialized class
     */
    <T> T loadConfiguration(
        @Nullable DomibusConnectorBusinessDomain.BusinessDomainId laneId,
        @NotNull Class<T> clazz, String prefix);

    <T> T loadConfigurationOnlyFromMap(Map<String, String> map, Class<T> clazz, String prefix);

    <T> Set<ConstraintViolation<T>> validateConfiguration(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId, T updatedConfigClazz);

    /**
     * Updates the configuration for a specific business domain and configuration bean. If the
     * laneId is null, the default business domain is used.
     *
     * @param laneId            the business domain id to update the configuration for. If null
     *                          defaultLaneId is used.
     * @param configurationBean the configuration bean to update
     */
    void updateConfiguration(
        @Nullable DomibusConnectorBusinessDomain.BusinessDomainId laneId,
        Object configurationBean);

    void updateConfiguration(
        DomibusConnectorBusinessDomain.BusinessDomainId laneId,
        Class<?> updatedConfigClazz, Map<String, String> diffProps);

    Map<String, String> getUpdatedConfiguration(
        DomibusConnectorBusinessDomain.BusinessDomainId defaultMessageLaneId,
        Object boundConfigValue);
}
