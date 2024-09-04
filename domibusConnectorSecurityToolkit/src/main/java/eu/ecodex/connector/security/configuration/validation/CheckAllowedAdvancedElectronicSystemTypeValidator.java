/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.security.configuration.validation;

import eu.ecodex.connector.domain.enums.AdvancedElectronicSystemType;
import eu.ecodex.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * The CheckAllowedAdvancedElectronicSystemTypeValidator class is a validator implementation for the
 * CheckAllowedAdvancedElectronicSystemType annotation. It validates whether the specified value is
 * allowed based on the allowed advanced electronic system types.
 *
 * <p>The class implements the ConstraintValidator interface and is used in conjunction with the
 * CheckAllowedAdvancedElectronicSystemType annotation to perform validation.
 *
 * <p>The isValid() method is overridden to perform the validation. It checks if the specified
 * value is null and returns true if it is null. Otherwise, it validates the value by checking if it
 * meets the following conditions: - The required signature validation is present if the allowed
 * advanced system types contain SIGNATURE_BASED. - The required authentication validation is
 * present if the allowed advanced system types contain AUTHENTICATION_BASED. - The default advanced
 * system type is set to one of the allowed advanced system types.
 *
 * <p>If any of the validation conditions fail, a constraint violation is created using
 * ConstraintValidatorContext and added to context. The isValid() method returns true if all the
 * validation conditions are met, indicating that the specified value is valid, and false
 * otherwise.
 */
public class CheckAllowedAdvancedElectronicSystemTypeValidator implements
    ConstraintValidator<
        CheckAllowedAdvancedElectronicSystemType,
        DCBusinessDocumentValidationConfigurationProperties> {
    @Override
    public boolean isValid(
        DCBusinessDocumentValidationConfigurationProperties value,
        ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        var valid = true;
        context.disableDefaultConstraintViolation();
        Set<AdvancedElectronicSystemType> allowedAdvancedSystemTypes =
            value.getAllowedAdvancedSystemTypes();
        AdvancedElectronicSystemType defaultAesSystem = value.getDefaultAdvancedSystemType();
        if (allowedAdvancedSystemTypes.contains(AdvancedElectronicSystemType.SIGNATURE_BASED)
            && value.getSignatureValidation() == null) {
            context.buildConstraintViolationWithTemplate(
                       "AllowedAdvancedSystemTypes contains SIGNATURE_BASED so "
                           + "signature-validation must be configured"
                   )
                   .addPropertyNode("signature-validation")
                   .addConstraintViolation();
            valid = false;
        }
        if (allowedAdvancedSystemTypes.contains(AdvancedElectronicSystemType.AUTHENTICATION_BASED)
            && value.getAuthenticationValidation() == null) {
            context.buildConstraintViolationWithTemplate(
                       "AllowedAdvancedSystemTypes contains AUTHENTICATION_BASED so "
                           + "authentication-validation must be configured"
                   )
                   .addPropertyNode("authentication-validation")
                   .addConstraintViolation();
            valid = false;
        }
        if (defaultAesSystem == null) {
            context.buildConstraintViolationWithTemplate(
                       "The DefaultAdvancedSystemType must be set to one of the "
                           + "AllowedAdvancedSystemTypes, but it is not set at all!"
                   )
                   .addPropertyNode("defaultAdvancedSystemType")
                   .addConstraintViolation();
            valid = false;
        }
        if (!allowedAdvancedSystemTypes.contains(defaultAesSystem)) {
            context.buildConstraintViolationWithTemplate(
                       "The DefaultAdvancedSystemType must be set to one of the "
                           + "AllowedAdvancedSystemTypes!")
                   .addPropertyNode("defaultAdvancedSystemType"
                   )
                   .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
