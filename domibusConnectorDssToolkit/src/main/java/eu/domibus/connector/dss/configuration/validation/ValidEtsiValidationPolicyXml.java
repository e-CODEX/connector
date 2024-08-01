/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.dss.configuration.validation;

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
 * The ValidEtsiValidationPolicyXml annotation is a validation constraint that checks whether a
 * given string represents a valid EtsiValidationPolicy XML. It is used in conjunction with the
 * ValidEtsiValidationPolicyXmlValidator class to perform the validation.
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidEtisValidationPolicyXmlValidator.class})
@Documented
@NotNull
public @interface ValidEtsiValidationPolicyXml {
    /**
     * Returns the message to be displayed when the provided xml is not a schema valid
     * EtsiValidationPolicy.
     *
     * @return the message indicating that the provided xml is not schema valid EtsiValidationPolicy
     */
    String message() default "The provided xml is not a schema valid EtsiValidationPolicy!";

    /**
     * Retrieves the groups associated with the ValidEtsiValidationPolicyXml annotation. These
     * groups are used for validation purposes.
     *
     * @return an array of the associated groups
     */
    Class<?>[] groups() default {};

    /**
     * Returns an array of payload groups targeted for validation.
     *
     * @return the payload groups for validation
     */
    Class<? extends Payload>[] payload() default {};
}
