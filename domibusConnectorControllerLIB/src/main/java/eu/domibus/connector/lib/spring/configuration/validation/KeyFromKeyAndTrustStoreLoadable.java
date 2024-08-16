/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * The KeyFromKeyAndTrustStoreLoadable class is a constraint validator that checks if a key can be
 * loaded from the configured key store and trust store. It implements the ConstraintValidator
 * interface with the CheckKeyIsLoadableFromKeyStore annotation.
 */
public class KeyFromKeyAndTrustStoreLoadable implements
    ConstraintValidator<CheckKeyIsLoadableFromKeyStore,
        KeyAndKeyStoreAndTrustStoreConfigurationProperties> {
    private final Validator validator;
    private final HelperMethods helperMethods;

    public KeyFromKeyAndTrustStoreLoadable(Validator validator, HelperMethods helperMethods) {
        this.validator = validator;
        this.helperMethods = helperMethods;
    }

    @Override
    public boolean isValid(
        KeyAndKeyStoreAndTrustStoreConfigurationProperties value,
        ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Set<ConstraintViolation<KeyAndKeyStoreAndTrustStoreConfigurationProperties>>
            constraintViolations;
        constraintViolations = validator.validateProperty(value, "privateKey");
        constraintViolations.addAll(validator.validateProperty(value, "keyStore"));

        if (!constraintViolations.isEmpty()) {
            return false;
        }
        // context.disableDefaultConstraintViolation();

        return helperMethods.checkKeyIsLoadable(
            context, value.getKeyStore(), value.getPrivateKey());
    }
}
