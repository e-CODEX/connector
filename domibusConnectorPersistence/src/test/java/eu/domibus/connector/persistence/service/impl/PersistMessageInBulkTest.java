package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.persistence.dao.CommonPersistenceTest;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.service.TransportStepPersistenceService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.stream.IntStream;


/**
 * Test the persistence of multiple messages
 * in parallel
 */
@CommonPersistenceTest
class PersistMessageInBulkTest {
    @Autowired
    DataSource ds;

    @Autowired
    TransportStepPersistenceService transportStepPersistenceService;

    @Autowired
    DCMessagePersistenceServiceImpl messagePersistenceService;

    @Autowired
    DomibusConnectorMessageDao msgDao;

    @Test
    @Disabled("is broken and does test a deprecated function")
    void testBulkMessage() {
        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.setConnectorMessageId(new DomibusConnectorMessageId("msg2"));
        messagePersistenceService.persistMessageIntoDatabase(
                message,
                DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY
        );

        IntStream.range(0, 30).parallel().forEach(i -> {
            DomibusConnectorMessage message2 = DomainEntityCreator.createMessage();
            message2.setConnectorMessageId(new DomibusConnectorMessageId("msg2" + i));
            messagePersistenceService.persistMessageIntoDatabase(
                    message2,
                    DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY
            );
        });
    }
}
