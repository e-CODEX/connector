package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;

import javax.validation.*;
import java.util.Set;

public class KeyFromKeyAndTrustStoreLoadable implements ConstraintValidator<CheckKeyIsLoadableFromKeyStore, KeyAndKeyStoreAndTrustStoreConfigurationProperties> {

    private final Validator validator;
    private final HelperMethods helperMethods;

    public KeyFromKeyAndTrustStoreLoadable(Validator validator, HelperMethods helperMethods) {
        this.validator = validator;
        this.helperMethods = helperMethods;
    }

    @Override
    public boolean isValid(KeyAndKeyStoreAndTrustStoreConfigurationProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Set<ConstraintViolation<KeyAndKeyStoreAndTrustStoreConfigurationProperties>> constraintViolations;
        constraintViolations = validator.validateProperty(value, "privateKey");
        constraintViolations.addAll(validator.validateProperty(value, "keyStore"));


        if (!constraintViolations.isEmpty()) {
            return false;
        }

//        context.disableDefaultConstraintViolation();

        return helperMethods.checkKeyIsLoadable(context, value.getKeyStore(), value.getPrivateKey());


    }
}
