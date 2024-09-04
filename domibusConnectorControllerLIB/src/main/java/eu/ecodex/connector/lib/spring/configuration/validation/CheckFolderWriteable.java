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
 * The {@code CheckFolderWriteable} annotation is used to validate if a folder is writable. It can
 * be applied to fields, parameters, or methods in classes that contain a {@code Path} object
 * representing a folder.
 */
@Target({ANNOTATION_TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = FolderWriteableValidator.class)
@Documented
@NotNull
public @interface CheckFolderWriteable {
    /**
     * Returns the default error message when a folder is not writable.
     *
     * @return the default error message
     */
    String message() default "Cannot write to folder!";

    /**
     * The {@code groups} method is used to return the groups that the validation constraint belongs
     * to. These groups can be used to specify which constraints should be applied during
     * validation.
     *
     * @return an array of {@code Class<?>} objects representing the groups
     */
    Class<?>[] groups() default {};

    /**
     * The {@code payload} method is used to specify the payload to be attached to the validation
     * constraint. The payload is used to provide additional contextual information during the
     * validation process.
     *
     * @return an array of {@code Class<? extends Payload>} objects representing the payload
     */
    Class<? extends Payload>[] payload() default {};
}
