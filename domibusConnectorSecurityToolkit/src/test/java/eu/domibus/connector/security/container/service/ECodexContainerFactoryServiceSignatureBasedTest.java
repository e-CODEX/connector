package eu.domibus.connector.security.container.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import test.context.SecurityToolkitTestContext;

/**
 * This class is a test class that extends the {@link ECodexContainerFactoryServiceITCaseTemplate}
 * class.
 */
@SpringBootTest(classes = SecurityToolkitTestContext.class)
@ActiveProfiles({"test", "test-sig", "seclib-test"})
public class ECodexContainerFactoryServiceSignatureBasedTest
    extends ECodexContainerFactoryServiceITCaseTemplate {
}
