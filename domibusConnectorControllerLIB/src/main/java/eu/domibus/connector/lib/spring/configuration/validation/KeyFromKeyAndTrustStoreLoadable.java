/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import java.util.Set;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

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
