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

import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger instance for logging.
 */
public class StoreLoadableValidator
    implements ConstraintValidator<CheckStoreIsLoadable, StoreConfigurationProperties> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreLoadableValidator.class);
    private final Validator validator;
    private final DCKeyStoreService dcKeyStoreService;

    public StoreLoadableValidator(Validator validator, DCKeyStoreService dcKeyStoreService) {
        this.validator = validator;
        this.dcKeyStoreService = dcKeyStoreService;
    }

    @Override
    public void initialize(CheckStoreIsLoadable constraintAnnotation) {
        //        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        //        validator = factory.getValidator();
    }

    @SuppressWarnings("squid:S1135")
    @Override
    public boolean isValid(StoreConfigurationProperties value, ConstraintValidatorContext context) {
        try {
            if (value == null) {
                return true;
            }
            Set<ConstraintViolation<StoreConfigurationProperties>> pathValidation =
                validator.validateProperty(value, "path");
            if (!pathValidation.isEmpty()) {
                return false;
            }
            try {
                // value.loadKeyStore();
                dcKeyStoreService.loadKeyStore(value);
            } catch (DCKeyStoreService.CannotLoadKeyStoreException exception) {
                // TODO: nice message add property path...
                LOGGER.warn("error while loading store", exception);
                Exception ecx = exception;
                while (ecx.getCause() != null && ecx.getCause() instanceof Exception) {
                    ecx = (Exception) ecx.getCause();
                    context.buildConstraintViolationWithTemplate(ecx.getMessage())
                           .addConstraintViolation();
                }
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("exception occured while checking CheckStore is loadable constraint", e);
            // throw new RuntimeException(e);
            context.buildConstraintViolationWithTemplate(e.getCause().getMessage())
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
