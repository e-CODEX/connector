package eu.domibus.connector.lib.spring.configuration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ValidationTestContext.class)
class ResourceReadableValidatorTest {
    @Autowired
    Validator validator;

    @Test
    void testResource() {
        String res = "testfile";
        TestEntity t = new TestEntity();
        t.setLocation(res);

        Set<ConstraintViolation<TestEntity>> validate = validator.validate(t);

        assertThat(validate).isEmpty();
    }

    @Test
    void testResourceIsNull_shouldNotValidate() {
        TestEntity t = new TestEntity();

        Set<ConstraintViolation<TestEntity>> validate = validator.validate(t);

        validate.forEach(
            a -> System.out.println(a.getMessage())
        );
        assertThat(validate).hasSize(1);
    }

    @Test
    void testResourceConfiguredPathDoesNotExist_shouldNotValidate() {
        String res = "/dsafdsadffds";
        TestEntity t = new TestEntity();
        t.setLocation(res);

        Set<ConstraintViolation<TestEntity>> validate = validator.validate(t);

        validate.forEach(
            a -> System.out.println(a.getMessage())
        );
        assertThat(validate).hasSize(1);
    }

    @Setter
    @Getter
    public static class TestEntity {
        @CheckResourceIsReadable
        String location;
    }
}
