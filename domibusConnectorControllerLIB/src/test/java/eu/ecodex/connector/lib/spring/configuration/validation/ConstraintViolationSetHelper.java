/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.lib.spring.configuration.validation;

import eu.ecodex.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.ecodex.connector.lib.spring.configuration.StoreConfigurationProperties;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import lombok.experimental.UtilityClass;

/**
 * The ConstraintViolationSetHelper class provides utility methods for working with sets of
 * ConstraintViolation objects.
 */
@UtilityClass
public class ConstraintViolationSetHelper {
    public static <T> void printSet(Set<ConstraintViolation<T>> constraintViolationSet) {
        constraintViolationSet.forEach(c -> System.out.println(
            "propertyPath: " + c.getPropertyPath() + " msg: " + c.getMessage()));
    }

    /**
     * Generates a test StoreConfigurationProperties object for testing purposes.
     *
     * @return a StoreConfigurationProperties object with predefined values
     */
    @SuppressWarnings("squid:S1135")
    public static StoreConfigurationProperties generateTestStore() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();
        storeConfigurationProperties.setPath("classpath:keystores/client-bob.jks");
        storeConfigurationProperties.setPassword("12345");
        return storeConfigurationProperties;
    }

    /**
     * Generates a test KeyConfigurationProperties object for testing purposes.
     *
     * @return a KeyConfigurationProperties object with predefined values
     */
    public static KeyConfigurationProperties generateTestKeyConfig() {
        KeyConfigurationProperties keyConfigurationProperties = new KeyConfigurationProperties();
        keyConfigurationProperties.setAlias("bob");
        keyConfigurationProperties.setPassword("12345");
        return keyConfigurationProperties;
    }
}
