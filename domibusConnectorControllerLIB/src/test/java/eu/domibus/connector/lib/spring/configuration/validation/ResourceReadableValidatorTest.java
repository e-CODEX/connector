package eu.domibus.connector.lib.spring.configuration.validation;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ValidationTestContext.class)
public class ResourceReadableValidatorTest {


    @Autowired
    Validator validator;

    @Test
    public void testResource() {
        String res = "testfile";
        TestEntity t = new TestEntity();
        t.setLocation(res);

        Set<ConstraintViolation<TestEntity>> validate = validator.validate(t);

        assertThat(validate).isEmpty();
    }


    @Test
    public void testResourceIsNull_shouldNotValidate() {
        TestEntity t = new TestEntity();

        Set<ConstraintViolation<TestEntity>> validate = validator.validate(t);

        validate.stream().forEach(
                a -> System.out.println(a.getMessage())
        );
        assertThat(validate).hasSize(1);

    }

    @Test
    public void testResourceConfiguredPathDoesNotExist_shouldNotValidate() {
        String res = "/dsafdsadffds";
        TestEntity t = new TestEntity();
        t.setLocation(res);

        Set<ConstraintViolation<TestEntity>> validate = validator.validate(t);

        validate.stream().forEach(
                a -> System.out.println(a.getMessage())
        );
        assertThat(validate).hasSize(1);

    }

    public static class TestEntity {

        @CheckResourceIsReadable
        String location;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }




}