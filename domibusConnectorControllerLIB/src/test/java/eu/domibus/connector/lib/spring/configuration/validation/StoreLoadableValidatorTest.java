package eu.domibus.connector.lib.spring.configuration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ValidationTestContext.class)
class StoreLoadableValidatorTest {
    @Autowired
    private Validator validator;

    @Test
    void isValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("12345");
        storeConfigurationProperties.setPath("classpath:keystores/connector-backend.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);

        assertThat(validate).isEmpty();
    }

    @Test
    void isValid_wrongPassword_shouldNotBeValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("WRONG");
        storeConfigurationProperties.setPath("classpath:keystores/connector-backend.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);

        validate.forEach(c -> System.out.println(c.getMessage()));

        assertThat(validate).hasSize(3);
    }

    @Test
    void isValid_wrongPATH_shouldNotBeValid() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("12345");
        storeConfigurationProperties.setPath("classpath:keystores/NONEXISTANT_KEYSTORE.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate =
            validator.validate(storeConfigurationProperties);
        validate.forEach(c -> System.out.println(
            "propertyPath: " + c.getPropertyPath() + " msg: " + c.getMessage()));

        assertThat(validate).hasSize(2);
    }
}
