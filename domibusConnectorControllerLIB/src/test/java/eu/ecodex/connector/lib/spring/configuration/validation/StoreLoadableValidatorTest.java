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

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.lib.spring.configuration.StoreConfigurationProperties;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ValidationTestContext.class)
class StoreLoadableValidatorTest {
    @Autowired
    private Validator validator;

    @Test
    void isValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("12345");
        storeConfigurationProperties.setPath("classpath:keystores/connector-backend.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);

        assertThat(validate).isEmpty();
    }

    @Test
    void isValid_wrongPassword_shouldNotBeValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("WRONG");
        storeConfigurationProperties.setPath("classpath:keystores/connector-backend.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);

        validate.forEach(c -> System.out.println(c.getMessage()));

        assertThat(validate).hasSize(3);
    }

    @Test
    void isValid_wrongPATH_shouldNotBeValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("12345");
        storeConfigurationProperties.setPath("classpath:keystores/NONEXISTANT_KEYSTORE.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);
        validate.forEach(c -> System.out.println(
            "propertyPath: " + c.getPropertyPath() + " msg: " + c.getMessage()));

        assertThat(validate).hasSize(2);
    }
}
