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
