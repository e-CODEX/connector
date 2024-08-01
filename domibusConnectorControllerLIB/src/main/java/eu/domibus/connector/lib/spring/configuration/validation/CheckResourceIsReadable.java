/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

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
    String message() default "{eu.domibus.connector.lib.spring.configuration.validation."
        + "resource_input_stream_valid}";

    /**
     * Returns the array of validation groups for the annotated resource. These groups are used to
     * specify the order in which validations should be performed.
     *
     * @return the array of validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Returns the array of payload classes for the annotated resource. These payload
     * classes are used to specify additional information for validation.
     *
     * @return the array of payload classes
     */
    Class<? extends Payload>[] payload() default {};
}
