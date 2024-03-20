package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ValidationTestContext.class)
public class StoreLoadableValidatorTest {

    @Autowired
    private Validator validator;

//    @BeforeAll
//    public static void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }

    @Test
    public void isValid() throws Exception {
        StoreConfigurationProperties storeConfigurationProperties = new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("12345");
//        storeConfigurationProperties.setPath(new ClassPathResource("keystores/connector-backend.jks"));
        storeConfigurationProperties.setPath("classpath:keystores/connector-backend.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate = validator.validate(storeConfigurationProperties);

        assertThat(validate).hasSize(0);
    }


    @Test
    public void isValid_wrongPassword_shouldNotBeValid() {
        StoreConfigurationProperties storeConfigurationProperties = new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("WRONG");
//        storeConfigurationProperties.setPath(new ClassPathResource("keystores/connector-backend.jks"));
        storeConfigurationProperties.setPath("classpath:keystores/connector-backend.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate = validator.validate(storeConfigurationProperties);

        validate.stream().forEach(c -> System.out.println(c.getMessage()));

        assertThat(validate).hasSize(3);
    }

    @Test
    public void isValid_wrongPATH_shouldNotBeValid() {
        StoreConfigurationProperties storeConfigurationProperties = new StoreConfigurationProperties();

        storeConfigurationProperties.setPassword("12345");
//        storeConfigurationProperties.setPath(new ClassPathResource("keystores/NONEXISTANT_KEYSTORE.jks"));
        storeConfigurationProperties.setPath("classpath:keystores/NONEXISTANT_KEYSTORE.jks");

        Set<ConstraintViolation<StoreConfigurationProperties>> validate = validator.validate(storeConfigurationProperties);
        validate.stream().forEach(c -> System.out.println("propertyPath: " + c.getPropertyPath() + " msg: " + c.getMessage()));

        assertThat(validate).hasSize(2);
    }


}