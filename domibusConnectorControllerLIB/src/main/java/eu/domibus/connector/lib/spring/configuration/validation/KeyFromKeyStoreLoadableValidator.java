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

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * The KeyFromKeyStoreLoadableValidator class is a constraint validator used to validate if a key
 * can be loaded from the configured key store.
 */
public class KeyFromKeyStoreLoadableValidator implements
    ConstraintValidator<CheckKeyIsLoadableFromKeyStore, KeyAndKeyStoreConfigurationProperties> {
    private final Validator validator;
    private final HelperMethods helperMethods;

    public KeyFromKeyStoreLoadableValidator(Validator validator, HelperMethods helperMethods) {
        this.validator = validator;
        this.helperMethods = helperMethods;
    }

    @Override
    public void initialize(CheckKeyIsLoadableFromKeyStore constraintAnnotation) {
        // ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        // validator = factory.getValidator();
    }

    @Override
    public boolean isValid(
        KeyAndKeyStoreConfigurationProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> path = new HashSet<>();
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> path1 =
            validator.validateProperty(value, "privateKey");
        // path.addAll(validator.validateProperty(value, "keyStore"));
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> path2 =
            validator.validateProperty(value, "keyStore");
        path.addAll(path1);
        path.addAll(path2);
        if (!path.isEmpty()) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        return helperMethods.checkKeyIsLoadable(
            context, value.getKeyStore(), value.getPrivateKey());
    }
}
