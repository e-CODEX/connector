package eu.domibus.connector.security.configuration.validation;

import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;

public class CheckAllowedAdvancedElectronicSystemTypeValidator implements ConstraintValidator<CheckAllowedAdvancedElectronicSystemType, DCBusinessDocumentValidationConfigurationProperties> {

    @Override
    public boolean isValid(DCBusinessDocumentValidationConfigurationProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean valid = true;
        context.disableDefaultConstraintViolation();
        Set<AdvancedElectronicSystemType> allowedAdvancedSystemTypes = value.getAllowedAdvancedSystemTypes();
        AdvancedElectronicSystemType defaultAesSystem = value.getDefaultAdvancedSystemType();
        if (allowedAdvancedSystemTypes.contains(AdvancedElectronicSystemType.SIGNATURE_BASED) && value.getSignatureValidation() == null) {
            context.buildConstraintViolationWithTemplate("AllowedAdvancedSystemTypes contains SIGNATURE_BASED so signature-validation must be configured")
                    .addPropertyNode("signature-validation")
                    .addConstraintViolation();
            valid = false;
        }
        if (allowedAdvancedSystemTypes.contains(AdvancedElectronicSystemType.AUTHENTICATION_BASED) && value.getAuthenticationValidation() == null) {
            context.buildConstraintViolationWithTemplate("AllowedAdvancedSystemTypes contains AUTHENTICATION_BASED so authentication-validation must be configured")
                    .addPropertyNode("authentication-validation")
                    .addConstraintViolation();
            valid = false;
        }
        if (defaultAesSystem == null) {
            context.buildConstraintViolationWithTemplate("The DefaultAdvancedSystemType must be set to one of the AllowedAdvancedSystemTypes, but it is not set at all!")
                    .addPropertyNode("defaultAdvancedSystemType")
                    .addConstraintViolation();
            valid = false;
        }
        if (!allowedAdvancedSystemTypes.contains(defaultAesSystem)) {
            context.buildConstraintViolationWithTemplate("The DefaultAdvancedSystemType must be set to one of the AllowedAdvancedSystemTypes!")
                    .addPropertyNode("defaultAdvancedSystemType")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }

}
