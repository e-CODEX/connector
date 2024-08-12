package eu.domibus.connector.security.container.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import test.context.SecurityToolkitTestContext;

/**
 * This class is a test class that tests the authentication-based functionality of the
 * ECodexContainerFactoryService class. It is a subclass of the
 * ECodexContainerFactoryServiceITCaseTemplate class, and is used to perform integration testing.
 */
@SpringBootTest(classes = SecurityToolkitTestContext.class)
@ActiveProfiles(value = {"test", "test-auth", "seclib-test"})
public class ECodexContainerFactoryServiceAuthenticationBasedTest
    extends ECodexContainerFactoryServiceITCaseTemplate {
}
