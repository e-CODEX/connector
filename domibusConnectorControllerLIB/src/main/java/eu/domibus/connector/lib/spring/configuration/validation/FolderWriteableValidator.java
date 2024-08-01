/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration.validation;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
