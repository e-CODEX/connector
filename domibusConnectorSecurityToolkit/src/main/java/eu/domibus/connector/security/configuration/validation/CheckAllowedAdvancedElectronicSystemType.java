/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.security.configuration.validation;

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
 * The CheckAllowedAdvancedElectronicSystemType annotation is used to specify a constraint on a
 * field, method parameter, or method return type. The annotation is used in conjunction with the
 * CheckAllowedAdvancedElectronicSystemTypeValidator class to validate that the specified value is
 * allowed based on a set of advanced electronic system types. The annotation can be applied to a
 * class, annotation type, field, method, or parameter, and is retained at runtime. It is also
 * annotated with @NotNull, indicating that the annotated element must not be null.
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {CheckAllowedAdvancedElectronicSystemTypeValidator.class})
@Documented
@NotNull
public @interface CheckAllowedAdvancedElectronicSystemType {
    /**
     * Returns the message of the annotation.
     *
     * @return the message of the annotation
     */
    String message() default "";

    /**
     * Retrieves the groups that are associated with this annotation.
     *
     * @return an array of Class objects representing the groups associated with this annotation
     */
    Class<?>[] groups() default {};

    /**
     * Returns the payload for the method.
     *
     * @return the payload for the method
     */
    Class<? extends Payload>[] payload() default {};
}
