/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

/**
 * The CheckStoreIsLoadable annotation is used to validate if a StoreConfigurationProperties object
 * can be loaded as a key store. It is a custom annotation that is applied to a field, method,
 * parameter, or type.
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = StoreLoadableValidator.class)
@Documented
@NotNull
public @interface CheckStoreIsLoadable {
    /**
     * Retrieves the message associated with the validation error if the key store cannot be
     * loaded.
     *
     * @return the message associated with the validation error
     */
    String message() default "{eu.domibus.connector.lib.spring.configuration"
        + ".validation.cannot_load_key_store}";

    /**
     * Returns an array of classes that represents the groups targeted for validation.
     *
     * @return an array of classes representing the groups targeted for validation
     */
    Class<?>[] groups() default {};

    /**
     * Returns an array of classes that represents the payload to be included in the constraint
     * violation message.
     *
     * @return an array of classes representing the payload to be included in the constraint
     *      violation message
     */
    Class<? extends Payload>[] payload() default {};
}
