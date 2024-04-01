package eu.domibus.connector.security.container.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import test.context.SecurityToolkitTestContext;


//@TestPropertySource({"classpath:test.properties", "classpath:test-auth.properties"})
@SpringBootTest(classes = SecurityToolkitTestContext.class)
@ActiveProfiles({"test", "test-auth", "seclib-test"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ECodexContainerFactoryService_AuthenticationBased_Test extends ECodexContainerFactoryServiceITCaseTemplate {
}
