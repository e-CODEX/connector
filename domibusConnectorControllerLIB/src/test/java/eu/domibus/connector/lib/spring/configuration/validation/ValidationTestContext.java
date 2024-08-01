package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.common.service.DCKeyStoreService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * The ValidationTestContext class is annotated with @SpringBootApplication and @Import. It serves
 * as the application context for validating key store properties.
 *
 * <p>It depends on the DCKeyStoreService class, which is responsible for loading and storing
 * keystores centrally.
 * The DCKeyStoreService class provides methods for loading and retrieving keystores, as well as
 * validating the existence of keys and certificates.
 *
 * @see DCKeyStoreService
 */
@SpringBootApplication
@Import(DCKeyStoreService.class)
public class ValidationTestContext {
}
