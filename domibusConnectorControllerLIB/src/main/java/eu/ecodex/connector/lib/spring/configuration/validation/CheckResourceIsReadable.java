/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.lib.spring.configuration.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is used to validate whether a resource is readable. It is applied to a field or a
 * method parameter of type String that represents the location of the resource. The validation is
 * performed by the ResourceReadableValidator class.
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ResourceReadableValidator.class)
@Documented
@NotNull
public @interface CheckResourceIsReadable {
    /**
     * Returns the message to be displayed when the resource input stream is not valid.
     *
     * <p>The message is defined in the {@link CheckResourceIsReadable} annotation.
     *
     * @return the message to be displayed
     */
    String message() default "{eu.ecodex.connector.lib.spring.configuration.validation."
        + "resource_input_stream_valid}";

    /**
     * Returns the array of validation groups for the annotated resource. These groups are used to
     * specify the order in which validations should be performed.
     *
     * @return the array of validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Returns the array of payload classes for the annotated resource. These payload classes are
     * used to specify additional information for validation.
     *
     * @return the array of payload classes
     */
    Class<? extends Payload>[] payload() default {};
}
