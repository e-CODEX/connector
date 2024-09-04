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

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * The FolderWriteableValidatorTest class is a test class for the FolderWriteableValidator class. It
 * validates the behavior of the validator when checking if a folder is writable.
 */
@Disabled("See why these tests are disabled")
public class FolderWriteableValidatorTest {
    private static Validator validator;

    @BeforeAll
    public static void beforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDirectoryExists() {
        FilePathTestClass pathTestClass =
            new FilePathTestClass(Paths.get("./" + UUID.randomUUID()));
        Set<ConstraintViolation<FilePathTestClass>> validate = validator.validate(pathTestClass);

        assertThat(validate).hasSize(2);
    }

    @Data
    private static class FilePathTestClass {
        @CheckFolderWriteable
        private Path filePath;

        public FilePathTestClass(Path f) {
            this.filePath = f;
        }
    }
}
