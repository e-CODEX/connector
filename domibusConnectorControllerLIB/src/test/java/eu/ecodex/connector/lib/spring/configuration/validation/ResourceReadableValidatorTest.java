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
import jakarta.validation.Validator;
import java.util.Set;
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
