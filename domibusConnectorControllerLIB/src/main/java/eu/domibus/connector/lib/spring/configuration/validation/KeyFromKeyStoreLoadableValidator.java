package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;

import javax.validation.*;
import java.util.HashSet;
import java.util.Set;

public class KeyFromKeyStoreLoadableValidator  implements ConstraintValidator<CheckKeyIsLoadableFromKeyStore, KeyAndKeyStoreConfigurationProperties> {

    private final Validator validator;
    private final HelperMethods helperMethods;

    public KeyFromKeyStoreLoadableValidator(Validator validator, HelperMethods helperMethods) {
        this.validator = validator;
        this.helperMethods = helperMethods;
    }

    @Override
    public void initialize(CheckKeyIsLoadableFromKeyStore constraintAnnotation) {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
    }

    @Override
    public boolean isValid(KeyAndKeyStoreConfigurationProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> path = new HashSet<>();
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> path1 = validator.validateProperty(value, "privateKey");
//        path.addAll(validator.validateProperty(value, "keyStore"));
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> path2 = validator.validateProperty(value, "keyStore");
        path.addAll(path1);
        path.addAll(path2);
        if (!path.isEmpty()) {
            return false;
        }

        context.disableDefaultConstraintViolation();


        return helperMethods.checkKeyIsLoadable(context, value.getKeyStore(), value.getPrivateKey());
    }





}
