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

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.net.URL;
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
                // context.buildConstraintViolationWithTemplate("eu.ecodex.connector.lib.spring
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
