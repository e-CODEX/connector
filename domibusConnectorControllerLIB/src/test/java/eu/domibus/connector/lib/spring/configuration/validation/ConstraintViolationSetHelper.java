package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.util.Set;
import javax.validation.ConstraintViolation;
import lombok.experimental.UtilityClass;

/**
 * The ConstraintViolationSetHelper class provides utility methods for working with sets of
 * ConstraintViolation objects.
 */
@UtilityClass
public class ConstraintViolationSetHelper {
    public static <T> void printSet(Set<ConstraintViolation<T>> constraintViolationSet) {
        constraintViolationSet.forEach(c -> System.out.println(
            "propertyPath: " + c.getPropertyPath() + " msg: " + c.getMessage()));
    }

    /**
     * Generates a test StoreConfigurationProperties object for testing purposes.
     *
     * @return a StoreConfigurationProperties object with predefined values
     */
    @SuppressWarnings("squid:S1135")
    public static StoreConfigurationProperties generateTestStore() {
        StoreConfigurationProperties storeConfigurationProperties =
            new StoreConfigurationProperties();
        storeConfigurationProperties.setPath("classpath:keystores/client-bob.jks");
        storeConfigurationProperties.setPassword("12345");
        return storeConfigurationProperties;
    }

    /**
     * Generates a test KeyConfigurationProperties object for testing purposes.
     *
     * @return a KeyConfigurationProperties object with predefined values
     */
    public static KeyConfigurationProperties generateTestKeyConfig() {
        KeyConfigurationProperties keyConfigurationProperties = new KeyConfigurationProperties();
        keyConfigurationProperties.setAlias("bob");
        keyConfigurationProperties.setPassword("12345");
        return keyConfigurationProperties;
    }
}
