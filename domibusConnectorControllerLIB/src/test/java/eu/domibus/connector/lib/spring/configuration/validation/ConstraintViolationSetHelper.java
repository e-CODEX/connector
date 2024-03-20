package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import org.springframework.core.io.ClassPathResource;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ConstraintViolationSetHelper {

    public static <T> void printSet(Set<ConstraintViolation<T>> constraintViolationSet) {
        constraintViolationSet.stream().forEach(c -> System.out.println("propertyPath: " + c.getPropertyPath() + " msg: " + c.getMessage()));
    }


    public static StoreConfigurationProperties generateTestStore() {
        StoreConfigurationProperties storeConfigurationProperties = new StoreConfigurationProperties();
//        storeConfigurationProperties.setPath(new ClassPathResource("keystores/client-bob.jks"));
        storeConfigurationProperties.setPath("classpath:keystores/client-bob.jks");
        storeConfigurationProperties.setPassword("12345");
        return storeConfigurationProperties;
    }

    public static KeyConfigurationProperties generateTestKeyConfig() {
        KeyConfigurationProperties keyConfigurationProperties = new KeyConfigurationProperties();
        keyConfigurationProperties.setAlias("bob");
        keyConfigurationProperties.setPassword("12345");
        return keyConfigurationProperties;
    }
}
