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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

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
