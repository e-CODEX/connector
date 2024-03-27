package eu.domibus.connector.controller.test.util;

import eu.domibus.connector.controller.processor.util.ConfirmationCreatorService;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.evidences.DomibusConnectorEvidencesToolkit;
import eu.domibus.connector.persistence.dao.DomibusConnectorActionDao;
import eu.domibus.connector.persistence.service.DomibusConnectorEvidencePersistenceService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;


public class MockedCreateConfirmationMessageBuilderFactoryImplProvider {
    private ConfirmationCreatorService createConfirmationMessageBuilderFactory;

    @Mock
    private DomibusConnectorEvidencesToolkit evidencesToolkit;
    @Mock
    private DomibusConnectorEvidencePersistenceService evidencePersistenceService;
    @Mock
    private DomibusConnectorMessageIdGenerator messageIdGenerator;

    public MockedCreateConfirmationMessageBuilderFactoryImplProvider() {
        MockitoAnnotations.initMocks(this);

        DomibusConnectorActionDao actionDao = Mockito.mock(DomibusConnectorActionDao.class);
        //        Mockito.when(actionDao.findById(any(String.class))).thenAnswer(
        //                (Answer<Optional<PDomibusConnectorAction>>) invocation -> {
        //                    PDomibusConnectorAction a = new PDomibusConnectorAction();
        //                    a.setAction(invocation.getArgument(0));
        //                    a.setDocumentRequired(false);
        //                    return Optional.of(a);
        //                }
        //        );

        Mockito.when(evidencesToolkit.createEvidence(any(), any(), any(), any()))
               .thenReturn(DomainEntityCreator.createMessageDeliveryConfirmation());

        Mockito.when(messageIdGenerator.generateDomibusConnectorMessageId())
               .thenReturn(new DomibusConnectorMessageId(UUID.randomUUID() + "@mockedid"));
    }

    public DomibusConnectorEvidencesToolkit getMockedEvidencesToolkit() {
        return evidencesToolkit;
    }

    public DomibusConnectorEvidencePersistenceService getMockedEvidencePersistenceService() {
        return evidencePersistenceService;
    }

    public DomibusConnectorMessageIdGenerator getMockedMessageIdGenerator() {
        return messageIdGenerator;
    }

    public ConfirmationCreatorService getCreateConfirmationMessageBuilderFactory() {
        return createConfirmationMessageBuilderFactory;
    }
}
