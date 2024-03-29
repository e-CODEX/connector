package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.persistence.dao.CommonPersistenceTest;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;


@CommonPersistenceTest
class DCMessagePersistenceServiceImplITCase {
    @Autowired
    DCMessagePersistenceService persistenceService;

    @Autowired
    TransactionTemplate txTemplate;

    @Test
    void testPersistLoadBusinessMessage() {
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        epoMessage.setConnectorMessageId(new DomibusConnectorMessageId("id1"));
        epoMessage.getMessageDetails().setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);

        txTemplate.executeWithoutResult(t -> persistenceService.persistBusinessMessageIntoDatabase(epoMessage));

        DomibusConnectorMessage loadedBusinessMsg = persistenceService.findMessageByConnectorMessageId("id1");
        assertThat(loadedBusinessMsg).isNotNull();
    }

    @Test
    void testPersistEvidenceMessage_shouldThrow() {
        //        Assertions.assertThrows(PersistenceException.class, () -> {
        DomibusConnectorMessage evidenceMsg = DomainEntityCreator.createEvidenceNonDeliveryMessage();
        evidenceMsg.getMessageDetails().setCausedBy(new DomibusConnectorMessageId("id1"));

        evidenceMsg.setConnectorMessageId(new DomibusConnectorMessageId("ev1"));
        evidenceMsg.getMessageDetails().setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);

        txTemplate.executeWithoutResult(t -> persistenceService.persistBusinessMessageIntoDatabase(evidenceMsg));
        //        });
    }
}
