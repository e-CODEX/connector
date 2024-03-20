package eu.domibus.connector.lib.spring.configuration.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FolderWriteableValidator implements ConstraintValidator<CheckFolderWriteable, Path> {

    @Override
    public boolean isValid(Path file, ConstraintValidatorContext context) {
        if (file == null) {
            return true;
        }

        if (Files.notExists(file)) {
            String message = String.format("Provided file path [%s] does not exist! Check if the path is correct and exists!", file);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        if (!Files.isDirectory(file)) {
            String message = String.format("Provided file path [%s] is not a directory! Check the path!", file);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        if (!Files.isWritable(file)) {
            String message = String.format("Cannot write to provided path [%s]!", file);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
