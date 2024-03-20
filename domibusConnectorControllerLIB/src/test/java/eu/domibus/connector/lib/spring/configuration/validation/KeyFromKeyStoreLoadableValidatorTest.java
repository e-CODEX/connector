package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static eu.domibus.connector.lib.spring.configuration.validation.ConstraintViolationSetHelper.printSet;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ValidationTestContext.class)
public class KeyFromKeyStoreLoadableValidatorTest {

    @Autowired
    private  Validator validator;

    private KeyAndKeyStoreConfigurationProperties props;

    @BeforeEach
    public void setUp() {
        props = new KeyAndKeyStoreConfigurationProperties();
        props.setKeyStore(ConstraintViolationSetHelper.generateTestStore());
        props.setPrivateKey(ConstraintViolationSetHelper.generateTestKeyConfig());
    }


    @Test
    public void isValid() throws Exception {
        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> validate = validator.validate(props);
        printSet(validate);
        assertThat(validate).hasSize(0);
    }

    @Test
    public void isValid_wrongStorePath_shouldNotValidate() {
//        props.getKeyStore().setPath(new ClassPathResource("/does/not/exist"));
        props.getKeyStore().setPath("classpath:/does/not/exist");

        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> validate = validator.validate(props);
        printSet(validate);
        assertThat(validate).hasSize(3);
    }


    @Test
    public void isValid_wrongKeyAlias_shouldNotValidate() {
        props.getPrivateKey().setAlias("WRONG_ALIAS");

        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> validate = validator.validate(props);
        printSet(validate);
        assertThat(validate).hasSize(1);
    }

    @Test
    public void isValid_wrongKeyPassword_shouldNotValidate() {
        props.getPrivateKey().setPassword("WRONG_PASSWORD");

        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> validate = validator.validate(props);
        printSet(validate);
        assertThat(validate).hasSize(1);
    }


    @Test
    public void isValid_keyInformationIsNull() {
        props.setPrivateKey(null);

        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> validate = validator.validate(props);
        printSet(validate);
        assertThat(validate).hasSize(2);
    }

    @Test
    public void isValid_keyStoreInformationIsNull() {
        props.setKeyStore(null);

        Set<ConstraintViolation<KeyAndKeyStoreConfigurationProperties>> validate = validator.validate(props);
        printSet(validate);
        assertThat(validate).hasSize(2);
    }



}