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

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The FolderWriteableValidator class is a validator for the CheckFolderWriteable annotation. It
 * validates if a folder is writable by checking if the provided path exists, is a directory, and is
 * writable.
 */
public class FolderWriteableValidator implements ConstraintValidator<CheckFolderWriteable, Path> {
    @Override
    public boolean isValid(Path file, ConstraintValidatorContext context) {
        if (file == null) {
            return true;
        }

        if (Files.notExists(file)) {
            var message = String.format(
                "Provided file path [%s] does not exist! Check if the path is correct and exists!",
                file
            );
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        if (!Files.isDirectory(file)) {
            var message =
                String.format("Provided file path [%s] is not a directory! Check the path!", file);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        if (!Files.isWritable(file)) {
            var message = String.format("Cannot write to provided path [%s]!", file);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
