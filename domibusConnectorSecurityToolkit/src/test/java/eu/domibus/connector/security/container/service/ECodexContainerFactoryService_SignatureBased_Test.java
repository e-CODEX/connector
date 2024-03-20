package eu.domibus.connector.security.container.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.service.DCBusinessDomainPersistenceService;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import eu.domibus.connector.persistence.service.testutil.LargeFilePersistenceServicePassthroughImpl;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import test.context.SecurityToolkitTestContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;


//@TestPropertySource({"classpath:test.properties", "classpath:test-sig.properties"})
@SpringBootTest(classes = SecurityToolkitTestContext.class)
@ActiveProfiles({"test", "test-sig", "seclib-test"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ECodexContainerFactoryService_SignatureBased_Test extends ECodexContainerFactoryServiceITCaseTemplate {

}