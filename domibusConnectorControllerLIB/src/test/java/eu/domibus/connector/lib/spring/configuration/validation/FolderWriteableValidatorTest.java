package eu.domibus.connector.lib.spring.configuration.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class FolderWriteableValidatorTest {


    private static Validator validator;

    @BeforeAll
    public static void beforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    public void testDirectoryExists() {
        FilePathTestClass pathTestClass = new FilePathTestClass(Paths.get("./" + UUID.randomUUID()));
        Set<ConstraintViolation<FilePathTestClass>> validate = validator.validate(pathTestClass);

        assertThat(validate).hasSize(2);
    }

    private static class FilePathTestClass {

        @CheckFolderWriteable
        private Path filePath;

        public FilePathTestClass(Path f) {
            this.filePath = f;
        }

        public Path getFilePath() {
            return filePath;
        }

        public void setFilePath(Path filePath) {
            this.filePath = filePath;
        }
    }


}