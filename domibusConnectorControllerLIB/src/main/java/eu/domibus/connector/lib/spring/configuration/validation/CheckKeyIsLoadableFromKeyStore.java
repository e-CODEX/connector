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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The CheckKeyIsLoadableFromKeyStore annotation is used to validate if a key can be loaded from the
 * configured key store. It is annotated with constraints that define the validation rules for the
 * key and key store configuration properties.
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(
    validatedBy = {KeyFromKeyStoreLoadableValidator.class, KeyFromKeyAndTrustStoreLoadable.class}
)
@Documented
@NotNull
public @interface CheckKeyIsLoadableFromKeyStore {
    /**
     * Retrieves the error message for when a key cannot be loaded from the configured key store.
     *
     * @return the error message
     */
    String message() default "Cannot load key from configured key store!";

    /**
     * Returns an array of classes that represent the constraint groups targeted for validation.
     *
     * @return an array of classes representing the constraint groups
     */
    Class<?>[] groups() default {};

    /**
     * Returns an array of classes that represent the payload to be associated with a validation
     * constraint. The payload is an optional element of a validation constraint and can be used to
     * provide additional information or metadata about the constraint. The payload can be accessed
     * at runtime by the validation framework.
     *
     * @return an array of classes representing the payload
     */
    Class<? extends Payload>[] payload() default {};
}
