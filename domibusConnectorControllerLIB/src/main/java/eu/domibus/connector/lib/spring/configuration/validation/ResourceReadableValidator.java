/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration.validation;

import java.io.IOException;
import java.net.URL;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * This class is a validator for the {@link CheckResourceIsReadable} annotation. It checks whether a
 * given resource is readable.
 */
public class ResourceReadableValidator
    implements ConstraintValidator<CheckResourceIsReadable, String> {
    private final ApplicationContext ctx;

    public ResourceReadableValidator(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void initialize(CheckResourceIsReadable constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        try {
            var resource = ctx.getResource(value);

            if (!resource.exists()) {
                // context.buildConstraintViolationWithTemplate("eu.domibus.connector.lib.spring
                // .configuration.validation.resource_input_stream_valid")
                //  .addConstraintViolation();
                var message = String.format(
                    "Cannot open provided resource [%s]! Check if the path is correct and exists!",
                    value
                );
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            resource.getInputStream().close();
            // inputStream.close();
        } catch (IOException e) {
            var message = String.format(
                "Cannot open provided resource [%s]! Check if the path is correct and exists!",
                value
            );
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }

    private String getUrl(Resource value) {
        URL url = null;
        try {
            url = value.getURL();
            return url.toString();
        } catch (IOException e) {
            return value.toString();
        }
    }
}
