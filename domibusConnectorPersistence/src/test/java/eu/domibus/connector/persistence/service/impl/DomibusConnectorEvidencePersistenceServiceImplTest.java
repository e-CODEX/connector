package eu.domibus.connector.persistence.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.domibus.connector.persistence.dao.DomibusConnectorEvidenceDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import eu.domibus.connector.persistence.model.test.util.PersistenceEntityCreator;
import eu.domibus.connector.persistence.service.DomibusConnectorEvidencePersistenceService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@Disabled
class DomibusConnectorEvidencePersistenceServiceImplTest {
    @Mock
    DomibusConnectorEvidenceDao domibusConnectorEvidenceDao;
    @Mock
    DomibusConnectorMessageDao domibusConnectorMessageDao;
    DomibusConnectorEvidencePersistenceService evidencePersistenceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DomibusConnectorEvidencePersistenceServiceImpl impl =
            new DomibusConnectorEvidencePersistenceServiceImpl();
        impl.setEvidenceDao(domibusConnectorEvidenceDao);
        impl.setMessageDao(domibusConnectorMessageDao);
        evidencePersistenceService = impl;
    }

    @Test
    void testPersistEvidenceForMessageIntoDatabase_evidenceBytesAreNull() {
        eu.domibus.connector.domain.model.DomibusConnectorMessage message =
            DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();

        PDomibusConnectorMessage dbMessage =
            PersistenceEntityCreator.createSimpleDomibusConnectorMessage();

        byte[] evidence = null;

        Mockito.when(this.domibusConnectorMessageDao.findOneByConnectorMessageId(eq("msgid")))
               .thenReturn(Optional.of(dbMessage));

        Mockito.when(this.domibusConnectorEvidenceDao.save(any(PDomibusConnectorEvidence.class)))
               .thenAnswer(new Answer<PDomibusConnectorEvidence>() {
                   @Override
                   public PDomibusConnectorEvidence answer(InvocationOnMock invocation)
                       throws Throwable {
                       PDomibusConnectorEvidence evidence = invocation.getArgument(0);
                       assertThat(evidence.getDeliveredToGateway()).isNull();
                       assertThat(evidence.getDeliveredToBackend()).isNull();
                       assertThat(evidence.getType()).isEqualTo(
                           eu.domibus.connector.persistence.model.enums.EvidenceType.DELIVERY);
                       assertThat(evidence.getBusinessMessage()).isNotNull();
                       assertThat(evidence.getEvidence()).isNull();
                       return evidence;
                   }
               });
        Mockito.verify(this.domibusConnectorEvidenceDao, Mockito.times(1))
               .save(any(PDomibusConnectorEvidence.class));
    }

    @Test
    void testPersistEvidenceForMessageIntoDatabase() {
        DomibusConnectorMessage message =
            DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();

        PDomibusConnectorMessage dbMessage =
            PersistenceEntityCreator.createSimpleDomibusConnectorMessage();

        byte[] evidence = "EVIDENCE1".getBytes();

        Mockito.when(this.domibusConnectorMessageDao.findOneByConnectorMessageId(eq("msgid")))
               .thenReturn(Optional.of(dbMessage));

        Mockito.when(this.domibusConnectorEvidenceDao.save(any(PDomibusConnectorEvidence.class)))
               .thenAnswer((Answer<PDomibusConnectorEvidence>) invocation -> {
                   PDomibusConnectorEvidence evidence1 = invocation.getArgument(0);
                   assertThat(evidence1.getDeliveredToGateway()).isNull();
                   assertThat(evidence1.getDeliveredToBackend()).isNull();
                   assertThat(evidence1.getType()).isEqualTo(
                       EvidenceType.DELIVERY);
                   assertThat(evidence1.getBusinessMessage()).isNotNull();
                   assertThat(evidence1.getEvidence()).isEqualTo("EVIDENCE1");
                   return evidence1;
               });
        Mockito.verify(this.domibusConnectorEvidenceDao, Mockito.times(1))
               .save(any(PDomibusConnectorEvidence.class));
    }
}
