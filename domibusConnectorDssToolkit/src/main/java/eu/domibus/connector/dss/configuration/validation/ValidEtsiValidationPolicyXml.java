/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.dss.configuration.validation;

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
